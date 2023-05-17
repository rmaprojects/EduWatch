package com.rmaprojects.teachers.presentation.assign_student.input

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.rmaprojects.core.ui.theme.EduWatchTheme
import com.rmaprojects.core.utils.buildSnackbar
import com.rmaprojects.teachers.R
import com.rmaprojects.teachers.components.StudentsCard
import com.rmaprojects.teachers.databinding.FragmentAssignStudentInputBinding
import com.rmaprojects.teachers.presentation.assign_student.input.events.AssignStudentInputEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AssignStudentInputFragment : Fragment(R.layout.fragment_assign_student_input) {

    private val binding: FragmentAssignStudentInputBinding by viewBinding()
    private val viewModel: AssignStudentInputViewModel by viewModels()

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            if (viewModel.classYearId != null) {
                viewModel.getClassYear(viewModel.classYearId!!)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        lifecycleScope.launch {
            viewModel.getStudentEvent.collectLatest { event ->
                when (event) {
                    is AssignStudentInputEvent.Error -> {
                        buildSnackbar(
                            binding.root,
                            event.message
                        ).show()
                    }
                    is AssignStudentInputEvent.Loading -> {
                        buildSnackbar(
                            binding.root,
                            "Mendapatkan Data Siswa..."
                        )
                    }
                    is AssignStudentInputEvent.Success -> {
                        val studentList = viewModel.studentList.value

                        binding.inputStudent.setSimpleItems(
                            studentList.map { it.name }.toTypedArray()
                        )

                        binding.inputStudent.setOnItemClickListener { _ , _, position, _ ->
                            val year = binding.inputYear.editText?.text.toString()
                            if (year.isBlank()) {
                                buildSnackbar(
                                    binding.root,
                                    "Isi Tahun terlebih dahulu"
                                ).show()
                                return@setOnItemClickListener
                            }
                            viewModel.chooseStudent(
                                studentName = studentList[position].name,
                                nis = studentList[position].nis,
                                studentId = studentList[position].id!!,
                                studentYearId = Integer.parseInt(year)
                            )
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
                viewModel.uiEvent.collectLatest { event ->
                    when (event) {
                        is AssignStudentInputEvent.Error -> {
                            buildSnackbar(
                                binding.root,
                                event.message
                            ).show()
                        }
                        is AssignStudentInputEvent.Loading -> {
                            binding.btnSubmit.isEnabled = false
                        }
                        is AssignStudentInputEvent.Success -> {
                            binding.btnSubmit.isEnabled = true
                            findNavController().navigateUp()
                            findNavController().popBackStack()
                        }
                    }
                }
            }

        binding.selectedStudentList.setContent {
            EduWatchTheme {
                Surface(
                    color = MaterialTheme.colorScheme.surface
                ) {
                    SelectedStudentList()
                }
            }
        }

        binding.btnSubmit.setOnClickListener {
            val year = binding.inputYear.editText?.text.toString().toInt()
            val grade = binding.inputClassName.editText?.text.toString().toInt()
            val vocation = binding.inputClassName.editText?.text.toString()
            viewModel.submit(grade, year, vocation)
        }

        viewModel.applyStudentDetail = { grade, vocation, year ->
            binding.inputClassName.editText?.apply {
                setText(grade)
                isEnabled = false
            }
            binding.inputVocation.editText?.apply {
                setText(vocation)
                isEnabled = false
            }
            binding.inputYear.editText?.apply {
                setText(year)
                isEnabled = false
            }
        }
    }

    @Composable
    fun SelectedStudentList() {

        LaunchedEffect(key1 = true) {
            viewModel.deleteStudentEvent.collectLatest { event ->
                when (event) {
                    is AssignStudentInputEvent.Error -> {
                        buildSnackbar(
                            binding.root,
                            event.message
                        ).show()
                    }
                    is AssignStudentInputEvent.Loading -> {
                        buildSnackbar(
                            binding.root,
                            "Menghapus..."
                        ).show()
                    }
                    is AssignStudentInputEvent.Success -> {
                        buildSnackbar(
                            binding.root,
                            "Berhasil menghapus siswa!"
                        ).show()
                    }
                }
            }
        }

        LazyColumn {
            items(viewModel.chosenStudent) { chosenStudent ->
                StudentsCard(
                    name = chosenStudent.studentName,
                    nis = chosenStudent.nis,
                    onClick = {},
                    onDeleteClick = {
                        viewModel.deleteStudent(chosenStudent.studentYearId!!)
                    }
                )
            }
        }
    }

    companion object {
        const val CLASS_YEAR = "CLASS_YEAR_ID"
    }
}