package com.rmaprojects.eduwatch.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rmaprojects.core.data.source.local.LocalUser
import com.rmaprojects.core.utils.buildSnackbar
import com.rmaprojects.eduwatch.R
import com.rmaprojects.eduwatch.databinding.FragmentProfileBinding
import com.rmaprojects.eduwatch.presentation.event.ProfileUiEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment: Fragment(R.layout.fragment_profile) {

    private val binding: FragmentProfileBinding by viewBinding()
    private val viewModel: ProfileViewModel by viewModels()

    override fun onStart() {
        super.onStart()
        viewModel.getUserDetail()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.uiEvent.collectLatest {  event ->
                when (event) {
                    is ProfileUiEvent.Error -> {
                        buildSnackbar(
                            binding.root,
                            event.message
                        ).show()
                    }
                    is ProfileUiEvent.Loading -> {
                        with(binding) {
                            txtRole.text = "Loading..."
                            txtUserId.text = "Loading..."
                            txtUsername.text = "Loading..."
                        }
                    }
                    is ProfileUiEvent.Success -> {
                        with(binding) {
                            txtRole.text = viewModel.userData.value.role
                            txtUsername.text = viewModel.userData.value.userName
                            txtUserId.text = viewModel.userData.value.userId
                        }
                    }
                }
            }
        }

        binding.btnLogOut.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Yakin ingin keluar?")
                .setPositiveButton("Ya") { _, _ ->
                    viewModel.logOut()
                    LocalUser.clear()
                    requireActivity().finish()
                }
                .setNegativeButton("Tidak") { _, _ -> }
                .create()
                .show()
        }
    }
}