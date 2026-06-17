package eu.kastroguru.astrodiary.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_events")
data class HistoryEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val year: Int, val month: Int, val day: Int,
    val hour: Int, val minutes: Int,
    val yearUtc: Int, val monthUtc: Int, val dayUtc: Int, val hourUtc: Int, val minutesUtc: Int,
    val city: String, val country: String,
    val latitude: Double, val longitude: Double, val timezone: String,
    // Sun
    val sunD: Double, val sunS: Int, val sunDeg: Int, val sunM: Int, val sunH: Int,
    // Moon
    val moonD: Double, val moonS: Int, val moonDeg: Int, val moonM: Int, val moonH: Int,
    // Mercury
    val mercuryD: Double, val mercuryS: Int, val mercuryDeg: Int, val mercuryM: Int, val mercuryH: Int,
    // Venus
    val venusD: Double, val venusS: Int, val venusDeg: Int, val venusM: Int, val venusH: Int,
    // Mars
    val marsD: Double, val marsS: Int, val marsDeg: Int, val marsM: Int, val marsH: Int,
    // Jupiter
    val jupiterD: Double, val jupiterS: Int, val jupiterDeg: Int, val jupiterM: Int, val jupiterH: Int,
    // Saturn
    val saturnD: Double, val saturnS: Int, val saturnDeg: Int, val saturnM: Int, val saturnH: Int,
    // Uranus
    val uranusD: Double, val uranusS: Int, val uranusDeg: Int, val uranusM: Int, val uranusH: Int,
    // Neptune
    val neptuneD: Double, val neptuneS: Int, val neptuneDeg: Int, val neptuneM: Int, val neptuneH: Int,
    // Pluto
    val plutoD: Double, val plutoS: Int, val plutoDeg: Int, val plutoM: Int, val plutoH: Int,
    // Chiron
    val chironD: Double, val chironS: Int, val chironDeg: Int, val chironM: Int, val chironH: Int,
    // Rahu (True Node)
    val rahuD: Double, val rahuS: Int, val rahuDeg: Int, val rahuM: Int, val rahuH: Int,
    // Lilith (Mean Apogee)
    val lilithD: Double, val lilithS: Int, val lilithDeg: Int, val lilithM: Int, val lilithH: Int,
    // House cusps (12 cusps, absolute degree 0-360)
    val cusp1: Double, val cusp2: Double, val cusp3: Double, val cusp4: Double,
    val cusp5: Double, val cusp6: Double, val cusp7: Double, val cusp8: Double,
    val cusp9: Double, val cusp10: Double, val cusp11: Double, val cusp12: Double,
    // Event-specific fields
    val description: String = "",
    val tags: String = "",  // comma-separated tag names
    val isGlobal: Boolean = false,
    // Optional link to the natal chart (BirthDataEntity.id) this event belongs to; null = unassigned.
    val personId: Long? = null,
    // Optional user-picked image, copied into internal storage; null = use the generated thumbnail.
    val imagePath: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
