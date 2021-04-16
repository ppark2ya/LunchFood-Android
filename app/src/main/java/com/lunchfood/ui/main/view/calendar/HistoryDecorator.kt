package com.lunchfood.ui.main.view.calendar

import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class HistoryDecorator(private val date: CalendarDay, private val placeName: String) : DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return day == date
    }

    override fun decorate(view: DayViewFacade?) {
        view?.addSpan(AddTextToDates(placeName))
    }
}