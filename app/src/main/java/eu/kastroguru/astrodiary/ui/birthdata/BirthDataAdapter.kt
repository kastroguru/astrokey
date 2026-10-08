package eu.kastroguru.astrodiary.ui.birthdata

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import eu.kastroguru.astrodiary.R
import eu.kastroguru.astrodiary.data.db.entity.BirthDataEntity
import eu.kastroguru.astrodiary.databinding.ItemBirthDataBinding
import eu.kastroguru.astrodiary.domain.model.ZodiacSign
import eu.kastroguru.astrodiary.ui.chart.localizedName
import eu.kastroguru.astrodiary.ui.chart.SignBadgeDrawable
import eu.kastroguru.astrodiary.ui.chart.withSignBadges
import eu.kastroguru.astrodiary.ui.common.SwipeRevealLayout

class BirthDataAdapter(
    private val onClick: (BirthDataEntity) -> Unit,
    private val onEdit: (BirthDataEntity) -> Unit,
    private val onDelete: (BirthDataEntity) -> Unit
) : ListAdapter<BirthDataEntity, BirthDataAdapter.ViewHolder>(DIFF_CALLBACK) {

    /** Only one row stands open at a time. */
    private var openRow: SwipeRevealLayout? = null

    fun closeOpenRow() {
        openRow?.close()
        openRow = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBirthDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    inner class ViewHolder(private val b: ItemBirthDataBinding) : RecyclerView.ViewHolder(b.root) {
        private val row: SwipeRevealLayout = b.root
        private var editActionId = View.NO_ID
        private var deleteActionId = View.NO_ID

        init {
            row.onOpened = { opened ->
                if (openRow !== opened) openRow?.close()
                openRow = opened
            }
        }

        fun bind(entity: BirthDataEntity) {
            row.closeNow()
            if (openRow === row) openRow = null
            b.textName.text = entity.name
            b.textDate.text = "%04d-%02d-%02d  %02d:%02d".format(
                entity.year, entity.month, entity.day, entity.hour, entity.minutes
            )
            b.textLocation.text = if (entity.country.isNotBlank()) "${entity.city}, ${entity.country}" else entity.city

            val sunSign = try { ZodiacSign.fromId(entity.sunS) } catch (e: Exception) { null }
            val moonSign = try { ZodiacSign.fromId(entity.moonS) } catch (e: Exception) { null }

            // Badge: our sun-sign badge; a neutral disc with a star when the sign is unknown
            if (sunSign != null) {
                b.viewSignBadge.background = SignBadgeDrawable(sunSign)
                b.textSignGlyph.text = ""
            } else {
                b.viewSignBadge.background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(Color.parseColor("#5598A0BA")); setStroke(2, Color.parseColor("#98A0BA"))
                }
                b.textSignGlyph.text = "★"
            }

            // Text: Sun in Aries · Moon in Taurus
            val signText = buildString {
                sunSign?.let { append("☉ ${it.symbol} ${it.localizedName(b.root.context)}") }
                moonSign?.let { append("  ☽ ${it.symbol} ${it.localizedName(b.root.context)}") }
            }
            b.textSunSign.text = withSignBadges(signText)

            b.card.setOnClickListener { onClick(entity) }
            b.buttonEdit.setOnClickListener { closeOpenRow(); onEdit(entity) }
            b.buttonDelete.setOnClickListener { closeOpenRow(); onDelete(entity) }

            // The same two actions without the swipe, for TalkBack and switch access.
            val ctx = b.root.context
            ViewCompat.removeAccessibilityAction(b.card, editActionId)
            ViewCompat.removeAccessibilityAction(b.card, deleteActionId)
            editActionId = ViewCompat.addAccessibilityAction(b.card, ctx.getString(R.string.edit_birth_data)) { _, _ ->
                onEdit(entity); true
            }
            deleteActionId = ViewCompat.addAccessibilityAction(b.card, ctx.getString(R.string.delete)) { _, _ ->
                onDelete(entity); true
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<BirthDataEntity>() {
            override fun areItemsTheSame(a: BirthDataEntity, b: BirthDataEntity) = a.id == b.id
            override fun areContentsTheSame(a: BirthDataEntity, b: BirthDataEntity) = a == b
        }
    }
}
