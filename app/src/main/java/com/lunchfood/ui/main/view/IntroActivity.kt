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

        loadingStart()
        val userId = PreferenceManager.getLong("userId")
        getAccount(User(userId))
    }

    private fun getAccount(data: User) {
        GlobalApplication.getViewModel()!!.getAccount(data).observe(this, {
            it?.let { resource ->
                when(resource.status) {
                    Status.PENDING -> {}
                    Status.SUCCESS -> {
                        loadingEnd()
                        resource.data?.let { res ->
                            if(res.resultCode == 200) {
                                val intent = Intent(this, MainActivity::class.java)
                                intent.putExtra("lon", res.data!!.x!!.toDouble())   // 경도
                                intent.putExtra("lat", res.data.y!!.toDouble())  // 위도
                                intent.putExtra("roadAddr", res.data.address)   // 주소
                                // 1번 호출 후 스택에서 제거해서 뒤로가기 방지
                                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                            } else {
                                val intent = Intent(this, KakaoLoginActivity::class.java)
                                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
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