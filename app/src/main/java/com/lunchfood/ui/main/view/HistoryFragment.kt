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
        getPlaceHistory(HistoryParam(id = userId, year = "2021", month = "04", intervalDate = 0))
    }

    private fun setupCalendarView() {
        materialCalendarView = historyView.findViewById(R.id.calendarView)
        materialCalendarView.selectedDate = CalendarDay.today()

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
                                mCalendarDataList = res.data
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
}