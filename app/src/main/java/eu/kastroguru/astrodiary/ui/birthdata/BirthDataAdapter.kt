package eu.kastroguru.astrodiary.ui.birthdata

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import eu.kastroguru.astrodiary.data.db.entity.BirthDataEntity
import eu.kastroguru.astrodiary.databinding.ItemBirthDataBinding
import eu.kastroguru.astrodiary.domain.model.Element
import eu.kastroguru.astrodiary.domain.model.ZodiacSign
import eu.kastroguru.astrodiary.ui.chart.localizedName

class BirthDataAdapter(
    private val onClick: (BirthDataEntity) -> Unit,
    private val onLongClick: (BirthDataEntity) -> Unit
) : ListAdapter<BirthDataEntity, BirthDataAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBirthDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    inner class ViewHolder(private val b: ItemBirthDataBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(entity: BirthDataEntity) {
            b.textName.text = entity.name
            b.textDate.text = "%04d-%02d-%02d  %02d:%02d".format(
                entity.year, entity.month, entity.day, entity.hour, entity.minutes
            )
            b.textLocation.text = if (entity.country.isNotBlank()) "${entity.city}, ${entity.country}" else entity.city

            val sunSign = try { ZodiacSign.fromId(entity.sunS) } catch (e: Exception) { null }
            val moonSign = try { ZodiacSign.fromId(entity.moonS) } catch (e: Exception) { null }

            // Badge: element color + sign glyph
            b.textSignGlyph.text = sunSign?.symbol ?: "★"
            val elementColor = elementColor(sunSign?.element)
            val bg = b.viewSignBadge.background as? GradientDrawable
                ?: GradientDrawable().also { b.viewSignBadge.background = it }
            bg.shape = GradientDrawable.OVAL
            bg.setColor(elementColor and 0x00FFFFFF or 0x55000000)
            bg.setStroke(2, elementColor)

            // Text: Sun in Aries · Moon in Taurus
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
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<BirthDataEntity>() {
            override fun areItemsTheSame(a: BirthDataEntity, b: BirthDataEntity) = a.id == b.id
            override fun areContentsTheSame(a: BirthDataEntity, b: BirthDataEntity) = a == b
        }
    }
}
