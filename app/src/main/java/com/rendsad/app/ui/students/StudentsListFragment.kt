package com.rendsad.app.ui.students

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.rendsad.app.R
import com.rendsad.app.databinding.FragmentStudentsListBinding
import com.rendsad.app.model.Student
import com.rendsad.app.model.StudentStatus
import java.text.Normalizer
import androidx.navigation.fragment.findNavController

class StudentsListFragment : Fragment(R.layout.fragment_students_list) {

    private var _binding: FragmentStudentsListBinding? = null
    private val binding get() = _binding!!

    private val adapter = StudentAdapter()
    private var query = ""
    private var category = "Todos"

    private val allStudents = listOf(
        Student("Mateo Ramírez", 10, "Medio central", "Sub-15", "Adolescentes",
            StudentStatus.INJURY, "Lesión: Esguince tobillo der."),
        Student("Santiago López", 9, "Delantero", "Sub-15", "Adolescentes",
            StudentStatus.OPTIMAL, "Rendimiento óptimo"),
        Student("Diego Valenzuela", 4, "Defensa central", "Sub-15", "Adolescentes",
            StudentStatus.OVERLOAD, "Sobrecarga muscular"),
        Student("Javier Gutiérrez", 1, "Portero", "Sub-15", "Adolescentes",
            StudentStatus.POSITIVE, "Excelente actitud")
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentStudentsListBinding.bind(view)

        binding.rvStudents.adapter = adapter
        binding.tvCount.text = "${allStudents.size} Alumnos registrados"
        val alerts = allStudents.count {
            it.status == StudentStatus.INJURY || it.status == StudentStatus.OVERLOAD
        }
        binding.tvAlerts.text = "$alerts con alerta médica"

        binding.etSearch.doOnTextChanged { text, _, _, _ ->
            query = text.toString().normalize()
            applyFilter()
        }

        binding.chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            category = when (checkedIds.firstOrNull()) {
                R.id.chipNinos -> "Niños"
                R.id.chipAdolescentes -> "Adolescentes"
                R.id.chipJovenes -> "Jóvenes"
                else -> "Todos"
            }
            applyFilter()
        }

        binding.btnAgregar.setOnClickListener {
            findNavController().navigate(R.id.action_students_to_add)
            // Aquí se abre la pantalla de agregar alumno cuando la tengamos
        }

        applyFilter()
    }

    private fun applyFilter() {
        val filtered = allStudents.filter { s ->
            val matchesCategory = category == "Todos" || s.category == category
            val matchesQuery = query.isEmpty() ||
                    s.name.normalize().contains(query) ||
                    s.number.toString() == query
            matchesCategory && matchesQuery
        }
        adapter.submitList(filtered)
        binding.emptyState.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun String.normalize(): String =
        Normalizer.normalize(this, Normalizer.Form.NFD)
            .replace("\\p{Mn}+".toRegex(), "")
            .lowercase()
            .trim()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}