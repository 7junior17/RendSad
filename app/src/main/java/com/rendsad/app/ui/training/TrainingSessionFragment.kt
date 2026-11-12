package com.rendsad.app.ui.training

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.rendsad.app.R
import com.rendsad.app.databinding.FragmentTrainingSessionBinding
import com.rendsad.app.databinding.ItemIntensityBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class TrainingSessionFragment : Fragment(R.layout.fragment_training_session) {

    private var _binding: FragmentTrainingSessionBinding? = null
    private val binding get() = _binding!!

    private class Intensity(
        val card: ItemIntensityBinding,
        val name: String,
        val subtitle: String,
        val icon: Int,
        val badge: String,
        val badgeBg: Int,
        val badgeText: Int
    )

    private lateinit var intensities: List<Intensity>
    private var selectedIntensity = 1

    private var dateMillis = MaterialDatePicker.todayInUtcMilliseconds()
    private var hour = 17
    private var minute = 30

    private val drills = listOf(
        "Control y Pase" to true,
        "Resistencia 4x4" to true,
        "Tiros libres" to true,
        "Rondo 5v2" to false,
        "Circuito de fuerza" to false,
        "Definición a portería" to false,
        "Sprint y cambios de ritmo" to false,
        "Juego aéreo" to false,
        "Presión tras pérdida" to false
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentTrainingSessionBinding.bind(view)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        showDate()
        showTime()
        binding.cardDate.setOnClickListener { pickDate() }
        binding.cardTime.setOnClickListener { pickTime() }

        binding.chipDrills.setOnCheckedStateChangeListener { _, _ -> updateDrillCount() }
        drills.forEach { (name, checked) -> addDrillChip(name, checked) }
        updateDrillCount()

        binding.btnToggleAdd.setOnClickListener { showAddForm(true) }
        binding.btnCloseAdd.setOnClickListener { showAddForm(false) }
        binding.btnSubmitDrill.setOnClickListener {
            val name = binding.etNewDrill.text.toString().trim()
            if (name.isEmpty()) return@setOnClickListener
            addDrillChip(name, true)
            updateDrillCount()
            binding.etNewDrill.text?.clear()
            showAddForm(false)
        }

        intensities = listOf(
            Intensity(binding.cardBaja, "Baja", "Recuperación", R.drawable.ic_spa,
                "Baja • RPE 3-5", R.color.surface_container_highest, R.color.on_surface),
            Intensity(binding.cardMedia, "Media", "Desarrollo", R.drawable.ic_speed,
                "Media • RPE 6-7", R.color.primary_fixed, R.color.on_primary_fixed),
            Intensity(binding.cardAlta, "Alta", "Competitiva", R.drawable.ic_local_fire_department,
                "Alta • RPE 8-10", R.color.amber_container, R.color.on_amber_container)
        )
        intensities.forEachIndexed { index, item ->
            item.card.title.text = item.name
            item.card.subtitle.text = item.subtitle
            item.card.icon.setImageResource(item.icon)
            item.card.root.setOnClickListener {
                selectedIntensity = index
                updateIntensity()
            }
        }
        updateIntensity()

        binding.tvNotesCount.text = "0 caracteres"
        binding.etNotes.doAfterTextChanged {
            binding.tvNotesCount.text = "${it?.length ?: 0} caracteres"
        }

        binding.btnSave.setOnClickListener { saveTraining() }
    }

    private fun showDate() {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        format.timeZone = TimeZone.getTimeZone("UTC")
        binding.tvDate.text = format.format(dateMillis)
    }

    private fun showTime() {
        binding.tvTime.text = String.format(Locale.US, "%02d:%02d hrs", hour, minute)
    }

    private fun pickDate() {
        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Fecha de la sesión")
            .setSelection(dateMillis)
            .build()
        picker.addOnPositiveButtonClickListener {
            dateMillis = it
            showDate()
        }
        picker.show(childFragmentManager, "date")
    }

    private fun pickTime() {
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(hour)
            .setMinute(minute)
            .setTitleText("Hora de inicio")
            .build()
        picker.addOnPositiveButtonClickListener {
            hour = picker.hour
            minute = picker.minute
            showTime()
        }
        picker.show(childFragmentManager, "time")
    }

    private fun addDrillChip(name: String, checked: Boolean) {
        val chip = layoutInflater.inflate(R.layout.item_drill_chip, binding.chipDrills, false) as Chip
        chip.text = name
        chip.isChecked = checked
        binding.chipDrills.addView(chip)
    }

    private fun updateDrillCount() {
        binding.tvDrillCount.text = "${binding.chipDrills.checkedChipIds.size} activos"
    }

    private fun showAddForm(show: Boolean) {
        binding.addDrillForm.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnToggleAdd.visibility = if (show) View.GONE else View.VISIBLE
        if (show) binding.etNewDrill.requestFocus()
    }

    private fun updateIntensity() {
        fun color(id: Int) = ContextCompat.getColor(requireContext(), id)

        intensities.forEachIndexed { index, item ->
            val selected = index == selectedIntensity
            val c = item.card
            c.root.setCardBackgroundColor(
                color(if (selected) R.color.primary else R.color.surface_container)
            )
            c.icon.setColorFilter(
                color(if (selected) R.color.primary_fixed else R.color.on_surface_variant)
            )
            c.title.setTextColor(
                color(if (selected) R.color.on_primary else R.color.on_surface)
            )
            c.subtitle.setTextColor(
                color(if (selected) R.color.surface_container_highest else R.color.on_surface_variant)
            )
        }

        val current = intensities[selectedIntensity]
        binding.tvIntensity.text = current.badge
        binding.tvIntensity.setTextColor(color(current.badgeText))
        binding.intensityBadge.setCardBackgroundColor(color(current.badgeBg))
    }

    private fun saveTraining() {
        val button = binding.btnSave
        button.isClickable = false
        button.text = "Guardando en RendSad..."

        // Aquí se guardará la sesión en SQLite y Supabase cuando conectemos la base de datos
        button.postDelayed({
            if (_binding == null) return@postDelayed
            button.text = "¡Entrenamiento Registrado!"
            button.setIconResource(R.drawable.ic_task_alt)
            button.postDelayed({
                if (_binding == null) return@postDelayed
                button.isClickable = true
                button.text = "Guardar Entrenamiento"
                button.setIconResource(R.drawable.ic_check_circle)
            }, 2000)
        }, 800)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}