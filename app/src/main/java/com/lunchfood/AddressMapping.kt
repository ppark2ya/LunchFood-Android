package com.lunchfood

import android.os.Bundle
import com.lunchfood.common.BaseActivity
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