package com.lunchfood.ui.main.view

import android.content.Intent
import android.os.Bundle
import com.lunchfood.R
import com.lunchfood.ui.base.BaseActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BridgeActivity: BaseActivity(TransitionMode.HORIZON) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bridge)

        loadingStart()
        launch {
            delay(1000)
            forwardMainPage()
        }
    }

    private fun forwardMainPage() {
        val param = intent
        val intent = Intent(this@BridgeActivity, MainActivity::class.java)
        intent.putExtra("x", param.getStringExtra("x"))   // 위도
        intent.putExtra("y", param.getStringExtra("y"))  // 경도
        intent.putExtra("roadAddr", param.getStringExtra("roadAddr"))   // 위도
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
    }

    override fun onStop() {
        super.onStop()
        loadingEnd()
    }
}