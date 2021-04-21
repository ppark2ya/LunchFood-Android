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
import com.lunchfood.ui.main.view.MainActivity
import com.lunchfood.ui.main.viewmodel.MainViewModel
import com.lunchfood.utils.PreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class BaseFragment: Fragment(), CoroutineScope {
    private lateinit var job: Job
    lateinit var mainActivity: MainActivity
    val userId by lazy { PreferenceManager.getLong("userId") }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
        mainActivity = activity as MainActivity
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}