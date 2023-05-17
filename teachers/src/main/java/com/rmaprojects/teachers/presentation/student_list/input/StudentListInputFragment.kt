package com.rmaprojects.teachers.presentation.student_list.input

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.rmaprojects.core.utils.buildSnackbar
import dagger.hilt.android.AndroidEntryPoint
import com.rmaprojects.teachers.R
import com.rmaprojects.teachers.databinding.FragmentStudentListInputBinding
import com.rmaprojects.teachers.presentation.student_list.input.events.StudentInputEvent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StudentListInputFragment: Fragment(R.layout.fragment_student_list_input) {

    private val viewModel: StudentListInputViewModel by viewModels()
    private val binding: FragmentStudentListInputBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.uiEvent.collectLatest { event ->
                when (event) {
                    is StudentInputEvent.Error -> {
                        buildSnackbar(
                            binding.root,
                            event.message
                        ).show()
                    }
                    is StudentInputEvent.Loading -> {
                        binding.btnSubmit.isEnabled = false
                    }
                    is StudentInputEvent.Success -> {
                        binding.btnSubmit.isEnabled = true
                        findNavController().navigateUp()
                        findNavController().popBackStack()
                    }
                }
            }
        }

        binding.btnSubmit.setOnClickListener {
            val nis = binding.inputNis.editText?.text.toString().toInt()
            val name = binding.inputStudent.editText?.text.toString()
            val phoneNumber = binding.inputPhoneNumber.editText?.text.toString()
            viewModel.insertStudent(
                name, nis, phoneNumber
            )
        }
    }

    override fun onStart() {
        super.onStart()
        if (viewModel.studentId != null) {
            viewModel.getStudentDetail()
            binding.inputNis.editText?.setText("${viewModel.studentDetail?.nis}")
            binding.inputStudent.editText?.setText("${viewModel.studentDetail?.name}")
            binding.inputPhoneNumber.editText?.setText("${viewModel.studentDetail?.phoneNumber}")
        }
    }

    companion object {
        const val STUDENT_ID = "ID_STUDENT"
    }
}