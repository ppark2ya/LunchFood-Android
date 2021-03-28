package com.lunchfood.ui.base

import android.app.Activity
import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.lunchfood.R
import com.lunchfood.ui.main.viewmodel.MainViewModel

class GlobalApplication: Application() {
    companion object {
        private var instance: GlobalApplication? = null
        private var currentActivity: Activity? = null
        private var viewModel: MainViewModel? = null

        fun getCurrentActivity(): Activity? {
            return currentActivity
        }

        fun setCurrentActivity(currentActivity: Activity) {
            Companion.currentActivity = currentActivity
        }

        fun getGlobalApplicationContext(): GlobalApplication? {
            if(instance === null) throw IllegalStateException("this application does not inherit com.lunchfood.GlobalApplication")
            return instance
        }

        fun setViewModel(viewModel: MainViewModel) {
            Companion.viewModel = viewModel
        }

        fun getViewModel(): MainViewModel? {
            if(viewModel === null) throw IllegalStateException("this application does not inherit com.lunchfood.GlobalApplication")
            return viewModel
        }
    }

    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, getString(R.string.kakao_app_key))
    }

    override fun onTerminate() {
        super.onTerminate()
        instance = null
    }
}