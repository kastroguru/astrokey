package eu.kastroguru.astrodiary.data.aaf

data class AafEntry(
    val name: String,
    val day: Int,
    val month: Int,
    val year: Int,
    val hour: Int,
    val minute: Int,
    val timezoneId: String,
    val latitude: Double,
    val longitude: Double,
    val city: String,
    val country: String
)

/**
 * Parses Astrodienst AAF export format.
 *
 * Uses regex on the raw text instead of line-by-line splitting, so it works
 * regardless of newline encoding (\n, \r\n, \r) or clipboard stripping.
 *
 * #A93: LastName,FirstName,gender,DD.MM.YYYY,HH:MM,City,Country
 * #B93: JulianDay,LatSpec,LonSpec,TZheSpec,DST
 *
 * Lat/lon: 43n07 = 43°7'N, 0w10 = 0°10'W, 27s28 = 27°28'S
 * Timezone: 2he00 = UTC+2  (DST flag adds +1h on top)
 */
object AafParser {

    // Grab everything after #A93: / #B93: up to the next # (or end of string)
    private val A93_RE = Regex("""#A93:([^#]+)""")
    private val B93_RE = Regex("""#B93:([^#]+)""")

    private val DATE_RE = Regex("""(\d{1,2})\.(\d{1,2})\.(\d{4})""")
    private val TIME_RE = Regex("""(\d{1,2}):(\d{2})""")
    private val LAT_N   = Regex("""(\d+)n(\d+)""", RegexOption.IGNORE_CASE)
    private val LAT_S   = Regex("""(\d+)s(\d+)""", RegexOption.IGNORE_CASE)
    private val LON_E   = Regex("""(\d+)e(\d+)""", RegexOption.IGNORE_CASE)
    private val LON_W   = Regex("""(\d+)w(\d+)""", RegexOption.IGNORE_CASE)
    private val TZ_RE   = Regex("""(\d+)h([ew])(\d*)""", RegexOption.IGNORE_CASE)

    fun parse(text: String): List<AafEntry> {
        val a93List = A93_RE.findAll(text).toList()
        val b93List = B93_RE.findAll(text).toList()

        return a93List.mapNotNull { a93 ->
            val a93Content = a93.groupValues[1].trim()
            val b93Content = b93List
                .firstOrNull { it.range.first > a93.range.last }
                ?.groupValues?.get(1)?.trim()
            parseEntry(a93Content, b93Content)
        }
    }

    private fun parseEntry(a93: String, b93: String?): AafEntry? {
        // Strip any embedded newlines/whitespace runs left over from the raw capture
        val clean = a93.replace(Regex("""[\r\n\t]+"""), " ").trim()
        val af = clean.split(",").map { it.trim() }
        if (af.size < 7) return null

        val lastName  = af[0]
        val firstName = af[1]
        val name = if (lastName == "*" || lastName.isBlank()) firstName else "$firstName $lastName"

        val dateMatch = DATE_RE.find(af[3]) ?: return null
        val day   = dateMatch.groupValues[1].toInt()
        val month = dateMatch.groupValues[2].toInt()
        val year  = dateMatch.groupValues[3].toInt()

        val timeMatch = TIME_RE.find(af[4]) ?: return null
        val hour   = timeMatch.groupValues[1].toInt()
        val minute = timeMatch.groupValues[2].toInt()

        val city    = af[5]
        val country = af.getOrElse(6) { "" }

        // #B93: JulianDay,LatSpec,LonSpec,TZheSpec,DST
        var lat = 0.0; var lon = 0.0; var timezoneId = "GMT+00:00"
        if (b93 != null) {
            val bc = b93.replace(Regex("""[\r\n\t]+"""), " ").trim()
            val bf = bc.split(",").map { it.trim() }
            if (bf.size >= 4) {
                lat = parseCoord(bf[1], isLat = true)  ?: 0.0
                lon = parseCoord(bf[2], isLat = false) ?: 0.0
                val dst = bf.getOrElse(4) { "0" }.toIntOrNull() ?: 0
                timezoneId = parseTz(bf[3], dst)
            }
        }

        return AafEntry(name, day, month, year, hour, minute, timezoneId, lat, lon, city, country)
    }

    private fun parseCoord(field: String, isLat: Boolean): Double? {
        return if (isLat) {
            LAT_N.find(field)?.let { m ->
                m.groupValues[1].toDouble() + m.groupValues[2].toDouble() / 60.0
            } ?: LAT_S.find(field)?.let { m ->
                -(m.groupValues[1].toDouble() + m.groupValues[2].toDouble() / 60.0)
            }
        } else {
            LON_E.find(field)?.let { m ->
                m.groupValues[1].toDouble() + m.groupValues[2].toDouble() / 60.0
            } ?: LON_W.find(field)?.let { m ->
                -(m.groupValues[1].toDouble() + m.groupValues[2].toDouble() / 60.0)
            }
        }
    }

    // DST adds 1h to east offsets, subtracts 1h from west offsets
    private fun parseTz(field: String, dst: Int): String {
        val m = TZ_RE.find(field) ?: return "GMT+00:00"
        val hBase = m.groupValues[1].toInt()
        val dir   = m.groupValues[2].lowercase()
        val min   = m.groupValues[3].toIntOrNull() ?: 0
        val h     = if (dir == "e") hBase + dst else hBase - dst
        val sign  = if (dir == "e") "+" else "-"
        return "GMT${sign}%02d:%02d".format(h, min)
    }
}
