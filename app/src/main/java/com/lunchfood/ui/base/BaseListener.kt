package com.lunchfood.ui.base

import android.view.View

interface BaseListener {
    fun<T> onItemClickListener(v: View, item: T, pos: Int)
}