package com.rmaprojects.auth.presentation.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.rmaprojects.auth.R
import com.rmaprojects.core.ui.component.EduWatchSegmentedButton
import com.rmaprojects.auth.databinding.FragmentRegisterBinding
import com.rmaprojects.auth.event.AuthUiEvent
import com.rmaprojects.auth.event.FetchStudentUiEvent
import com.rmaprojects.core.utils.buildSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment: Fragment(R.layout.fragment_register) {

    private val binding: FragmentRegisterBinding by viewBinding()
    private val viewModel: RegisterViewModel by viewModels()

    override fun onStart() {
        super.onStart()
        viewModel.setUserRole(0)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.txtGoToLogin.setOnClickListener {
            findNavController().navigate(R.id.nav_login)
        }

        binding.btnRegister.setOnClickListener {
            val email = binding.inputEmail.editText?.text.toString()
            val password = binding.inputPassword.editText?.text.toString()
            val name = binding.inputName.editText?.text.toString()
            val phoneNumber = binding.inputPhone.editText?.text.toString()

            viewModel.register(
                email, name, password, phoneNumber
            )
        }

        lifecycleScope.launch {
            viewModel.uiEvent.collectLatest { event ->
                when (event) {
                    is AuthUiEvent.EmptyTextField -> {
                        buildSnackbar(
                            binding.root,
                            "Form pendaftaran belum diisi lengkap"
                        ).show()
                    }
                    is AuthUiEvent.Error -> {
                        buildSnackbar(
                            binding.root,
                            event.message
                        )
                    }
                    is AuthUiEvent.Loading -> {
                        with(binding) {
                            btnRegister.isEnabled = false
                            inputName.isEnabled = false
                            inputPassword.isEnabled = false
                            inputEmail.isEnabled = false
                        }
                    }
                    is AuthUiEvent.Success -> {
                        with(binding) {
                            btnRegister.isEnabled = true
                            inputName.isEnabled = true
                            inputPassword.isEnabled = true
                            inputEmail.isEnabled = false
                        }
                        requireActivity().startActivity(Intent().setClassName(requireContext(), "com.rmaprojects.eduwatch.MainActivity"))
                        requireActivity().finish()
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.fetchStudentUiEvent.collectLatest { event ->
                when (event) {
                    is FetchStudentUiEvent.Error -> {
                        buildSnackbar(
                            binding.root,
                            "Error Fetching Students"
                        ).show()
                    }
                    is FetchStudentUiEvent.Success -> {
                        val item = event.data.map { it.name }.toTypedArray()
                        binding.inputStudent.setSimpleItems(item)
                        binding.inputStudent.setOnItemClickListener { _, _, position, _ ->
                            binding.inputStudent.setText(item[position])
                            viewModel.setSelectedStudentId(event.data[position].id)
                        }
                    }
                }
            }
        }

        binding.composeViewSegmentRole.setContent {
            EduWatchSegmentedButton(
                items = listOf("Guru", "Wali Murid"),
                onItemSelection = { selectedRole ->
                    viewModel.setUserRole(selectedRole)
                    when (selectedRole) {
                        0 -> binding.layoutInputParents.isVisible = false
                        1 -> binding.layoutInputParents.isVisible = true
                    }
                }
            )
        }
    }
}