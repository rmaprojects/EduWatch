package com.rmaprojects.teachers.presentation.student_scoring.input

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.rmaprojects.core.ui.component.EduWatchSegmentedButton
import com.rmaprojects.core.ui.component.SubjectListAdapter
import com.rmaprojects.core.utils.buildSnackbar
import com.rmaprojects.teachers.R
import com.rmaprojects.teachers.databinding.FragmentScoringInputBinding
import com.rmaprojects.teachers.presentation.student_scoring.input.event.InputScoringEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StudentScoringInputFragment: Fragment(R.layout.fragment_scoring_input) {

    private val viewModel: StudentScoringViewModel by viewModels()
    private val binding: FragmentScoringInputBinding by viewBinding()

    override fun onStart() {
        super.onStart()
        viewModel.setGradingType("PTS")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost : MenuHost = requireActivity()

        createContextMenu(menuHost)

        binding.gradingSegmentBtn.setContent {
            val gradingList = listOf("PTS", "PAS")

            EduWatchSegmentedButton(
                items = gradingList,
                onItemSelection = {
                    viewModel.setGradingType(gradingList[it])
                }
            )
        }

        lifecycleScope.launch {
            viewModel.getSubjectEvent.collectLatest { event ->
                when (event) {
                    is InputScoringEvent.Error -> {
                        buildSnackbar(
                            binding.root,
                            event.message
                        ).show()
                    }
                    is InputScoringEvent.Loading -> {
                        buildSnackbar(
                            binding.root,
                            "Sedang mendapatkan list pelajaran..."
                        ).show()
                        binding.progressCircular.isVisible = true
                    }
                    is InputScoringEvent.Success -> {
                        buildSnackbar(
                            binding.root,
                            "List pelajaran didapatkan!"
                        ).show()
                        binding.progressCircular.isVisible = false

                        binding.rvSubjects.adapter = SubjectListAdapter(viewModel.gradingList) { subjectId, inputedGrade ->
                            viewModel.changeGradeInput(subjectId, inputedGrade)
                        }
                        binding.rvSubjects.layoutManager = LinearLayoutManager(requireContext())
                    }
                }
            }
        }
    }

    private fun createContextMenu(menuHost: MenuHost) {
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_input_save, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_save -> {
                        viewModel.submit()
                        true
                    }
                    else -> false
                }
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    companion object {
        const val STUDENT_YEAR_ID = "STUDENT_YEAR_ID"
    }
}