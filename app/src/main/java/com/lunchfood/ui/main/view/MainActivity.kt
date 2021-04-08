package com.lunchfood.ui.main.view

import android.os.Bundle
import com.lunchfood.R
import com.lunchfood.ui.base.BaseActivity

class MainActivity: BaseActivity(TransitionMode.HORIZON) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}