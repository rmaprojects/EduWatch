package com.rmaprojects.parents.presentation.scoring

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import com.rmaprojects.core.utils.buildSnackbar
import com.rmaprojects.parents.R
import com.rmaprojects.parents.databinding.FragmentScoringBinding
import com.rmaprojects.parents.presentation.scoring.event.ScoringUiEvent
import com.rmaprojects.parents.presentation.scoring.tabs.PasFragment
import com.rmaprojects.parents.presentation.scoring.tabs.PtsFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ScoringFragment : Fragment(R.layout.fragment_scoring) {

    private val binding: FragmentScoringBinding by viewBinding()
    private val viewModel: ScoringViewModel by viewModels()

    override fun onStart() {
        super.onStart()
        viewModel.fetchGrades()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.fetchGradeUiEvent.collectLatest { event ->
                when (event) {
                    is ScoringUiEvent.EmptyList -> {
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Anak anda belum dinilai")
                            .setMessage("Silahkan kembali lagi setelah mendapatkan informasi lanjut")
                            .setPositiveButton("Ok") { _, _ -> }
                            .create()
                            .show()
                    }
                    is ScoringUiEvent.Error -> {
                        buildSnackbar(
                            binding.root,
                            event.message
                        ).show()
                    }
                    is ScoringUiEvent.Loading -> {
                        buildSnackbar(
                            binding.root,
                            "Mengambil data kelas siswa..."
                        ).show()
                    }
                    is ScoringUiEvent.Success -> {

                        val classYearIdList = viewModel.classYearIdList.map {
                            "${it.classGrade} - ${it.classVocation} ${it.classYear}"
                        }

                        if (classYearIdList.isNotEmpty()) {
                            buildSnackbar(
                                binding.root,
                                "Berhasil mengambil data siswa, silahkan pilih kelas untuk melihat penilaian!"
                            ).show()
                        } else {
                            buildSnackbar(
                                binding.root,
                                "Data kosong"
                            ).show()
                        }

                        binding.inputClassYear.setSimpleItems(
                            classYearIdList.toTypedArray()
                        )

                        Log.d("LIST", viewModel.classYearIdList.toList().toString())
                        binding.inputClassYear.setOnItemClickListener { _, _, position, _ ->
                            val classModel = viewModel.classYearIdList[position]
                            viewModel.setClassYearId(
                                classModel.classId
                            )
                            binding.inputClassYear.setText("${classModel.classGrade} - ${classModel.classVocation} ${classModel.classYear}")
                        }
                    }
                }
            }
        }

        binding.viewPager.adapter = TabAdapter(
            requireActivity(),
            listOf(
                PasFragment(),
                PtsFragment()
            )
        )

        binding.viewPager.registerOnPageChangeCallback(
            object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    when (position) {
                        0 -> viewModel.fetchPtsGrades()
                        1 -> viewModel.fetchPasGrades()
                    }
                }
            }
        )

        TabLayoutMediator(binding.tabLayout, binding.viewPager, true, true) { tab, position ->
            when (position) {
                0 -> tab.text = "PTS"
                1 -> tab.text = "PAS"
            }
        }

    }
}