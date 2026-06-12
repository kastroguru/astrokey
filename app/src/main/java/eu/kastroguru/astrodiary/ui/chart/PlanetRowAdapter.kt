package eu.kastroguru.astrodiary.ui.chart

import android.graphics.Color
import android.text.SpannableString
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.SuperscriptSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import eu.kastroguru.astrodiary.databinding.ItemPlanetRowBinding

class PlanetRowAdapter(
    private val showDignities: Boolean = false
) : ListAdapter<PlanetRow, PlanetRowAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPlanetRowBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), showDignities)
    }

    class ViewHolder(private val binding: ItemPlanetRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(row: PlanetRow, showDignities: Boolean) {
            val retro = row.retroStatus
            if (retro != null) {
                val retroColor = if (retro == "R") Color.parseColor("#EF5350") else Color.parseColor("#FFA726")
                val s = SpannableString("${row.glyph}$retro")
                val g = row.glyph.length
                s.setSpan(RelativeSizeSpan(0.55f),         g, s.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                s.setSpan(SuperscriptSpan(),               g, s.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                s.setSpan(ForegroundColorSpan(retroColor), g, s.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                binding.textGlyph.text = s
            } else {
                binding.textGlyph.text = row.glyph
            }
            binding.textPlanetName.text = row.planetName
            binding.textSign.text = "${row.signGlyph} ${row.signName}"
            binding.textDegree.text = "%d°%02d'".format(row.degreeInSign, row.minutes)
            binding.textHouse.text = if (row.house > 0) row.house.toString() else ""

            // Keep dignity column consistently VISIBLE or GONE across all rows so
            // weighted columns stay at the same x position in every row.
            if (showDignities) {
                binding.textDignity.visibility = android.view.View.VISIBLE
                val dignity = row.dignity
                binding.textDignity.text = dignity ?: ""
                binding.textDignity.setTextColor(when (dignity) {
                    "Dm" -> android.graphics.Color.parseColor("#66BB6A")
                    "Ex" -> android.graphics.Color.parseColor("#FFD700")
                    "Dt" -> android.graphics.Color.parseColor("#FFA726")
                    "Fl" -> android.graphics.Color.parseColor("#EF5350")
                    else -> android.graphics.Color.TRANSPARENT
                })
            } else {
                binding.textDignity.visibility = android.view.View.GONE
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PlanetRow>() {
            override fun areItemsTheSame(a: PlanetRow, b: PlanetRow) = a.planetName == b.planetName
            override fun areContentsTheSame(a: PlanetRow, b: PlanetRow) = a == b
        }
    }
}
