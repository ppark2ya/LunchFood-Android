package com.lunchfood.ui.base

import android.app.Activity
import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.lunchfood.R

class GlobalApplication: Application() {
    companion object {
        private var instance: GlobalApplication? = null
        private var currentActivity: Activity? = null

        fun getCurrentActivity(): Activity? {
            return currentActivity
        }

        fun setCurrentActivity(currentActivity: Activity) {
            Companion.currentActivity = currentActivity
        }

        fun getGlobalApplicationContext(): GlobalApplication? {
            if(instance === null) throw IllegalStateException("this application does not inherit com.kakao.GlobalApplication")
            return instance
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