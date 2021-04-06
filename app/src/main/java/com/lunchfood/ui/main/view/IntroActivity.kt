package com.lunchfood.ui.main.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lunchfood.R
import com.lunchfood.ui.base.BaseActivity

class IntroActivity : BaseActivity(TransitionMode.HORIZON) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

//        val intent = Intent(this, KakaoLoginActivity::class.java)
//        startActivity(intent)
    }

}