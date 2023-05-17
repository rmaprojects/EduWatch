package com.rmaprojects.teachers.presentation.student_list.dashboard

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.rmaprojects.core.data.source.remote.response.students.StudentsEntity
import com.rmaprojects.core.ui.theme.EduWatchTheme
import com.rmaprojects.core.utils.buildSnackbar
import com.rmaprojects.teachers.R
import com.rmaprojects.teachers.components.StudentsCard
import com.rmaprojects.teachers.databinding.FragmentStudentListDashboardBinding
import com.rmaprojects.teachers.presentation.student_list.dashboard.events.DeleteStudentEvent
import com.rmaprojects.teachers.presentation.student_list.dashboard.events.StudentListDashboardUiEvent
import com.rmaprojects.teachers.presentation.student_list.input.StudentListInputFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class StudentListDashboardFragment : Fragment(R.layout.fragment_student_list_dashboard) {

    private val binding: FragmentStudentListDashboardBinding by viewBinding()
    private val viewModel: StudentListDashboardViewModel by viewModels()

    override fun onStart() {
        super.onStart()
        viewModel.getStudents()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabAddStudent.setOnClickListener {
            findNavController().navigate(R.id.action_studentListDashboardFragment_to_studentListInputFragment)
        }

        binding.composeViewStudentList.setContent {

            EduWatchTheme {
                Surface(
                    color = MaterialTheme.colorScheme.surface
                ) {
                    LaunchedEffect(key1 = true) {
                        viewModel.deleteUiEvent.collectLatest { event ->
                            when (event) {
                                is DeleteStudentEvent.Error -> {
                                    buildSnackbar(
                                        binding.root,
                                        event.message
                                    ).show()
                                }
                                is DeleteStudentEvent.Loading -> {
                                    buildSnackbar(
                                        binding.root,
                                        "Menghapus..."
                                    ).show()
                                }
                                is DeleteStudentEvent.Success -> {
                                    buildSnackbar(
                                        binding.root,
                                        "Selesai menghapus!"
                                    ).show()
                                    viewModel.getStudents()
                                }
                            }
                        }
                    }

                    viewModel.uiEvent.collectAsState(initial = StudentListDashboardUiEvent.Loading).value.let { event ->
                        when (event) {
                            is StudentListDashboardUiEvent.Error -> {
                                buildSnackbar(
                                    binding.root,
                                    event.message
                                ).show()
                                Box(
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "Terjadi Kesalahan",
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Button(onClick = { viewModel.getStudents() }) {
                                            Text(text = "Muat ulang")
                                        }
                                    }
                                }
                            }
                            is StudentListDashboardUiEvent.Loading -> {
                                Box {
                                    CircularProgressIndicator(
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }
                            is StudentListDashboardUiEvent.Success -> {
                                StudentList(
                                    studentList = viewModel.studentList.value
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun StudentList(
        studentList: List<StudentsEntity>
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(studentList, key = { it.id!! }) { student ->
                StudentsCard(
                    modifier = Modifier.padding(12.dp),
                    name = student.name,
                    nis = student.nis,
                    onClick = {
                        val bundle = bundleOf(
                            StudentListInputFragment.STUDENT_ID to student.id
                        )
                        findNavController().navigate(R.id.action_studentListDashboardFragment_to_studentListInputFragment, bundle)
                    },
                    onDeleteClick = {
                        viewModel.deleteStudent(student.id)
                    }
                )
            }
        }
    }
}