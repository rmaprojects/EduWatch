package com.rmaprojects.parents.presentation.calendar

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.pushpal.jetlime.data.JetLimeItemsModel
import com.pushpal.jetlime.data.config.IconType
import com.pushpal.jetlime.data.config.JetLimeItemConfig
import com.rmaprojects.core.ui.theme.EduWatchTheme
import com.rmaprojects.core.utils.buildSnackbar
import com.rmaprojects.parents.R
import com.rmaprojects.parents.components.TimelineView
import com.rmaprojects.parents.databinding.FragmentCalendarBinding
import com.rmaprojects.parents.presentation.calendar.event.CalendarUiEvent
import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date as JavaDate

@AndroidEntryPoint
class CalendarFragment : Fragment(R.layout.fragment_calendar),
    CollapsibleCalendar.CalendarListener {

    private val binding: FragmentCalendarBinding by viewBinding()
    private val viewModel: CalendarViewModel by viewModels()

    override fun onStart() {
        super.onStart()

        val calendar = Calendar.getInstance()
        val date = JavaDate()
        calendar.time = date
        calendar.add(Calendar.DATE, 1)

        val dateNow = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(JavaDate().time)
        val dateNext = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(calendar.time)

        getTimeline(dateNow, dateNext)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.calendarView.setCalendarListener(this)

        binding.layoutTimeline.setContent {
            EduWatchTheme {
                Surface(
                    color = MaterialTheme.colorScheme.surface
                ) {
                    viewModel.uiState.collectAsState(initial = CalendarUiEvent.EmptyTimeline).value.let { event ->
                        when (event) {
                            is CalendarUiEvent.EmptyTimeline -> {
                                Box(
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Text(
                                        text = "Hari ini kosong atau tidak ada",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                            is CalendarUiEvent.Error -> {
                                buildSnackbar(
                                    binding.root,
                                    "Gagal mendapatkan timeline hari ini."
                                ).show()
                            }
                            is CalendarUiEvent.Loading -> {
                                Box(
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }
                            is CalendarUiEvent.Success -> {
                                LearningTimeline()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onClickListener() {}

    override fun onDataUpdate() {}

    override fun onDayChanged() {}

    override fun onDaySelect() {}

    override fun onItemClick(v: View) {
        val selectedDay = binding.calendarView.selectedDay ?: return

        val day = selectedDay.day
        val month = selectedDay.month + 1
        val yearMonth = selectedDay.year

        val dateNow = "$yearMonth-$month-$day"
        val calendar = Calendar.getInstance()
        val date = JavaDate()
        calendar.time = date
        calendar.add(Calendar.DATE, 1)

        val dateNext = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(calendar.time)

        getTimeline(dateNow, dateNext)
    }

    override fun onMonthChange() {}

    override fun onWeekChange(position: Int) {}

    @Composable
    fun LearningTimeline(
        modifier: Modifier = Modifier
    ) {

        val primaryColorContainer = MaterialTheme.colorScheme.primaryContainer

        val jetLimesModel = remember {
            JetLimeItemsModel(
                list = viewModel.attendanceList.map { attendance ->
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

        TimelineView(
            modifier = modifier,
            visibilityValidator = viewModel.attendanceList.isNotEmpty(),
            jetLimesModel = jetLimesModel,
        )
    }

    private fun getTimeline(
        dateNow: String,
        dateNext: String
    ) {
        viewModel.setSelectedDate(
            Date(
                dateNow, dateNext
            )
        )
        viewModel.getTimeLine()
    }
}