package com.rendsad.app.ui.detail

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.rendsad.app.R
import com.rendsad.app.databinding.FragmentStudentDetailBinding
import com.rendsad.app.databinding.ItemCriterionBinding
import com.rendsad.app.databinding.ItemMatchBinding
import com.rendsad.app.databinding.ItemMetricBinding
import com.rendsad.app.model.TrainingBadge
import com.rendsad.app.model.TrainingEntry

class StudentDetailFragment : Fragment(R.layout.fragment_student_detail) {

    private var _binding: FragmentStudentDetailBinding? = null
    private val binding get() = _binding!!

    private class Criterion(
        val view: ItemCriterionBinding,
        val title: String,
        val icon: Int,
        var selected: Int,
        var labelText: String,
        val note: String?
    )

    private val options = listOf("Buena", "Regular", "Atención")
    private lateinit var criteria: List<Criterion>

    private val entries = listOf(
        TrainingEntry("18 May 2024 - 17:30 hrs", "Completado (90 min)", TrainingBadge.COMPLETED,
            listOf("Control orientado", "Pases cortos", "Resistencia aeróbica", "Definición 1v1"),
            7, "Sin dolor articular", true),
        TrainingEntry("15 May 2024 - 17:00 hrs", "Carga moderada (75 min)", TrainingBadge.MODERATE,
            listOf("Rondos de presión", "Sprint 30m", "Táctica fija"),
            6, "Monitoreo de tobillo preventivo", false),
        TrainingEntry("12 May 2024 - 16:30 hrs", "60 min", TrainingBadge.PLAIN,
            listOf("Fuerza explosiva", "Pliometría"),
            null, null, false)
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentStudentDetailBinding.bind(view)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        binding.metricAttendance.setMetric("Asistencia", "94%")
        binding.metricLoad.setMetric("Ritmo carga", "Óptimo", R.color.primary)
        binding.metricMinutes.setMetric("Minutos mes", "420'")

        binding.btnViewInjury.setOnClickListener {
            // Aquí se abre la pantalla de la lesión cuando la tengamos
        }
        binding.btnAddInjury.setOnClickListener {
            // Aquí se abre el registro de lesión cuando lo tengamos
        }

        val tabButtons = listOf(binding.btnTabRendimiento, binding.btnTabDestreza, binding.btnTabActitud)
        tabButtons.forEachIndexed { index, button -> button.setOnClickListener { selectTab(index) } }
        selectTab(0)

        val adapter = TrainingEntryAdapter()
        binding.tabRendimiento.rvEntries.adapter = adapter
        adapter.submitList(entries)
        binding.tabRendimiento.btnRegisterTraining.setOnClickListener {
            findNavController().navigate(R.id.action_detail_to_training)
        }

        with(binding.tabDestreza) {
            btnRegisterMatch.setOnClickListener {
                findNavController().navigate(R.id.action_detail_to_match)
            }
            mdMinutes.setMetric("Minutos", "85'")
            mdGoals.setMetric("Goles", "1", R.color.primary)
            mdAssists.setMetric("Asist.", "2", R.color.primary)
            mdRating.setMetric("Nota", "8.8")
            setMatch(matchOne, "Atlético Juvenil (3 - 1)", "Jornada 14",
                "1 gol • 2 asistencias • 85 minutos", "8.8/10", "10 May", true)
            setMatch(matchTwo, "Real Cantera (2 - 2)", "Amistoso",
                "0 goles • 1 asistencia • 60 minutos", "7.9/10", "03 May", false)
        }

        val a = binding.tabActitud
        criteria = listOf(
            Criterion(a.criterionPunctuality, "Puntualidad", R.drawable.ic_schedule, 0,
                "Excelente récord", null),
            Criterion(a.criterionBehavior, "Comportamiento", R.drawable.ic_group, 0,
                "Líder positivo", null),
            Criterion(a.criterionDisposition, "Disposición", R.drawable.ic_psychology, 1,
                "Bajo observación",
                "Nota técnica: Presenta fatiga al cierre. Requiere pausas tácticas escalonadas y recuperación activa.")
        )
        criteria.forEach { c ->
            c.view.title.text = c.title
            c.view.icon.setImageResource(c.icon)
            val buttons = listOf(c.view.btnGood, c.view.btnRegular, c.view.btnAttention)
            buttons.forEachIndexed { index, button ->
                button.setOnClickListener {
                    c.selected = index
                    c.labelText = options[index]
                    updateCriterion(c)
                }
            }
            updateCriterion(c)
        }
    }

    private fun color(id: Int) = ContextCompat.getColor(requireContext(), id)

    private fun ItemMetricBinding.setMetric(label: String, value: String, colorRes: Int = R.color.on_surface) {
        this.label.text = label
        this.value.text = value
        this.value.setTextColor(color(colorRes))
    }

    private fun selectTab(index: Int) {
        val buttons = listOf(binding.btnTabRendimiento, binding.btnTabDestreza, binding.btnTabActitud)
        val contents = listOf(binding.tabRendimiento.root, binding.tabDestreza.root, binding.tabActitud.root)
        buttons.forEachIndexed { i, button ->
            val selected = i == index
            button.backgroundTintList = ColorStateList.valueOf(
                color(if (selected) R.color.primary else android.R.color.transparent)
            )
            button.setTextColor(color(if (selected) R.color.on_primary else R.color.on_surface_variant))
            contents[i].visibility = if (selected) View.VISIBLE else View.GONE
        }
    }

    private fun setMatch(
        m: ItemMatchBinding,
        title: String,
        tag: String,
        stats: String,
        score: String,
        date: String,
        highlight: Boolean
    ) {
        m.tvTitle.text = title
        m.tvTag.text = tag
        m.tvStats.text = stats
        m.tvScore.text = score
        m.tvDate.text = date
        m.vsCard.setCardBackgroundColor(color(if (highlight) R.color.primary else R.color.surface_container_high))
        m.tvVs.setTextColor(color(if (highlight) R.color.on_primary else R.color.on_surface))
        m.tvScore.setTextColor(color(if (highlight) R.color.primary else R.color.on_surface))
    }

    private fun updateCriterion(c: Criterion) {
        val buttons = listOf(c.view.btnGood, c.view.btnRegular, c.view.btnAttention)
        val bgColors = listOf(R.color.primary, R.color.amber_container, R.color.error_container)
        val fgColors = listOf(R.color.on_primary, R.color.on_amber_container, R.color.on_error_container)
        val labelColors = listOf(R.color.primary, R.color.on_amber_container, R.color.error)

        buttons.forEachIndexed { i, button ->
            val selected = i == c.selected
            button.backgroundTintList = ColorStateList.valueOf(
                color(if (selected) bgColors[i] else android.R.color.transparent)
            )
            button.setTextColor(color(if (selected) fgColors[i] else R.color.on_surface_variant))
        }

        c.view.label.text = c.labelText
        c.view.label.setTextColor(color(labelColors[c.selected]))
        c.view.icon.setColorFilter(color(labelColors[c.selected]))

        c.view.noteBox.visibility = if (c.note != null && c.selected == 1) View.VISIBLE else View.GONE
        c.note?.let { c.view.tvNote.text = it }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}