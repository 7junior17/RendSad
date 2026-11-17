package com.rendsad.app.ui.match

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.rendsad.app.R
import com.rendsad.app.databinding.FragmentMatchRegistrationBinding
import com.rendsad.app.databinding.ItemSportModuleBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import kotlin.math.ceil

class MatchRegistrationFragment : Fragment(R.layout.fragment_match_registration) {

    private var _binding: FragmentMatchRegistrationBinding? = null
    private val binding get() = _binding!!

    private class Sport(
        val title: String,
        val iconRes: Int,
        val labelA: String,
        val labelB: String,
        val captionA: String?,
        val captionB: String?
    )

    private val sports = mapOf(
        R.id.chipFutbol to Sport("Métricas de Fútbol", R.drawable.ic_sports_soccer,
            "Goles", "Asistencias", "1 penal / 1 jugada", "Pase gol filtrado"),
        R.id.chipBasquetbol to Sport("Métricas de Básquetbol", R.drawable.ic_sports_basketball,
            "Puntos", "Rebotes", null, null),
        R.id.chipVoleibol to Sport("Métricas de Voleibol", R.drawable.ic_sports_volleyball,
            "Puntos directos", "Saques", null, null),
        R.id.chipBeisbol to Sport("Métricas de Béisbol", R.drawable.ic_sports_baseball,
            "Turnos al bate", "Hits", null, null)
    )

    private val ratings = listOf(
        6.0 to "En desarrollo",
        7.0 to "Competitivo",
        8.0 to "Sólido",
        8.5 to "Destacado",
        9.0 to "Excelente",
        10.0 to "Élite"
    )

    private var dateMillis = MaterialDatePicker.todayInUtcMilliseconds()
    private var minutes = 75
    private var goals = 2
    private var assists = 1
    private var yellow = 0
    private var red = 0
    private var ratingIndex = 3

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentMatchRegistrationBinding.bind(view)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        showDate()
        binding.cardDate.setOnClickListener { pickDate() }

        binding.switchOfficial.setOnCheckedChangeListener { _, isOfficial ->
            binding.chipTournament.visibility = if (isOfficial) View.VISIBLE else View.GONE
            binding.tvMatchType.text = if (isOfficial) "Oficial 90'" else "Amistoso"
        }

        binding.chipSports.setOnCheckedStateChangeListener { _, checkedIds ->
            val id = checkedIds.firstOrNull() ?: return@setOnCheckedStateChangeListener
            goals = 0
            assists = 0
            applySport(id)
        }
        applySport(R.id.chipFutbol)

        binding.btnMinutesMinus.setOnClickListener {
            minutes = (minutes - 5).coerceAtLeast(0)
            showCounters()
        }
        binding.btnMinutesPlus.setOnClickListener {
            minutes = (minutes + 5).coerceAtMost(120)
            showCounters()
        }
        binding.counterA.btnMinus.setOnClickListener {
            goals = (goals - 1).coerceAtLeast(0)
            showCounters()
        }
        binding.counterA.btnPlus.setOnClickListener {
            goals += 1
            showCounters()
        }
        binding.counterB.btnMinus.setOnClickListener {
            assists = (assists - 1).coerceAtLeast(0)
            showCounters()
        }
        binding.counterB.btnPlus.setOnClickListener {
            assists += 1
            showCounters()
        }
        binding.cardYellow.setOnClickListener {
            yellow = (yellow + 1) % 3
            showCounters()
        }
        binding.cardRed.setOnClickListener {
            red = (red + 1) % 3
            showCounters()
        }

        setModule(binding.moduleBasketball, R.drawable.ic_sports_basketball,
            "Básquetbol", "Puntos • Rebotes • Asistencias")
        setModule(binding.moduleVolleyball, R.drawable.ic_sports_volleyball,
            "Voleibol", "Puntos directos • Saques • Bloqueos")
        setModule(binding.moduleBaseball, R.drawable.ic_sports_baseball,
            "Béisbol", "Turnos al bate • Hits • Carreras")

