package com.lunchfood.ui.base

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.ViewModelProvider
import com.lunchfood.R
import com.lunchfood.data.api.ApiHelper
import com.lunchfood.data.api.RetrofitBuilder
import com.lunchfood.ui.main.viewmodel.MainViewModel
import com.lunchfood.utils.KeyboardVisibilityUtils
import com.lunchfood.utils.PreferenceManager
import kotlinx.android.synthetic.main.activity_menu_regist.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class BaseActivity(private val transitionMode: TransitionMode = TransitionMode.NONE) : AppCompatActivity(), CoroutineScope {

    enum class TransitionMode {
        NONE,
        HORIZON,
        VERTICAL
    }

    private lateinit var job: Job
    private lateinit var viewModel: MainViewModel
    val userId by lazy { PreferenceManager.getLong("userId") }
    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()

        when (transitionMode) {
            TransitionMode.HORIZON -> overridePendingTransition(R.anim.horizon_enter, R.anim.none)
            TransitionMode.VERTICAL -> overridePendingTransition(R.anim.vertical_enter, R.anim.none)
            else -> Unit
        }

        // 상태바 배경색 transparent 변경 후 상태바 높이만큼 padding을 넣어서 자연스럽게 적용
        window.let {
            it.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN    // FLAG_LAYOUT_NO_LIMITS로 하면 하단 네비게이션바도 transparent 처리됌
            )
            it.decorView.setPadding(0, statusBarHeight(this), 0, 0)
            it.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        /**
         * @desc 키보드가 올라왔을 떄 자동으로 스크롤 up
         *  1. AndroidManifest.xml -> activity -> android:windowSoftInputMode="adjustResize" 필요
         *  2. activity_~.xml 루트 layout에 android:fitsSystemWindows="true" 속성 필요
         *  3. 해당 기능을 사용하기 위해선 NestedScrollView 의 id를 nsvContainer 로 지정
         */
        keyboardVisibilityUtils = KeyboardVisibilityUtils(window,
            onShowKeyboard = { keyboardHeight ->
                nsvContainer?.run {
                    smoothScrollTo(scrollX, scrollY + keyboardHeight)
                }
            }
        )

        setupViewModel()
    }

    override fun finish() {
        super.finish()

        when (transitionMode) {
            TransitionMode.HORIZON -> overridePendingTransition(R.anim.none, R.anim.horizon_exit)
            TransitionMode.VERTICAL -> overridePendingTransition(R.anim.none, R.anim.vertical_exit)
            else -> Unit
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (isFinishing) {
            when (transitionMode) {
                TransitionMode.HORIZON -> overridePendingTransition(R.anim.none, R.anim.horizon_exit)
                TransitionMode.VERTICAL -> overridePendingTransition(R.anim.none, R.anim.vertical_exit)
                else -> Unit
            }
        }
    }

    override fun onDestroy() {
        job.cancel()
        keyboardVisibilityUtils.detachKeyboardListeners()
        super.onDestroy()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
                this,
                ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(MainViewModel::class.java)
        // 현재 뷰에 대한 viewModel 전역상태로 관리
        GlobalApplication.setViewModel(viewModel)
    }

    private fun statusBarHeight(context: Context): Int {
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")

        return if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId)
        else 0
    }

    fun loadingStart() {
        launch {
            GlobalApplication.instance.loadingStart(this@BaseActivity)
        }
    }

    fun loadingEnd() {
        launch {
            GlobalApplication.instance.loadingEnd()
        }
    }

    // 상단 메뉴명을 텍스트뷰로 사용할 때 param 값으로 세팅
    fun setCommonHeaderText(text: String, view: View = window.decorView) {
        val ivHeader = view.findViewById<ImageView>(R.id.ivHeader)
        ivHeader.visibility = View.GONE

        val layout = view.findViewById<RelativeLayout>(R.id.rlCommonHeader)
        val layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        val tvHeader = AppCompatTextView(this)
        tvHeader.text = text
        tvHeader.layoutParams = layoutParams
        tvHeader.gravity = Gravity.CENTER
        tvHeader.setTextColor(Color.BLACK)
        tvHeader.textSize = 18f
        tvHeader.typeface = Typeface.DEFAULT_BOLD
        layout.addView(tvHeader)
    }
}