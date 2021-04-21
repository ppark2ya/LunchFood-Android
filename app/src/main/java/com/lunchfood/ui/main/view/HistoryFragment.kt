package com.lunchfood.ui.main.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lunchfood.R
import com.lunchfood.data.model.HistoryParam
import com.lunchfood.data.model.HistoryResponse
import com.lunchfood.ui.base.BaseFragment
import com.lunchfood.ui.base.GlobalApplication
import com.lunchfood.ui.main.view.calendar.HistoryDecorator
import com.lunchfood.ui.main.view.calendar.SaturdayDecorator
import com.lunchfood.ui.main.view.calendar.SundayDecorator
import com.lunchfood.utils.Dlog
import com.lunchfood.utils.Status
import com.prolificinteractive.materialcalendarview.CalendarDay
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
        materialCalendarView = historyView.findViewById(R.id.calendarView)
        mainActivity.run { setCommonHeaderText(getString(R.string.menu_history), historyView) }

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

        return historyView
    }

    private fun setupCalendarView() {
        val list = mutableListOf(SaturdayDecorator(), SundayDecorator())
        mCalendarDataList?.forEach { hist ->
            val date = CalendarDay.from(LocalDate.parse(hist.insertedDate))
            list.add(HistoryDecorator(date, hist.placeName))
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
            val selectedDate = date.date.toString()
            val dayMenu = mCalendarDataList?.filter {
                it.insertedDate == selectedDate
            }

            if(!dayMenu.isNullOrEmpty()) {
                val intent = Intent(mainActivity, MenuRegistActivity::class.java)
                intent.putExtra("dayMenu", dayMenu[0])
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            }
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