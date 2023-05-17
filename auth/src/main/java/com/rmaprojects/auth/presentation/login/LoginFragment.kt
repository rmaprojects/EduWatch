package com.rmaprojects.auth.presentation.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.rmaprojects.auth.R
import com.rmaprojects.auth.databinding.FragmentLoginBinding
import com.rmaprojects.auth.event.AuthUiEvent
import com.rmaprojects.core.utils.buildSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private val binding: FragmentLoginBinding by viewBinding()
    private val viewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.txtToRegister.setOnClickListener {
            findNavController().navigate(R.id.nav_register)
        }

        binding.btnSignIn.setOnClickListener {
            val email = binding.inputEmail.editText?.text.toString()
            val password = binding.inputPassword.editText?.text.toString()
            viewModel.login(email, password)
        }

        lifecycleScope.launch {
            viewModel.uiEvent.collectLatest { event ->
                when (event) {
                    is AuthUiEvent.EmptyTextField -> {
                        buildSnackbar(
                            binding.root,
                            "Email atau Password masih kosong"
                        ).show()
                    }
                    is AuthUiEvent.Error -> {
                        buildSnackbar(
                            binding.root,
                            event.message
                        ).show()
                    }
                    is AuthUiEvent.Loading -> {
                        with(binding) {
                            inputEmail.isEnabled = false
                            inputPassword.isEnabled = false
                            btnSignIn.isEnabled = false
                        }
                    }
                    is AuthUiEvent.Success -> {
                        with(binding) {
                            inputEmail.isEnabled = true
                            inputPassword.isEnabled = true
                            btnSignIn.isEnabled = true
                            requireActivity().startActivity(Intent().setClassName(requireContext(), "com.rmaprojects.eduwatch.MainActivity"))
                            requireActivity().finish()
                        }
                    }
                }
            }
        }
    }
}