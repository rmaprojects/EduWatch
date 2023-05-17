package com.rmaprojects.teachers.presentation.assign_student.dashboard

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
import com.rmaprojects.core.ui.theme.EduWatchTheme
import com.rmaprojects.core.utils.buildSnackbar
import com.rmaprojects.teachers.R
import com.rmaprojects.teachers.components.ClassCard
import com.rmaprojects.teachers.databinding.FragmentAssignStudentDashboardBinding
import com.rmaprojects.teachers.presentation.assign_student.dashboard.event.AssignStudentDashboardEvent
import com.rmaprojects.teachers.presentation.assign_student.dashboard.event.DeleteClassEvent
import com.rmaprojects.teachers.presentation.assign_student.input.AssignStudentInputFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AssignStudentDashboardFragment : Fragment(R.layout.fragment_assign_student_dashboard) {

    private val binding: FragmentAssignStudentDashboardBinding by viewBinding()
    private val viewModel: AssignStudentDashboardViewModel by viewModels()

    override fun onStart() {
        super.onStart()
        viewModel.getClassList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabAddClass.setOnClickListener {
            findNavController().navigate(R.id.action_nav_assign_student_to_assignStudentInputFragment)
        }

        binding.composeViewStudentList.setContent {
            EduWatchTheme {
                Surface(
                    color = MaterialTheme.colorScheme.surface
                ) {
                    ClassList()
                }
            }
        }
    }

    @Composable
    fun ClassList() {

        LaunchedEffect(key1 = true) {
            viewModel.deleteUiEvent.collectLatest { event ->
                when (event) {
                    is DeleteClassEvent.Error -> {
                        buildSnackbar(
                            binding.root,
                            event.message
                        ).show()
                    }
                    is DeleteClassEvent.Loading -> {
                        buildSnackbar(
                            binding.root,
                            "Menghapus..."
                        ).show()
                    }
                    is DeleteClassEvent.Success -> {
                        buildSnackbar(
                            binding.root,
                            "Berhasil menghapus Kelas!"
                        ).show()
                        viewModel.getClassList()
                    }
                }
            }
        }

        viewModel.uiEvent.collectAsState(AssignStudentDashboardEvent.Loading).value.let { event ->
            when (event) {
                is AssignStudentDashboardEvent.Error -> {
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
                            Button(onClick = { viewModel.getClassList() }) {
                                Text(text = "Muat ulang")
                            }
                        }
                    }
                }
                is AssignStudentDashboardEvent.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
                is AssignStudentDashboardEvent.Success -> {
                    LazyColumn {
                        items(viewModel.classList.value) { classes ->
                            ClassCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                className = "${classes.grade}",
                                classVocation = classes.vocation,
                                academicYear = "${classes.year}",
                                onClick = {
                                    val bundle = bundleOf(
                                        AssignStudentInputFragment.CLASS_YEAR to classes.id
                                    )
                                    findNavController().navigate(R.id.action_nav_assign_student_to_assignStudentInputFragment, bundle)
                                },
                                onDeleteClick = {
                                    viewModel.deleteClassFromYears(classes.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}