package com.lunchfood.ui.main.view

import android.content.Intent
import android.os.Bundle
import com.lunchfood.R
import com.lunchfood.data.model.User
import com.lunchfood.ui.base.BaseActivity
import com.lunchfood.ui.base.GlobalApplication
import com.lunchfood.utils.Dlog
import com.lunchfood.utils.PreferenceManager
import com.lunchfood.utils.Status

class IntroActivity : BaseActivity(TransitionMode.HORIZON) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        val userId = PreferenceManager.getLong("userId")
        getAccount(User(userId))
    }

    private fun getAccount(data: User) {
        GlobalApplication.getViewModel()!!.getAccount(data).observe(this, {
            it?.let { resource ->
                when(resource.status) {
                    Status.PENDING -> {
                        loadingStart()
                    }
                    Status.SUCCESS -> {
                        loadingEnd()
                        resource.data?.let { res ->
                            if(res.resultCode == 200) {
                                val intent = Intent(this, MainActivity::class.java)
                                intent.putExtra("x", res.data!!.x)   // 위도
                                intent.putExtra("y", res.data.y)  // 경도
                                intent.putExtra("roadAddr", res.data.address)   // 위도
                                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                            } else {
                                val intent = Intent(this, KakaoLoginActivity::class.java)
                                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                            }
                        }
                    }
                    Status.FAILURE -> {
                        loadingEnd()
                        Dlog.e("getAccount FAILURE : ${it.message}")
                    }
                }
            }
        })
    }
}