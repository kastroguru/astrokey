package eu.kastroguru.astrodiary.ui.events

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import eu.kastroguru.astrodiary.data.db.entity.HistoryEventEntity
import eu.kastroguru.astrodiary.databinding.ItemEventBinding
import eu.kastroguru.astrodiary.domain.model.Element
import eu.kastroguru.astrodiary.domain.model.ZodiacSign
import eu.kastroguru.astrodiary.ui.chart.localizedName

class EventAdapter(
    private val onClick: (HistoryEventEntity) -> Unit,
    private val onLongClick: (HistoryEventEntity) -> Unit
) : ListAdapter<HistoryEventEntity, EventAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    inner class ViewHolder(private val b: ItemEventBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(entity: HistoryEventEntity) {
            b.textName.text = entity.name
            b.textDate.text = "%04d-%02d-%02d  %02d:%02d".format(
                entity.year, entity.month, entity.day, entity.hour, entity.minutes
            )
            b.textDescription.text = entity.description.take(100).let {
                if (entity.description.length > 100) "$it…" else it
            }
            b.textDescription.visibility = if (entity.description.isBlank()) View.GONE else View.VISIBLE
            b.textTags.text = if (entity.tags.isNotBlank()) "# ${entity.tags.replace(",", " · #")}" else ""
            b.textTags.visibility = if (entity.tags.isBlank()) View.GONE else View.VISIBLE
            b.textGlobal.visibility = if (entity.isGlobal) View.VISIBLE else View.GONE

            val sunSign = try { ZodiacSign.fromId(entity.sunS) } catch (e: Exception) { null }
            val moonSign = try { ZodiacSign.fromId(entity.moonS) } catch (e: Exception) { null }

            b.textSignGlyph.text = sunSign?.symbol ?: "☆"
            val elementColor = elementColor(sunSign?.element)
            val bg = b.viewSignBadge.background as? GradientDrawable
                ?: GradientDrawable().also { b.viewSignBadge.background = it }
            bg.shape = GradientDrawable.OVAL
            bg.setColor(elementColor and 0x00FFFFFF or 0x55000000)
            bg.setStroke(2, elementColor)

            val signText = buildString {
                sunSign?.let { append("☉ ${it.symbol} ${it.localizedName(b.root.context)}") }
                moonSign?.let { append("  ☽ ${it.symbol} ${it.localizedName(b.root.context)}") }
            }
            b.textSunSign.text = signText

            b.root.setOnClickListener { onClick(entity) }
            b.root.setOnLongClickListener { onLongClick(entity); true }
        }
    }

    private fun elementColor(element: Element?): Int = when (element) {
        Element.FIRE  -> Color.parseColor("#D94F4F")
        Element.EARTH -> Color.parseColor("#5A9A6A")
        Element.AIR   -> Color.parseColor("#4A8EC2")
        Element.WATER -> Color.parseColor("#7B6FC9")
        null          -> Color.parseColor("#98A0BA")
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<HistoryEventEntity>() {
            override fun areItemsTheSame(a: HistoryEventEntity, b: HistoryEventEntity) = a.id == b.id
            override fun areContentsTheSame(a: HistoryEventEntity, b: HistoryEventEntity) = a == b
        }
    }
}
