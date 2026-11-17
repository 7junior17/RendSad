package com.rendsad.app.ui.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.rendsad.app.R
import com.rendsad.app.databinding.ItemTrainingEntryBinding
import com.rendsad.app.model.TrainingBadge
import com.rendsad.app.model.TrainingEntry

class TrainingEntryAdapter : ListAdapter<TrainingEntry, TrainingEntryAdapter.ViewHolder>(Diff) {

    class ViewHolder(val b: ItemTrainingEntryBinding) : RecyclerView.ViewHolder(b.root)

    object Diff : DiffUtil.ItemCallback<TrainingEntry>() {
        override fun areItemsTheSame(a: TrainingEntry, b: TrainingEntry) = a.dateText == b.dateText
        override fun areContentsTheSame(a: TrainingEntry, b: TrainingEntry) = a == b
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemTrainingEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val e = getItem(position)
        val b = holder.b
        val context = b.root.context
        fun color(id: Int) = ContextCompat.getColor(context, id)

        b.tvDate.text = e.dateText
        b.tvBadge.text = e.badgeText

        val (bg, fg) = when (e.badge) {
            TrainingBadge.COMPLETED -> R.color.primary_10 to R.color.primary
            TrainingBadge.MODERATE -> R.color.amber_container to R.color.on_amber_container
            TrainingBadge.PLAIN -> R.color.surface_container to R.color.on_surface_variant
        }
        b.badgeCard.setCardBackgroundColor(color(bg))
        b.tvBadge.setTextColor(color(fg))

        val badgeIcon = when (e.badge) {
            TrainingBadge.COMPLETED -> R.drawable.ic_check_circle
            TrainingBadge.MODERATE -> R.drawable.ic_bolt
            TrainingBadge.PLAIN -> null
        }
        if (badgeIcon != null) {
            b.ivBadge.setImageResource(badgeIcon)
            b.ivBadge.setColorFilter(color(fg))
            b.ivBadge.visibility = View.VISIBLE
        } else {
            b.ivBadge.visibility = View.GONE
        }

        b.chipsExercises.removeAllViews()
        val inflater = LayoutInflater.from(context)
        e.exercises.forEach { name ->
            val chip = inflater.inflate(R.layout.item_tag_chip, b.chipsExercises, false) as Chip
            chip.text = name
            b.chipsExercises.addView(chip)
        }

        b.footer.visibility = if (e.rpe != null || e.note != null) View.VISIBLE else View.GONE
        b.rpeRow.visibility = if (e.rpe != null) View.VISIBLE else View.GONE
        b.tvRpe.text = "RPE Carga: ${e.rpe}/10"
        b.tvNote.text = e.note.orEmpty()
        b.tvNote.setTextColor(color(if (e.noteIsGood) R.color.primary else R.color.on_surface_variant))
    }
}