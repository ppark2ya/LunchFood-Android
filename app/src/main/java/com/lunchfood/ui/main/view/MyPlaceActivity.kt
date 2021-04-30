package com.lunchfood.ui.main.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.lunchfood.R
import com.lunchfood.data.model.filter.FilterCommonRequest
import com.lunchfood.ui.base.BaseActivity
import com.lunchfood.ui.base.GlobalApplication
import com.lunchfood.utils.Dlog
import com.lunchfood.utils.Status

class MyPlaceActivity : BaseActivity(TransitionMode.HORIZON) {

    private val mainViewModel by lazy { GlobalApplication.getViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myplace)
        setCommonHeaderText(getString(R.string.frequently_used_restaurants))
        getSelectedPlace(FilterCommonRequest(id = userId))
    }

    private fun getSelectedPlace(data: FilterCommonRequest) {
        mainViewModel?.let { model ->
            model.getSelectedPlace(data).observe(this@MyPlaceActivity, {
                it.let { resource ->
                    when (resource.status) {
                        Status.PENDING -> {
                            loadingStart()
                        }
                        Status.SUCCESS -> {
                            loadingEnd()
                            resource.data?.let { res ->
                                if(res.resultCode == 200) {
                                    val intent = Intent(this, BridgeActivity::class.java)
                                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                                } else {
                                    Toast.makeText(this@MyPlaceActivity, getString(R.string.user_update_fail_msg), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        Status.FAILURE -> {
                            loadingEnd()
                            Dlog.e("getSelectedPlace FAILURE : ${it.message}")
                        }
                    }
                }
            })
        }
    }

    private fun deleteSelectedPlace(data: FilterCommonRequest) {
        mainViewModel?.let { model ->
            model.deleteSelectedPlace(data).observe(this@MyPlaceActivity, {
                it.let { resource ->
                    when (resource.status) {
                        Status.PENDING -> {
                            loadingStart()
                        }
                        Status.SUCCESS -> {
                            loadingEnd()
                            resource.data?.let { res ->
                                if(res.resultCode == 200) {
                                    val intent = Intent(this, BridgeActivity::class.java)
                                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                                } else {
                                    Toast.makeText(this@MyPlaceActivity, getString(R.string.user_update_fail_msg), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        Status.FAILURE -> {
                            loadingEnd()
                            Dlog.e("getSelectedPlace FAILURE : ${it.message}")
                        }
                    }
                }
            })
        }
    }
}