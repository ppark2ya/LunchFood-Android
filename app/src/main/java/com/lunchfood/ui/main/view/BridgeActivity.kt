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

    private val mainViewModel by lazy { GlobalApplication.getViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bridge)
        loadingStart()
    }

    override fun onResume() {
        super.onResume()
        getAccount(User(userId))
    }

    private fun getAccount(data: User) {
        mainViewModel?.run {
            getAccount(data).observe(this@BridgeActivity, {
                it?.let { resource ->
                    when(resource.status) {
                        Status.PENDING -> {}
                        Status.SUCCESS -> {
                            resource.data?.let { res ->
                                if(res.resultCode == 200) {
                                    GlobalApplication.mUserData = res.data!!
                                    launch {
                                        delay(1000)
                                        val intent = Intent(this@BridgeActivity, MainActivity::class.java)
                                        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                                    }
                                } else {
                                    val intent = Intent(this@BridgeActivity, KakaoLoginActivity::class.java)
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

    override fun onStop() {
        super.onStop()
        loadingEnd()
    }
}