        val rateButtons = listOf(
            binding.btnRate60, binding.btnRate70, binding.btnRate80,
            binding.btnRate85, binding.btnRate90, binding.btnRate100
        )
        rateButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                ratingIndex = index
                showRating()
            }
        }

        binding.btnSave.setOnClickListener { saveMatch() }

        showCounters()
        showRating()
    }

    private fun showDate() {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        format.timeZone = TimeZone.getTimeZone("UTC")
        binding.tvDate.text = format.format(dateMillis)
    }

    private fun pickDate() {
        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Fecha del partido")
            .setSelection(dateMillis)
            .build()
        picker.addOnPositiveButtonClickListener {
            dateMillis = it
            showDate()
        }
        picker.show(childFragmentManager, "date")
    }

    private fun applySport(chipId: Int) {
        val sport = sports.getValue(chipId)
        val isFootball = chipId == R.id.chipFutbol

        binding.tvMetricsTitle.text = sport.title
        binding.counterA.label.text = sport.labelA
        binding.counterA.icon.setImageResource(sport.iconRes)
        binding.counterB.label.text = sport.labelB
        binding.counterB.icon.setImageResource(R.drawable.ic_share)

        binding.counterA.caption.text = sport.captionA.orEmpty()
        binding.counterA.caption.visibility = if (sport.captionA == null) View.GONE else View.VISIBLE
        binding.counterB.caption.text = sport.captionB.orEmpty()
        binding.counterB.caption.visibility = if (sport.captionB == null) View.GONE else View.VISIBLE

        binding.cardDiscipline.visibility = if (isFootball) View.VISIBLE else View.GONE
        binding.cardEffectiveness.visibility = if (isFootball) View.VISIBLE else View.GONE

        showCounters()
    }

    private fun showCounters() {
        binding.tvMinutes.text = minutes.toString()
        binding.counterA.tvValue.text = goals.toString()
        binding.counterB.tvValue.text = assists.toString()
        binding.tvYellow.text = yellow.toString()
        binding.tvRed.text = red.toString()
    }

    private fun setModule(m: ItemSportModuleBinding, icon: Int, name: String, description: String) {
        m.icon.setImageResource(icon)
        m.tvName.text = name
        m.tvDescription.text = description
    }

    private fun showRating() {
        fun color(id: Int) = ContextCompat.getColor(requireContext(), id)

        val (value, badge) = ratings[ratingIndex]
        binding.tvRatingValue.text = String.format(Locale.US, "%.1f", value)
        binding.tvRatingBadge.text = badge

        val halves = ceil(value).toInt()
        val full = halves / 2
        val half = halves % 2
        val stars = listOf(binding.star1, binding.star2, binding.star3, binding.star4, binding.star5)
        stars.forEachIndexed { i, star ->
            star.setImageResource(
                when {
                    i < full -> R.drawable.ic_star
                    i == full && half == 1 -> R.drawable.ic_star_half
                    else -> R.drawable.ic_star_border
                }
            )
        }

        val buttons = listOf(
            binding.btnRate60, binding.btnRate70, binding.btnRate80,
            binding.btnRate85, binding.btnRate90, binding.btnRate100
        )
        buttons.forEachIndexed { i, button ->
            val selected = i == ratingIndex
            button.backgroundTintList = ColorStateList.valueOf(
                color(if (selected) R.color.primary else R.color.surface_container_low)
            )
            button.setTextColor(color(if (selected) R.color.on_primary else R.color.on_surface))
        }
    }

    private fun saveMatch() {
        val rival = binding.etRival.text.toString().trim()
        if (rival.isEmpty()) {
            binding.etRival.error = "Escribe el nombre del rival"
            binding.etRival.requestFocus()
            return
        }

        val button = binding.btnSave
        button.isClickable = false
        button.text = "Guardando datos..."

        // Aquí se guardará el partido en SQLite y Supabase cuando conectemos la base de datos
        button.postDelayed({
            if (_binding == null) return@postDelayed
            button.text = "¡Partido registrado con éxito!"
            button.setIconResource(R.drawable.ic_check_circle)
            button.postDelayed({
                if (_binding == null) return@postDelayed
                findNavController().popBackStack()
            }, 1000)
        }, 700)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}