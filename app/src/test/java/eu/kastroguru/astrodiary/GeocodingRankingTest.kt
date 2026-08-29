package eu.kastroguru.astrodiary

import eu.kastroguru.astrodiary.data.network.NominatimAddress
import eu.kastroguru.astrodiary.data.network.NominatimResult
import eu.kastroguru.astrodiary.data.network.countryFirst
import eu.kastroguru.astrodiary.data.network.dedupePlaces
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * The city picker used to offer the same place twice and put the user's own country last — a search
 * for "Sofia" on a Bulgarian phone offered Madagascar first and Bulgaria second.
 */
class GeocodingRankingTest {

    private fun result(name: String, lat: String, lon: String, cc: String?) =
        NominatimResult(lat, lon, name, cc?.let { NominatimAddress(country = it, countryCode = it) })

    @Test
    fun collapsesRepeatedPlaces() {
        val raw = listOf(
            result("Sofia, Mahajanga, Madagascar", "-15.35", "47.21", "mg"),
            result("Sofia, Mahajanga, Madagascar", "-15.35", "47.21", "mg"),   // same entry again
            result("София, Столична, България", "42.6977", "23.3219", "bg"),
        )
        val out = raw.dedupePlaces()
        assertEquals(2, out.size)
        assertEquals("Sofia, Mahajanga, Madagascar", out[0].displayName)
        assertEquals("София, Столична, България", out[1].displayName)
    }

    @Test
    fun collapsesTwoObjectsForOneTown() {
        // Different OSM objects, different names, same spot (within the ~1 km dedupe grid).
        val raw = listOf(
            result("Sofia city centre", "42.6977", "23.3219", "bg"),
            result("Sofia, Sredets", "42.6981", "23.3225", "bg"),
        )
        assertEquals(1, raw.dedupePlaces().size)
    }

    @Test
    fun devicesCountryComesFirstAndKeepsTheRestInOrder() {
        val raw = listOf(
            result("Sofia, Madagascar", "-15.35", "47.21", "mg"),
            result("Sofia, Moldova", "46.78", "28.02", "md"),
            result("София, България", "42.6977", "23.3219", "bg"),
        )
        val out = raw.countryFirst("BG")
        assertEquals("София, България", out[0].displayName)
        assertEquals("Sofia, Madagascar", out[1].displayName)   // original relative order kept
        assertEquals("Sofia, Moldova", out[2].displayName)
    }

    @Test
    fun unknownDeviceCountryLeavesTheOrderAlone() {
        val raw = listOf(
            result("Sofia, Madagascar", "-15.35", "47.21", "mg"),
            result("София, България", "42.6977", "23.3219", "bg"),
        )
        assertEquals(raw.map { it.displayName }, raw.countryFirst(null).map { it.displayName })
        assertEquals(raw.map { it.displayName }, raw.countryFirst("").map { it.displayName })
    }
}
