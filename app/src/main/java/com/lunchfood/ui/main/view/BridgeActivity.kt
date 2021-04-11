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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BridgeActivity: BaseActivity(TransitionMode.HORIZON) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bridge)
        loadingStart()
    }

    override fun onResume() {
        super.onResume()

        val userId = PreferenceManager.getLong("userId")
        getAccount(User(userId))
    }

    private fun getAccount(data: User) {
        GlobalApplication.getViewModel()!!.getAccount(data).observe(this, {
            it?.let { resource ->
                when(resource.status) {
                    Status.PENDING -> {}
                    Status.SUCCESS -> {
                        resource.data?.let { res ->
                            if(res.resultCode == 200) {
                                val user = res.data
                                launch {
                                    delay(1000)
                                    forwardMainPage(user!!.x!!, user.y!!, user.address!!)
                                }
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

    private fun forwardMainPage(x: String, y: String, address: String) {
        val intent = Intent(this@BridgeActivity, MainActivity::class.java)
        intent.putExtra("x", x)   // 위도
        intent.putExtra("y", y)  // 경도
        intent.putExtra("roadAddr", address)   // 위도
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
    }

    override fun onStop() {
        super.onStop()
        loadingEnd()
    }
}