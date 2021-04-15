package com.lunchfood.ui.main.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lunchfood.R
import com.lunchfood.ui.main.view.calendar.SaturdayDecorator
import com.lunchfood.ui.main.view.calendar.SundayDecorator
import com.prolificinteractive.materialcalendarview.MaterialCalendarView

class HistoryFragment: Fragment() {

    private lateinit var historyView: View
    private lateinit var materialCalendarView: MaterialCalendarView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        historyView = inflater.inflate(R.layout.fragment_history, container, false)
        materialCalendarView = historyView.findViewById(R.id.calendarView)
        materialCalendarView.addDecorators(
            SaturdayDecorator(),
            SundayDecorator()
        )
        return historyView
    }
}