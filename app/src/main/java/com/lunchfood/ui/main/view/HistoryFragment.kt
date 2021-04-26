package com.lunchfood.ui.main.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.lunchfood.R
import com.lunchfood.data.model.history.HistoryParam
import com.lunchfood.data.model.history.HistoryResponse
import com.lunchfood.ui.base.BaseFragment
import com.lunchfood.ui.base.GlobalApplication
import com.lunchfood.ui.main.view.calendar.HistoryDecorator
import com.lunchfood.ui.main.view.calendar.SaturdayDecorator
import com.lunchfood.ui.main.view.calendar.SundayDecorator
import com.lunchfood.ui.main.view.history.MenuRegistActivity
import com.lunchfood.utils.Dlog
import com.lunchfood.utils.Status
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import org.threeten.bp.LocalDate

class HistoryFragment: BaseFragment() {

    private val mainViewModel by lazy { GlobalApplication.getViewModel() }
    private lateinit var materialCalendarView: MaterialCalendarView
    private var mCalendarDataList: List<HistoryResponse>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        materialCalendarView = view.findViewById(R.id.calendarView)
        mainActivity.run {
            view.findViewById<ImageView>(R.id.headerBackBtn).visibility = View.GONE
            setCommonHeaderText(getString(R.string.menu_history), view)
        }

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
        mCalendarDataList?.forEach { hist ->
            val date = CalendarDay.from(LocalDate.parse(hist.insertedDate))
            list.add(HistoryDecorator(date, hist.placeName))
        }

        materialCalendarView.addDecorators(list)
        materialCalendarView.invalidateDecorators()
    }

    private fun getPlaceHistory(data: HistoryParam) {
        mainViewModel?.run {
            getPlaceHistory(data).observe(viewLifecycleOwner, {
                it.let { resource ->
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