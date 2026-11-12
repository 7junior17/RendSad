package com.rendsad.app.ui.registration

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.rendsad.app.R
import com.rendsad.app.databinding.FragmentCoachRegistrationBinding

class CoachRegistrationFragment : Fragment(R.layout.fragment_coach_registration) {

    private var _binding: FragmentCoachRegistrationBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentCoachRegistrationBinding.bind(view)

        val sport = arguments?.getString("sport") ?: "futbol"
        binding.ivSport.setImageResource(
            when (sport) {
                "basquetbol" -> R.drawable.ic_sports_basketball
                "voleibol" -> R.drawable.ic_sports_volleyball
                "beisbol" -> R.drawable.ic_sports_baseball
                else -> R.drawable.ic_sports_soccer
            }
        )

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSiguiente.setOnClickListener {
            val age = binding.etAge.text.toString().trim().toIntOrNull()
            if (age == null || age < 18) {
                binding.tilAge.error = "Ingresa una edad de 18 años o más"
                return@setOnClickListener
            }
            binding.tilAge.error = null
            // Aquí se navega al Paso 2 cuando tengamos esa pantalla
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}