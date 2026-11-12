package com.rendsad.app.ui.sport

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.rendsad.app.R
import com.rendsad.app.databinding.FragmentSportSelectionBinding
import com.rendsad.app.databinding.ItemSportCardBinding
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController

class SportSelectionFragment : Fragment(R.layout.fragment_sport_selection) {

    private var _binding: FragmentSportSelectionBinding? = null
    private val binding get() = _binding!!

    private var selectedSport = "futbol"

    private class Sport(
        val key: String,
        val card: ItemSportCardBinding,
        val name: String,
        val subtitle: String,
        val icon: Int,
        val description: String
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentSportSelectionBinding.bind(view)

        val sports = listOf(
            Sport("basquetbol", binding.cardBasquetbol, "Básquetbol", "Carga y salto",
                R.drawable.ic_sports_basketball, "Monitoreo de salto vertical, impactos y fatiga en pista."),
            Sport("voleibol", binding.cardVoleibol, "Voleibol", "Potencia reactiva",
                R.drawable.ic_sports_volleyball, "Evaluación de picos de salto, aterrizajes y potencia reactiva."),
            Sport("futbol", binding.cardFutbol, "Fútbol", "Resistencia y sprint",
                R.drawable.ic_sports_soccer, "Monitoreo de sprint, desaceleración y carga metabólica."),
            Sport("beisbol", binding.cardBeisbol, "Béisbol", "Fuerza rotacional",
                R.drawable.ic_sports_baseball, "Métricas de aceleración escapular, torsión de tronco y velocidad de brazo.")
        )

        sports.forEach { sport ->
            sport.card.title.text = sport.name
            sport.card.subtitle.text = sport.subtitle
            sport.card.icon.setImageResource(sport.icon)
            sport.card.root.setOnClickListener {
                selectedSport = sport.key
                updateSelection(sports)
            }
        }
        updateSelection(sports)

        binding.btnContinuar.setOnClickListener {
            findNavController().navigate(
                R.id.action_sport_to_registration,
                bundleOf("sport" to selectedSport)
            )

        }
    }

    private fun updateSelection(sports: List<Sport>) {
        fun color(id: Int) = ContextCompat.getColor(requireContext(), id)

        sports.forEach { sport ->
            val selected = sport.key == selectedSport
            val c = sport.card

            c.root.setCardBackgroundColor(
                color(if (selected) R.color.surface_container_high else R.color.surface_container_low)
            )
            c.iconCard.setCardBackgroundColor(
                color(if (selected) R.color.primary_container else R.color.surface_container_lowest)
            )
            c.icon.setColorFilter(color(if (selected) R.color.on_primary else R.color.secondary))
            c.radio.setCardBackgroundColor(
                color(if (selected) R.color.primary else R.color.surface_container_highest)
            )
            c.check.visibility = if (selected) View.VISIBLE else View.INVISIBLE
            c.subtitle.setTextColor(color(if (selected) R.color.primary else R.color.on_surface_variant))

            if (selected) binding.tvDescripcion.text = sport.description
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}