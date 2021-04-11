package com.lunchfood.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lunchfood.R
import com.lunchfood.data.api.ApiHelper
import com.lunchfood.data.api.RetrofitBuilder
import com.lunchfood.ui.main.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class BaseFragment: Fragment(), CoroutineScope {
    private lateinit var job: Job
    private lateinit var viewModel: MainViewModel

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
        setupViewModel()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
                this,
                ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(MainViewModel::class.java)
        // 현재 뷰에 대한 viewModel 전역상태로 관리
        GlobalApplication.setViewModel(viewModel)
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    fun loadingStart() {
        launch {
            GlobalApplication.instance.loadingStart(activity)
        }
    }

    fun loadingEnd() {
        launch {
            GlobalApplication.instance.loadingEnd()
        }
    }
}