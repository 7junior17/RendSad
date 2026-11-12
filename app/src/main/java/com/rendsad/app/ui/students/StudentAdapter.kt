package com.rendsad.app.ui.students

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rendsad.app.R
import com.rendsad.app.databinding.ItemStudentBinding
import com.rendsad.app.model.Student
import com.rendsad.app.model.StudentStatus

class StudentAdapter : ListAdapter<Student, StudentAdapter.ViewHolder>(Diff) {

    class ViewHolder(val b: ItemStudentBinding) : RecyclerView.ViewHolder(b.root)

    object Diff : DiffUtil.ItemCallback<Student>() {
        override fun areItemsTheSame(a: Student, b: Student) = a.number == b.number && a.name == b.name
        override fun areContentsTheSame(a: Student, b: Student) = a == b
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemStudentBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val s = getItem(position)
        val b = holder.b
        fun color(id: Int) = ContextCompat.getColor(b.root.context, id)

        b.tvName.text = s.name
        b.tvInitials.text = s.name.split(" ").take(2).map { it.first() }.joinToString("").uppercase()
        b.tvLevel.text = s.level
        b.tvPosition.text = "${s.position} · #${s.number}"
        b.tvStatus.text = s.statusText

        val (avatarBg, avatarText) = when (s.status) {
            StudentStatus.OPTIMAL -> R.color.primary_fixed to R.color.on_primary_fixed
            StudentStatus.POSITIVE -> R.color.surface_container_high to R.color.on_surface_variant
            else -> R.color.secondary_fixed to R.color.on_secondary_fixed
        }
        b.avatarCard.setCardBackgroundColor(color(avatarBg))
        b.tvInitials.setTextColor(color(avatarText))

        b.ivStatus.setImageResource(
            when (s.status) {
                StudentStatus.INJURY -> R.drawable.ic_warning
                StudentStatus.OVERLOAD -> R.drawable.ic_report
                StudentStatus.OPTIMAL -> R.drawable.ic_check_circle
                StudentStatus.POSITIVE -> R.drawable.ic_thumb_up
            }
        )

        val isAlert = s.status == StudentStatus.INJURY || s.status == StudentStatus.OVERLOAD
        val statusBg = if (isAlert) R.color.error_container else R.color.primary_10
        val statusFg = if (isAlert) R.color.tertiary else R.color.primary
        b.statusCard.setCardBackgroundColor(color(statusBg))
        b.ivStatus.setColorFilter(color(statusFg))
        b.tvStatus.setTextColor(color(statusFg))

        val lp = b.statusCard.layoutParams
        lp.width = if (isAlert) ViewGroup.LayoutParams.MATCH_PARENT else ViewGroup.LayoutParams.WRAP_CONTENT
        b.statusCard.layoutParams = lp
    }
}