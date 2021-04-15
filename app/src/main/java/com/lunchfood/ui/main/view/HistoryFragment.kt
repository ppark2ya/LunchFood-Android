package com.lunchfood.ui.main.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lunchfood.R
import com.lunchfood.data.model.HistoryParam
import com.lunchfood.data.model.HistoryRequest
import com.lunchfood.ui.base.BaseFragment
import com.lunchfood.ui.base.GlobalApplication
import com.lunchfood.ui.main.view.calendar.SaturdayDecorator
import com.lunchfood.ui.main.view.calendar.SundayDecorator
import com.lunchfood.utils.Constants
import com.lunchfood.utils.Dlog
import com.lunchfood.utils.Status
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView

class HistoryFragment: BaseFragment() {

    private lateinit var historyView: View
    private lateinit var materialCalendarView: MaterialCalendarView

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
        getPlaceHistory(HistoryParam(id = userId))
        setupCalendarView()
    }

    private fun setupCalendarView() {
        materialCalendarView = historyView.findViewById(R.id.calendarView)
        materialCalendarView.selectedDate = CalendarDay.today()
        materialCalendarView.addDecorators(
            SaturdayDecorator(),
            SundayDecorator()
        )
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
                                Dlog.i("달력 데이터:: ${res.data}")
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
}