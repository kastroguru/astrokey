package eu.kastroguru.astrodiary.data.repository

import eu.kastroguru.astrodiary.data.ChartDisplayPrefs
import eu.kastroguru.astrodiary.data.db.dao.HistoryEventDao
import eu.kastroguru.astrodiary.data.db.entity.HistoryEventEntity
import eu.kastroguru.astrodiary.data.network.GeocodingApi
import eu.kastroguru.astrodiary.domain.calculator.AstroCalculator
import eu.kastroguru.astrodiary.domain.model.AstroData
import eu.kastroguru.astrodiary.domain.model.PlanetPosition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryEventRepository @Inject constructor(
    private val dao: HistoryEventDao,
    private val astroCalculator: AstroCalculator,
    private val chartDisplayPrefs: ChartDisplayPrefs
) {

    fun getAll(): Flow<List<HistoryEventEntity>> = dao.getAll()
    fun getByTag(tag: String): Flow<List<HistoryEventEntity>> = dao.getByTag(tag)
    fun getBySunSign(signId: Int): Flow<List<HistoryEventEntity>> = dao.getBySunSign(signId)
    fun getByMoonSign(signId: Int): Flow<List<HistoryEventEntity>> = dao.getByMoonSign(signId)
    fun getGlobalOnly(): Flow<List<HistoryEventEntity>> = dao.getGlobalOnly()
    fun getByYear(year: Int): Flow<List<HistoryEventEntity>> = dao.getByYear(year)
    fun search(query: String): Flow<List<HistoryEventEntity>> = dao.search(query)

    suspend fun getById(id: Long): HistoryEventEntity? = dao.getById(id)
    suspend fun insert(entity: HistoryEventEntity) = dao.insert(entity)
    suspend fun delete(entity: HistoryEventEntity) = dao.delete(entity)
    suspend fun deleteById(id: Long) = dao.deleteById(id)

    suspend fun deleteTag(tag: String, deleteEvents: Boolean) {
        val candidates = dao.getCandidatesByTag(tag)
        val withTag = candidates.filter { e ->
            e.tags.split(",").map { it.trim() }.contains(tag)
        }
        if (deleteEvents) {
            withTag.forEach { dao.deleteById(it.id) }
        } else {
            withTag.forEach { e ->
                val newTags = e.tags.split(",")
                    .map { it.trim() }
                    .filter { it.isNotBlank() && it != tag }
                    .joinToString(", ")
                dao.update(e.copy(tags = newTags))
            }
        }
    }

    suspend fun getAllTags(): List<String> =
        dao.getAllTagStrings()
            .flatMap { it.split(",") }
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()

    suspend fun getAllYears(): List<Int> = dao.getAllYears()

    suspend fun calculateAndSave(
        name: String,
        year: Int, month: Int, day: Int,
        hour: Int, minutes: Int,
        city: String, country: String,
        timezoneId: String,
        latitude: Double,
        longitude: Double,
        description: String,
        tags: String,
        isGlobal: Boolean,
        editId: Long = 0L
    ): Result<Long> {
        return try {
            val tz = TimeZone.getTimeZone(timezoneId)
            val cal = Calendar.getInstance(tz).apply {
                set(year, month - 1, day, hour, minutes, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                timeInMillis = cal.timeInMillis
            }

            val yearUtc = utcCal.get(Calendar.YEAR)
            val monthUtc = utcCal.get(Calendar.MONTH) + 1
            val dayUtc = utcCal.get(Calendar.DAY_OF_MONTH)
            val hourUtc = utcCal.get(Calendar.HOUR_OF_DAY)
            val minutesUtc = utcCal.get(Calendar.MINUTE)
            val hourDecimalUtc = hourUtc + minutesUtc / 60.0

            val astroData = withContext(Dispatchers.Default) {
                astroCalculator.calculate(yearUtc, monthUtc, dayUtc, hourDecimalUtc, latitude, longitude,
                    chartDisplayPrefs.houseSystemChar)
            }

            val entity = buildEntity(
                name = name,
                year = year, month = month, day = day,
                hour = hour, minutes = minutes,
                yearUtc = yearUtc, monthUtc = monthUtc, dayUtc = dayUtc,
                hourUtc = hourUtc, minutesUtc = minutesUtc,
                city = city, country = country,
                latitude = latitude, longitude = longitude,
                timezone = timezoneId,
                astroData = astroData,
                description = description,
                tags = tags,
                isGlobal = isGlobal
            )

            val id = if (editId > 0L) {
                dao.update(entity.copy(id = editId))
                editId
            } else {
                dao.insert(entity)
            }
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(Exception("${e.javaClass.simpleName}: ${e.message ?: "no detail"}", e))
        }
    }

    private fun buildEntity(
        name: String,
        year: Int, month: Int, day: Int,
        hour: Int, minutes: Int,
        yearUtc: Int, monthUtc: Int, dayUtc: Int,
        hourUtc: Int, minutesUtc: Int,
        city: String, country: String,
        latitude: Double, longitude: Double,
        timezone: String,
        astroData: AstroData,
        description: String,
        tags: String,
        isGlobal: Boolean
    ): HistoryEventEntity {
        val planets = astroData.planets
        val cusps = astroData.cusps

        val default = PlanetPosition(0.0, 1, 0, 0, 0)
        fun p(key: String) = planets[key] ?: default

        return HistoryEventEntity(
            name = name,
            year = year, month = month, day = day,
            hour = hour, minutes = minutes,
            yearUtc = yearUtc, monthUtc = monthUtc, dayUtc = dayUtc,
            hourUtc = hourUtc, minutesUtc = minutesUtc,
            city = city, country = country,
            latitude = latitude, longitude = longitude, timezone = timezone,
            sunD = p("sun").absoluteDegree, sunS = p("sun").sign,
            sunDeg = p("sun").degreeInSign, sunM = p("sun").minutes, sunH = p("sun").house,
            moonD = p("moon").absoluteDegree, moonS = p("moon").sign,
            moonDeg = p("moon").degreeInSign, moonM = p("moon").minutes, moonH = p("moon").house,
            mercuryD = p("mercury").absoluteDegree, mercuryS = p("mercury").sign,
            mercuryDeg = p("mercury").degreeInSign, mercuryM = p("mercury").minutes, mercuryH = p("mercury").house,
            venusD = p("venus").absoluteDegree, venusS = p("venus").sign,
            venusDeg = p("venus").degreeInSign, venusM = p("venus").minutes, venusH = p("venus").house,
            marsD = p("mars").absoluteDegree, marsS = p("mars").sign,
            marsDeg = p("mars").degreeInSign, marsM = p("mars").minutes, marsH = p("mars").house,
            jupiterD = p("jupiter").absoluteDegree, jupiterS = p("jupiter").sign,
            jupiterDeg = p("jupiter").degreeInSign, jupiterM = p("jupiter").minutes, jupiterH = p("jupiter").house,
            saturnD = p("saturn").absoluteDegree, saturnS = p("saturn").sign,
            saturnDeg = p("saturn").degreeInSign, saturnM = p("saturn").minutes, saturnH = p("saturn").house,
            uranusD = p("uranus").absoluteDegree, uranusS = p("uranus").sign,
            uranusDeg = p("uranus").degreeInSign, uranusM = p("uranus").minutes, uranusH = p("uranus").house,
            neptuneD = p("neptune").absoluteDegree, neptuneS = p("neptune").sign,
            neptuneDeg = p("neptune").degreeInSign, neptuneM = p("neptune").minutes, neptuneH = p("neptune").house,
            plutoD = p("pluto").absoluteDegree, plutoS = p("pluto").sign,
            plutoDeg = p("pluto").degreeInSign, plutoM = p("pluto").minutes, plutoH = p("pluto").house,
            chironD = p("chiron").absoluteDegree, chironS = p("chiron").sign,
            chironDeg = p("chiron").degreeInSign, chironM = p("chiron").minutes, chironH = p("chiron").house,
            rahuD = p("rahu").absoluteDegree, rahuS = p("rahu").sign,
            rahuDeg = p("rahu").degreeInSign, rahuM = p("rahu").minutes, rahuH = p("rahu").house,
            lilithD = p("lilith").absoluteDegree, lilithS = p("lilith").sign,
            lilithDeg = p("lilith").degreeInSign, lilithM = p("lilith").minutes, lilithH = p("lilith").house,
            cusp1 = cusps[0], cusp2 = cusps[1], cusp3 = cusps[2], cusp4 = cusps[3],
            cusp5 = cusps[4], cusp6 = cusps[5], cusp7 = cusps[6], cusp8 = cusps[7],
            cusp9 = cusps[8], cusp10 = cusps[9], cusp11 = cusps[10], cusp12 = cusps[11],
            description = description,
            tags = tags,
            isGlobal = isGlobal
        )
    }
}
