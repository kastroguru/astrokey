package eu.kastroguru.astrodiary.ui.transit

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import eu.kastroguru.astrodiary.R
import eu.kastroguru.astrodiary.databinding.ItemAspectBinding
import eu.kastroguru.astrodiary.domain.model.Planet

class AspectAdapter : ListAdapter<TransitAspect, AspectAdapter.VH>(DIFF) {

    var onItemClick: ((TransitAspect) -> Unit)? = null

    inner class VH(val binding: ItemAspectBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemAspectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val aspect = getItem(position)
        val b = holder.binding
        val ctx = b.root.context

        val tPlanet = Planet.values().find { it.key == aspect.transitPlanet }
        val nPlanet = Planet.values().find { it.key == aspect.natalPlanet }

        b.root.setOnClickListener { onItemClick?.invoke(aspect) }
        b.tvTransitPlanet.text = tPlanet?.glyph ?: aspect.transitPlanet
        b.tvAspectSymbol.text = aspectSymbol(aspect.exactDegree)
        b.tvAspectSymbol.setTextColor(aspectColor(aspect.exactDegree))
        b.tvNatalPlanet.text = nPlanet?.glyph ?: aspect.natalPlanet
        b.tvAspectName.text = aspectNameLocalized(ctx, aspect.aspectName)
        // Primary directions show the perfection date; transits show the orb.
        b.tvOrb.text = aspect.perfectionLabel ?: ctx.getString(R.string.orb_format, aspect.orb.toString())
        if (aspect.isApplying) {
            b.tvApplying.text = ctx.getString(R.string.applying)
            b.tvApplying.setTextColor(Color.parseColor("#5A9A6A"))
        } else {
            b.tvApplying.text = ctx.getString(R.string.separating)
            b.tvApplying.setTextColor(Color.parseColor("#98A0BA"))
        }
    }

    private fun aspectNameLocalized(ctx: android.content.Context, name: String): String = when (name) {
        "Conjunction" -> ctx.getString(R.string.aspect_conjunction)
        "Sextile"     -> ctx.getString(R.string.aspect_sextile)
        "Square"      -> ctx.getString(R.string.aspect_square)
        "Trine"       -> ctx.getString(R.string.aspect_trine)
        "Quincunx"    -> ctx.getString(R.string.aspect_quincunx)
        "Opposition"  -> ctx.getString(R.string.aspect_opposition)
        else          -> name
    }

    private fun aspectSymbol(deg: Int): String = when (deg) {
        0   -> "☌"
        60  -> "⚹"
        90  -> "□"
        120 -> "△"
        150 -> "⚻"
        180 -> "☍"
        else -> "$deg°"
    }

    private fun aspectColor(deg: Int): Int = when (deg) {
        0   -> Color.parseColor("#8E8CEB")
        60  -> Color.parseColor("#5A9A6A")
        90  -> Color.parseColor("#E53935")
        120 -> Color.parseColor("#2196F3")
        150 -> Color.parseColor("#FF9800")
        180 -> Color.parseColor("#9C27B0")
        else -> Color.parseColor("#3A4258")
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<TransitAspect>() {
            override fun areItemsTheSame(a: TransitAspect, b: TransitAspect) =
                a.transitPlanet == b.transitPlanet && a.natalPlanet == b.natalPlanet && a.exactDegree == b.exactDegree
            override fun areContentsTheSame(a: TransitAspect, b: TransitAspect) = a == b
        }
    }
}
