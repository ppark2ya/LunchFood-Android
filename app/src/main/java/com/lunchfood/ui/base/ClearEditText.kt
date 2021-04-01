package com.lunchfood.ui.base

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.View.OnTouchListener
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


open class ClearEditText : AppCompatEditText, TextWatcher, OnTouchListener, OnFocusChangeListener, CoroutineScope {
    private lateinit var mClearDrawable: Drawable
    private var mOnFocusChangeListener: OnFocusChangeListener? = null
    private var mOnTouchListener: OnTouchListener? = null
    private var debounceMode = false
    private var _delay: Long? = null    // millisecond
    override val coroutineContext: CoroutineContext = Dispatchers.Main
    private var debounceJob: Job? = null
    private lateinit var _destinationFunction: () -> Unit

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context!!, attrs, defStyleAttr) {
        init()
    }

    override fun setOnFocusChangeListener(_onFocusChangeListener: OnFocusChangeListener) {
        this.mOnFocusChangeListener = _onFocusChangeListener
    }

    override fun setOnTouchListener(onTouchListener: OnTouchListener) {
        this.mOnTouchListener = onTouchListener
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        // res.drawable 에 있는 이미지 파일을 불러온다
        val resourceId = resources.getIdentifier("small_clear_btn", "drawable", context.packageName)
        val tempDrawable = ContextCompat.getDrawable(context, resourceId)
        mClearDrawable = DrawableCompat.wrap(tempDrawable!!)
        DrawableCompat.setTintList(mClearDrawable, hintTextColors)
        mClearDrawable.setBounds(
            0,
            0,
            mClearDrawable.intrinsicWidth,
            mClearDrawable.intrinsicHeight
        )
        setClearIconVisible(false)
        super.setOnTouchListener(this)
        super.setOnFocusChangeListener(this)
        addTextChangedListener(this)
    }

    override fun onFocusChange(view: View, hasFocus: Boolean) {
        if (hasFocus) {
            setClearIconVisible(text!!.isNotEmpty())
        } else {
            setClearIconVisible(false)
        }

        mOnFocusChangeListener?.onFocusChange(view, hasFocus)
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        val x = motionEvent.x.toInt()
        if (mClearDrawable.isVisible && x > width - paddingRight - mClearDrawable.intrinsicWidth) {
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                error = null
                text = null
            }
            return true
        }

        return if(mOnTouchListener != null) {
            mOnTouchListener!!.onTouch(view, motionEvent)
        } else {
            false
        }
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (isFocused) {
            setClearIconVisible(s.isNotEmpty())
        }
        // 참고: https://gist.github.com/faruktoptas/c45272047fae8da61acfb7b14c451793
        if(debounceMode) {
            debounceJob?.cancel()
            debounceJob = launch {
                delay(_delay!!)
                _destinationFunction()
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun afterTextChanged(s: Editable) {}

    private fun setClearIconVisible(visible: Boolean) {
        mClearDrawable.setVisible(visible, false)
        setCompoundDrawables(null, null, if (visible) mClearDrawable else null, null)
    }

    // 실시간 request 요청 시에 부하를 줄이기 위한 debounceMode
    fun setDebounceRequest(delay: Long? = 100L, destinationFunction: () -> Unit) {
        debounceMode = true
        _delay = delay
        _destinationFunction = destinationFunction
    }
}