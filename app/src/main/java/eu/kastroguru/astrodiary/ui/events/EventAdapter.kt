package eu.kastroguru.astrodiary.ui.events

import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import eu.kastroguru.astrodiary.data.db.entity.HistoryEventEntity
import eu.kastroguru.astrodiary.databinding.ItemEventThumbnailBinding
import eu.kastroguru.astrodiary.domain.EventAspects
import eu.kastroguru.astrodiary.domain.model.Planet
import eu.kastroguru.astrodiary.domain.model.ZodiacSign
import eu.kastroguru.astrodiary.ui.chart.PlanetColors
import java.io.File

/**
 * Instagram-style gallery of events: a portrait thumbnail per event. If the event has an uploaded
 * image we show it; otherwise we draw the generated thumbnail of its most-exact aspect.
 */
class EventAdapter(
    private val onClick: (HistoryEventEntity) -> Unit,
    private val onLongClick: (HistoryEventEntity) -> Unit
) : ListAdapter<HistoryEventEntity, EventAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEventThumbnailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    inner class ViewHolder(private val b: ItemEventThumbnailBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(entity: HistoryEventEntity) {
            // The custom view draws the photo (if any) as its base and overlays the info band; when
            // there's no photo it draws the most-exact-aspect art instead.
            val file = entity.imagePath?.let { File(it) }
            b.thumbView.bitmap = if (file != null && file.exists()) decodeScaled(file, TARGET_PX) else null
            b.thumbView.data = buildThumbData(entity)
            b.root.setOnClickListener { onClick(entity) }
            b.root.setOnLongClickListener { onLongClick(entity); true }
        }
    }

    private fun buildThumbData(entity: HistoryEventEntity): EventThumbnailView.Data {
        val asp = EventAspects.mostExact(entity)
        val a = asp?.pointA ?: "sun"
        val c = asp?.pointB ?: "moon"
        val datetime = "%04d-%02d-%02d  %02d:%02d".format(entity.year, entity.month, entity.day, entity.hour, entity.minutes)
        fun signGlyph(id: Int) = try { ZodiacSign.fromId(id).symbol } catch (e: Exception) { "" }
        return EventThumbnailView.Data(
            glyphA = glyphOf(a), colorA = PlanetColors.of(a),
            glyphB = glyphOf(c), colorB = PlanetColors.of(c),
            aspectSymbol = aspectSymbol(asp?.angle ?: 0), aspectColor = aspectColor(asp?.angle ?: 0),
            sunSign = signGlyph(entity.sunS), sunColor = PlanetColors.of("sun"),
            moonSign = signGlyph(entity.moonS), moonColor = PlanetColors.of("moon"),
            city = entity.city, datetime = datetime,
        )
    }

    private fun glyphOf(key: String): String = when (key) {
        "asc" -> "Asc"
        "mc"  -> "MC"
        else  -> Planet.values().find { it.key == key }?.glyph ?: "•"
    }

    private fun aspectSymbol(angle: Int): String = when (angle) {
        0 -> "☌"; 60 -> "✶"; 90 -> "□"; 120 -> "△"; 150 -> "⚻"; 180 -> "☍"; else -> "$angle°"
    }

    private fun aspectColor(angle: Int): Int = when (angle) {
        0 -> Color.parseColor("#8E8CEB"); 60 -> Color.parseColor("#5A9A6A")
        90 -> Color.parseColor("#E53935"); 120 -> Color.parseColor("#2196F3")
        150 -> Color.parseColor("#FF9800"); 180 -> Color.parseColor("#9C27B0")
        else -> Color.parseColor("#6D7691")
    }

    /** Decodes a downsampled bitmap (~[reqPx] on the long edge) so the grid never loads full photos. */
    private fun decodeScaled(file: File, reqPx: Int) = try {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(file.absolutePath, bounds)
        var sample = 1
        val longest = maxOf(bounds.outWidth, bounds.outHeight)
        while (longest / sample > reqPx * 2) sample *= 2
        BitmapFactory.decodeFile(file.absolutePath, BitmapFactory.Options().apply { inSampleSize = sample })
    } catch (e: Exception) {
        null
    }

    companion object {
        private const val TARGET_PX = 400

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<HistoryEventEntity>() {
            override fun areItemsTheSame(a: HistoryEventEntity, b: HistoryEventEntity) = a.id == b.id
            override fun areContentsTheSame(a: HistoryEventEntity, b: HistoryEventEntity) = a == b
        }
    }
}
