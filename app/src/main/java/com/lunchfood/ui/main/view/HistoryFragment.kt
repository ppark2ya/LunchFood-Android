package com.lunchfood.ui.main.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lunchfood.R
import com.lunchfood.data.model.HistoryParam
import com.lunchfood.data.model.HistoryRequest
import com.lunchfood.data.model.HistoryResponse
import com.lunchfood.ui.base.BaseFragment
import com.lunchfood.ui.base.GlobalApplication
import com.lunchfood.ui.main.view.calendar.HistoryDecorator
import com.lunchfood.ui.main.view.calendar.SaturdayDecorator
import com.lunchfood.ui.main.view.calendar.SundayDecorator
import com.lunchfood.utils.Constants
import com.lunchfood.utils.Dlog
import com.lunchfood.utils.Status
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import org.threeten.bp.LocalDate

class HistoryFragment: BaseFragment() {

    private lateinit var historyView: View
    private lateinit var materialCalendarView: MaterialCalendarView
    private var mCalendarDataList: List<HistoryResponse>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        historyView = inflater.inflate(R.layout.fragment_history, container, false)
        return historyView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        materialCalendarView = historyView.findViewById(R.id.calendarView)

        val now = CalendarDay.today()
        materialCalendarView.selectedDate = now
        getPlaceHistory(
            HistoryParam(
                id = userId,
                year = now.year.toString(),
                month = now.month.toString(),
                intervalDate = 0
            )
        )
        setupCalendarEventListener()
    }

    private fun setupCalendarView() {
        val list = mutableListOf(SaturdayDecorator(), SundayDecorator())
        mCalendarDataList?.let {
            for(hist in it) {
                val yyyy = hist.insertedDate.substring(0, 4)
                val mm = hist.insertedDate.substring(5, 7)
                val dd = hist.insertedDate.substring(8, 10)
                val date = CalendarDay.from(yyyy.toInt(), mm.toInt(), dd.toInt())
                list.add(HistoryDecorator(date, hist.placeName))
            }
        }

        materialCalendarView.addDecorators(list)
        materialCalendarView.invalidateDecorators()
    }

    private fun getPlaceHistory(data: HistoryParam) {
        GlobalApplication.getViewModel()!!.getPlaceHistory(data).observe(viewLifecycleOwner, {
            it?.let { resource ->
                when(resource.status) {
                    Status.PENDING -> {
                        mainActivity.loadingStart()
                    }
                    Status.SUCCESS -> {
                        mainActivity.loadingEnd()
                        resource.data?.let { res ->
                            if(res.resultCode == 200) {
                                mCalendarDataList = res.data as List<HistoryResponse>
                                setupCalendarView()
                            }
                        }
                    }
                    Status.FAILURE -> {
                        mainActivity.loadingEnd()
                        Dlog.e("getPlaceHistory FAILURE : ${it.message}")
                    }
                }
            }
        })
    }

    private fun setupCalendarEventListener() {
        // 날짜 선택 시 이벤트
        materialCalendarView.setOnDateChangedListener { widget, date, selected ->
            val year = date.year
            val month = date.month
            val day = date.day

            Dlog.i("연: $year, 월: $month, 일: $day")
        }

        // 달력 넘어갈 때 이벤트
        materialCalendarView.setOnMonthChangedListener { widget, date ->
            getPlaceHistory(
                HistoryParam(
                    id = userId,
                    year = date.year.toString(),
                    month = date.month.toString(),
                    intervalDate = 0
                )
            )
        }
    }
}