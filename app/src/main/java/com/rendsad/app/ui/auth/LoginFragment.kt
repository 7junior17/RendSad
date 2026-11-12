package com.rendsad.app.ui.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController   // NUEVA
import com.rendsad.app.R
import com.rendsad.app.databinding.FragmentLoginBinding

class LoginFragment : Fragment(R.layout.fragment_login) {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentLoginBinding.bind(view)

        binding.btnGoogle.setOnClickListener {
            binding.btnGoogle.text = "Conectando con Google..."
            binding.btnGoogle.isEnabled = false
            findNavController().navigate(R.id.action_login_to_sport)   // NUEVA
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}