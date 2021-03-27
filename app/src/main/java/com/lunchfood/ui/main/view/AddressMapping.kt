package com.lunchfood.ui.main.view

import android.os.Bundle
import com.lunchfood.R
import com.lunchfood.ui.base.BaseActivity
import kotlinx.android.synthetic.main.header.*

class AddressMapping : BaseActivity(TransitionMode.HORIZON) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.address_setting_activity)

        headerBackBtn.setOnClickListener {
            onBackPressed()
        }
    }

}