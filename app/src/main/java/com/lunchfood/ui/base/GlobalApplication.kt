package com.lunchfood.ui.base

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.kakao.sdk.common.KakaoSdk
import com.lunchfood.R
import com.lunchfood.ui.main.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.dialog_loading.*

class GlobalApplication: Application() {
    companion object {
        lateinit var instance: GlobalApplication
            private set
        private var viewModel: MainViewModel? = null
        private var loadingDialog: AppCompatDialog? = null

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
        instance = this
    }

    @SuppressLint("CheckResult")
    fun loadingStart(activity: Activity?) {
        if(activity == null || activity.isFinishing) {
            return;
        }

        loadingDialog = AppCompatDialog(activity)
        loadingDialog?.setCancelable(false)
        loadingDialog?.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        loadingDialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        loadingDialog?.setContentView(R.layout.dialog_loading)
        loadingDialog?.show()

        val loadingGif = loadingDialog?.ivLoadingGif
        if (loadingGif != null) {
            Glide.with(this)
                .asGif()
                .load(R.raw.loading)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(loadingGif)
        }
    }

    fun loadingEnd() {
        if (loadingDialog != null && loadingDialog?.isShowing == true) {
            loadingDialog?.dismiss();
        }

    }

    val context: Context
        get() = applicationContext
}
