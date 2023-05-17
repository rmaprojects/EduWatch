package com.rmaprojects.parents.presentation.scoring.tabs

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.rmaprojects.core.ui.component.SubjectListAdapter
import com.rmaprojects.core.utils.buildSnackbar
import com.rmaprojects.parents.R
import com.rmaprojects.parents.databinding.FragmentScorePtsBinding
import com.rmaprojects.parents.presentation.scoring.ScoringViewModel
import com.rmaprojects.parents.presentation.scoring.event.ScoringUiEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PtsFragment: Fragment(R.layout.fragment_score_pts) {

    private val viewModel: ScoringViewModel by viewModels()
    private val binding: FragmentScorePtsBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.ptsUiEvent.collectLatest { event ->
                when (event) {
                    is ScoringUiEvent.EmptyList -> {
                        binding.progressCircular.isVisible = false
                        binding.txtEmpty.isVisible = true
                    }
                    is ScoringUiEvent.Error -> {
                        binding.progressCircular.isVisible = false
                        binding.txtEmpty.isVisible = false
                        buildSnackbar(
                            binding.root,
                            event.message
                        ).show()
                    }
                    is ScoringUiEvent.Loading -> {
                        binding.progressCircular.isVisible = true
                        binding.txtEmpty.isVisible = false
                    }
                    is ScoringUiEvent.Success -> {

                        val gradesList = viewModel.pasGradesList

                        if (gradesList.isEmpty()) {
                            binding.progressCircular.isVisible = false
                            binding.txtEmpty.isVisible = true
                        } else {
                            binding.progressCircular.isVisible = false
                            binding.txtEmpty.isVisible = false
                            binding.rvScorePts.adapter = SubjectListAdapter(
                                viewModel.ptsGradesList,
                                null
                            )
                        }
                    }
                }
            }
        }
    }
}