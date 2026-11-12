package com.rendsad.app.ui.students

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.rendsad.app.R
import com.rendsad.app.databinding.FragmentAddStudentBinding

class AddStudentFragment : Fragment(R.layout.fragment_add_student) {

    private var _binding: FragmentAddStudentBinding? = null
    private val binding get() = _binding!!

    private val categories = listOf(
        "Niños (6 - 11 años)",
        "Adolescentes (12 - 15 años)",
        "Jóvenes (16 - 19 años)",
        "Sub (Sub-17 / Sub-20)"
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentAddStudentBinding.bind(view)

        binding.actvCategory.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, categories)
        )
        binding.etAge.setText("14")
        setCategoryForAge(14)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnCancelar.setOnClickListener { findNavController().popBackStack() }

        binding.btnMinus.setOnClickListener { adjustAge(-1) }
        binding.btnPlus.setOnClickListener { adjustAge(1) }

        binding.switchFit.setOnCheckedChangeListener { _, isFit -> updateFitnessState(isFit) }

        binding.btnGuardar.setOnClickListener { saveStudent() }
    }

    private fun adjustAge(delta: Int) {
        val current = binding.etAge.text.toString().toIntOrNull() ?: 14
        val age = (current + delta).coerceIn(5, 40)
        binding.etAge.setText(age.toString())
        setCategoryForAge(age)
    }

    private fun setCategoryForAge(age: Int) {
        val index = when {
            age <= 11 -> 0
            age <= 15 -> 1
            age <= 18 -> 2
            else -> 3
        }
        binding.actvCategory.setText(categories[index], false)
    }

    private fun updateFitnessState(isFit: Boolean) {
        fun color(id: Int) = ContextCompat.getColor(requireContext(), id)

        binding.statusIconCard.setCardBackgroundColor(
            color(if (isFit) R.color.primary_fixed else R.color.tertiary_fixed)
        )
        binding.ivStatusIcon.setImageResource(
            if (isFit) R.drawable.ic_health_and_safety else R.drawable.ic_medical_services
        )
        binding.ivStatusIcon.setColorFilter(
            color(if (isFit) R.color.on_primary_fixed else R.color.tertiary)
        )
        binding.tvFitDesc.text =
            if (isFit) "Apto para entrenar a máxima intensidad" else "Reporta molestias o lesión previa"
        binding.warningCard.visibility = if (isFit) View.GONE else View.VISIBLE
    }

    private fun saveStudent() {
        val name = binding.etName.text.toString().trim()
        val lastName = binding.etLastName.text.toString().trim()
        val age = binding.etAge.text.toString().toIntOrNull()

        binding.tilName.error = if (name.isEmpty()) "Escribe el nombre" else null
        binding.tilLastName.error = when {
            lastName.isEmpty() -> "Escribe el apellido"
            lastName.contains(" ") -> "Solo un apellido"
            else -> null
        }
        binding.tilAge.error = if (age == null || age < 5 || age > 40) "Edad entre 5 y 40" else null

        if (binding.tilName.error != null ||
            binding.tilLastName.error != null ||
            binding.tilAge.error != null
        ) return

        // Aquí se guardará en SQLite y Supabase cuando conectemos la base de datos
        Snackbar.make(binding.root, "Alumno registrado con éxito", Snackbar.LENGTH_SHORT).show()
        view?.postDelayed({ findNavController().popBackStack() }, 1200)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}