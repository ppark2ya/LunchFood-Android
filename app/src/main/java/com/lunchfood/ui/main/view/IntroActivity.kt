package com.lunchfood.ui.main.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lunchfood.R
import com.lunchfood.ui.base.BaseActivity
import com.lunchfood.ui.base.GlobalApplication
import com.lunchfood.utils.Dlog
import com.lunchfood.utils.PreferenceManager
import com.lunchfood.utils.Status
import kotlinx.coroutines.launch

class IntroActivity : BaseActivity(TransitionMode.HORIZON) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        val userId = PreferenceManager.getLong("userId")
        getAccount(userId = userId)
    }

    private fun getAccount(userId: Long) {
        GlobalApplication.getViewModel()!!.getAccount(id = userId).observe(this, {
            it?.let { resource ->
                when(resource.status) {
                    Status.PENDING -> {
                        loadingStart()
                    }
                    Status.SUCCESS -> {
                        loadingEnd()
                        resource.data?.let { res ->
                            val resUserId = res.result.id
                            // 조건 변경 필요
                            if(resUserId != null && resUserId != 0L) {
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                            } else {
                                val intent = Intent(this, KakaoLoginActivity::class.java)
                                startActivity(intent)
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