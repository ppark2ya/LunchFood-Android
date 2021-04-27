package com.lunchfood.ui.main.view

import android.os.Bundle
import com.lunchfood.R
import com.lunchfood.ui.base.BaseActivity
import com.lunchfood.ui.base.GlobalApplication

class MyPlaceActivity : BaseActivity(TransitionMode.HORIZON) {

    private val mainViewModel by lazy { GlobalApplication.getViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myplace)
        setCommonHeaderText(getString(R.string.frequently_used_restaurants))
    }

}