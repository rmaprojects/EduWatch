package com.rmaprojects.teachers.presentation.attendance

import android.os.Bundle
import android.view.View
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.rmaprojects.core.ui.theme.EduWatchTheme
import com.rmaprojects.core.utils.buildSnackbar
import com.rmaprojects.teachers.R
import com.rmaprojects.teachers.SharedViewModel
import com.rmaprojects.teachers.components.StudentAttendanceCard
import com.rmaprojects.teachers.databinding.FragmentAttendanceBinding
import com.rmaprojects.teachers.presentation.attendance.event.AttendanceEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AttendanceFragment : Fragment(R.layout.fragment_attendance) {

    private val binding: FragmentAttendanceBinding by viewBinding()
    private val viewModel: AttendanceViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onStart() {
        super.onStart()
        viewModel.getClassYearList()
        viewModel.getSubjectList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.studentListView.setContent {
            EduWatchTheme {
                Surface(
                    color = MaterialTheme.colorScheme.surface
                ) {
                    AttendanceStudentList()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.getClassYearEvent.collectLatest { event ->
                when (event) {
                    is AttendanceEvent.Error -> {
                        buildSnackbar(
                            binding.root,
                            event.message
                        ).show()
                    }
                    is AttendanceEvent.Loading -> {
                        buildSnackbar(
                            binding.root,
                            "Sedang mendapatkan list kelas..."
                        ).show()
                    }
                    is AttendanceEvent.Success -> {
                        buildSnackbar(
                            binding.root,
                            "Berhasil mendapatkan list kelas"
                        ).show()
                        val classList =
                            viewModel.classYearList.value.map { "${it.grade} - ${it.vocation}" }
                        binding.inputClass.setSimpleItems(classList.toTypedArray())
                        binding.inputClass.setOnItemClickListener { _, _, position, _ ->
                            viewModel.setSelectedClassYear(
                                viewModel.classYearList.value[position].id
                            )
                            viewModel.getStudentList()
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.getSubjectEvent.collectLatest { event ->
                when (event) {
                    is AttendanceEvent.Error -> {
                        buildSnackbar(
                            binding.root,
                            event.message
                        ).show()
                    }
                    is AttendanceEvent.Loading -> {
                        buildSnackbar(
                            binding.root,
                            "Sedang mendapatkan list pelajaran..."
                        ).show()
                    }
                    is AttendanceEvent.Success -> {
                        val subjectList = viewModel.subjectList.value.map { it.subjectName }
                        binding.inputSubject.setSimpleItems(subjectList.toTypedArray())
                        binding.inputSubject.setOnItemClickListener { _, _, position, _ ->
                            viewModel.setSelectedSubject(
                                viewModel.subjectList.value[position].id
                            )
                        }
                    }
                }
            }
        }

        binding.inputTopic.editText?.doOnTextChanged { text, _, _, _ ->
            viewModel.setTopic("$text")
        }

        lifecycleScope.launch {
            viewModel.getStudentEvent.collectLatest { event ->
                when (event) {
                    is AttendanceEvent.Error -> {
                        buildSnackbar(
                            binding.root,
                            event.message
                        ).show()
                    }
                    is AttendanceEvent.Loading -> {
                        buildSnackbar(
                            binding.root,
                            "Sedang mendapatkan list murid..."
                        ).show()
                    }
                    is AttendanceEvent.Success -> {
                        buildSnackbar(
                            binding.root,
                            "Berhasil mendapatkan list murid"
                        ).show()
                    }
                }
            }
        }

        binding.btnSubmit.setOnClickListener {
            val longitude = sharedViewModel.location.value?.longitude
            val latitude = sharedViewModel.location.value?.latitude

            if (viewModel.studentList.isEmpty()) {
                buildSnackbar(
                    binding.root,
                    "Siswa masih kosong"
                ).show()
                return@setOnClickListener
            }

            viewModel.submitAttendance(
                longitude = longitude,
                latitude = latitude
            )
        }

        lifecycleScope.launch {
            viewModel.uiEvent.collectLatest { event ->
                when (event) {
                    is AttendanceEvent.Error -> {
                        buildSnackbar(
                            binding.root,
                            event.message
                        ).show()
                        clearInput()
                    }
                    is AttendanceEvent.Loading -> {
                        with(binding) {
                            btnSubmit.isEnabled = false
                            inputClass.isEnabled = false
                            inputTopic.isEnabled = false
                            inputSubject.isEnabled = false
                        }
                    }
                    is AttendanceEvent.Success -> {
                        with(binding) {
                            btnSubmit.isEnabled = true
                            inputClass.isEnabled = true
                            inputTopic.isEnabled = true
                            inputSubject.isEnabled = true
                            clearInput()
                        }
                    }
                }
            }
        }
    }

    private fun clearInput() = with(binding) {
        inputTopic.editText?.setText("")
    }

    @Composable
    fun AttendanceStudentList() {
        AnimatedVisibility(visible = viewModel.studentList.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(viewModel.studentList) { student ->
                    StudentAttendanceCard(
                        modifier = Modifier.padding(12.dp),
                        studentName = student.studentName,
                        studentNis = "${student.studentNis}",
                        studentYearId = student.studentYearId,
                        onDropdownChange = { status, studentYearId ->
                            viewModel.changeStudentStatus(studentYearId, status)
                        }
                    )
                }
            }
        }
    }
}