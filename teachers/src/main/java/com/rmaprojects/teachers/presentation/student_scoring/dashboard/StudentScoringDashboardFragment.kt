package com.rmaprojects.teachers.presentation.student_scoring.dashboard

import android.os.Bundle
import android.view.View
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.rmaprojects.core.ui.theme.EduWatchTheme
import com.rmaprojects.core.utils.buildSnackbar
import com.rmaprojects.teachers.R
import com.rmaprojects.teachers.components.StudentsCard
import com.rmaprojects.teachers.databinding.FragmentScoringDashboardBinding
import com.rmaprojects.teachers.presentation.student_scoring.dashboard.event.DashboardScoringEvent
import com.rmaprojects.teachers.presentation.student_scoring.input.StudentScoringInputFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StudentScoringDashboardFragment : Fragment(R.layout.fragment_scoring_dashboard) {

    private val binding: FragmentScoringDashboardBinding by viewBinding()
    private val viewModel: StudentScoringDashboardViewModel by viewModels()

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            viewModel.getClassYear()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.getClassYearState.collectLatest { event ->
                when (event) {
                    is DashboardScoringEvent.Error -> {
                        buildSnackbar(
                            binding.root,
                            event.message
                        ).show()
                    }
                    is DashboardScoringEvent.Loading -> {
                        buildSnackbar(
                            binding.root,
                            "Mendapatkan Kelas..."
                        ).show()
                        binding.inputClass.isEnabled = false
                    }
                    is DashboardScoringEvent.Success -> {
                        buildSnackbar(
                            binding.root,
                            "Berhasil mendapatkan kelas!"
                        ).show()

                        val classYear = viewModel.classYearList.value.map {
                            "${it.grade} - ${it.vocation} ${it.year}"
                        }
                        binding.inputClass.isEnabled = true
                        binding.inputClass.setSimpleItems(classYear.toTypedArray())
                        binding.inputClass.setOnItemClickListener { _, _, position, _ ->
                            viewModel.setClassYearId(
                                viewModel.classYearList.value[position].id
                            )
                            viewModel.getStudentListByClassYear(viewModel.classYearId.value!!)
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.uiState.collectLatest { event ->
                when (event) {
                    is DashboardScoringEvent.Error -> {
                        buildSnackbar(
                            binding.root,
                            event.message
                        ).show()
                    }
                    is DashboardScoringEvent.Loading -> {
                        binding.progressCircular.isVisible = true
                    }
                    is DashboardScoringEvent.Success -> {
                        binding.progressCircular.isVisible = false
                    }
                }
            }
        }

        binding.studentListLayout.setContent {
            EduWatchTheme {
                Surface(
                    color = MaterialTheme.colorScheme.surface
                ) {
                    AnimatedVisibility(visible = viewModel.studentList.isNotEmpty()) {
                        StudentListLayout()
                    }
                }
            }
        }
    }

    @Composable
    fun StudentListLayout(
        modifier: Modifier = Modifier
    ) {
        LazyColumn(
            modifier = modifier
        ) {
            items(viewModel.studentList) { students ->
                StudentsCard(
                    name = students.nameStudent,
                    nis = students.nis,
                    onClick = {
                        val bundle = bundleOf(
                            StudentScoringInputFragment.STUDENT_YEAR_ID to students.id
                        )
                        findNavController().navigate(R.id.action_nav_scoring_to_studentScoringDashboardInput, bundle)
                    }
                )
            }
        }
    }
}