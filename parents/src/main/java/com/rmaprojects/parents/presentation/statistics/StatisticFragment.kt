package com.rmaprojects.parents.presentation.statistics

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.pushpal.jetlime.data.JetLimeItemsModel
import com.pushpal.jetlime.data.config.IconType
import com.pushpal.jetlime.data.config.JetLimeItemConfig
import com.rmaprojects.core.ui.theme.EduWatchTheme
import com.rmaprojects.core.utils.buildSnackbar
import com.rmaprojects.parents.R
import com.rmaprojects.parents.components.TimelineView
import com.rmaprojects.parents.databinding.FragmentStatisticsBinding
import com.rmaprojects.parents.presentation.statistics.event.StatisticEvents
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@AndroidEntryPoint
class StatisticFragment : Fragment(R.layout.fragment_statistics) {

    private val binding: FragmentStatisticsBinding by viewBinding()
    private val viewModel: StatisticViewModel by viewModels()

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            viewModel.fetchAttendances()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.uiState.collectLatest { event ->
                when (event) {
                    is StatisticEvents.Error -> {
                        buildSnackbar(
                            binding.root,
                            event.message
                        ).show()
                        Log.d("ERR", event.message)
                    }
                    is StatisticEvents.Loading -> {
                        binding.progressLoading.isVisible = true
                    }
                    is StatisticEvents.Success -> {
                        binding.progressLoading.isVisible = false
                        val listAttendance = viewModel.attendanceList.value

                        var attendCount = 0
                        var alphaCount = 0
                        var permissionCount = 0

                        listAttendance.forEach { item ->
                            when (item.status) {
                                "Hadir" -> attendCount++
                                "Izin" -> permissionCount++
                                "Alpha" -> alphaCount++
                            }
                        }

                        binding.txtAlpha.text = "Alpha: $alphaCount"
                        binding.txtTxtIzin.text = "Izin: $permissionCount"
                        binding.txtHadir.text = "Hadir: $attendCount"

                        val attendPercentage =
                            listAttendance.size.toFloat() / attendCount.toFloat() * 100F

                        binding.txtPercentage.text = "${attendPercentage.roundToInt()}%"
                        binding.progressBar.setProgress(attendPercentage.roundToInt(), true)
                    }
                }
            }
        }

        binding.layoutTimeline.setContent {

            EduWatchTheme {
                Surface(
                    color = MaterialTheme.colorScheme.surface
                ) {
                    val primaryColorContainer = MaterialTheme.colorScheme.primaryContainer

                    val jetLimesModel = remember {
                        JetLimeItemsModel(
                            list = viewModel.attendanceTimelineList.map { attendance ->
                                JetLimeItemsModel.JetLimeItem(
                                    title = "${attendance.subjectName} - ${attendance.subjectTopic}",
                                    description = "${attendance.clock} | ${attendance.teacherName}",
                                    jetLimeItemConfig = JetLimeItemConfig(
                                        itemHeight = 80.dp,
                                        iconType = IconType.Filled,
                                        iconColor = primaryColorContainer
                                    )
                                )
                            }
                        )
                    }

                    if (viewModel.attendanceTimelineList.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                modifier = Modifier.align(Alignment.Center),
                                text = "Belum/Tidak ada pembelajaran hari ini",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    } else {
                        TimelineView(
                            viewModel.attendanceTimelineList.isNotEmpty(),
                            jetLimesModel
                        )
                    }
                }
            }
        }
    }
}