package com.lunchfood.ui.main.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import com.lunchfood.R
import com.lunchfood.data.model.filter.FilterCommonRequest
import com.lunchfood.ui.base.BaseFragment
import com.lunchfood.ui.base.GlobalApplication
import com.lunchfood.utils.Dlog
import com.lunchfood.utils.Status
import kotlinx.android.synthetic.main.fragment_mymenu.*

class MyMenuFragment: BaseFragment(), AdapterView.OnItemSelectedListener, View.OnClickListener {

    private val mainViewModel by lazy { GlobalApplication.getViewModel() }
    private var mUserRadius = GlobalApplication.mUserData.radius
    private var mDuplicatedInterval = GlobalApplication.mUserData.setDate
    private var mRadiusOn = GlobalApplication.mUserData.radiusOn
    private var mPlaceOn = GlobalApplication.mUserData.placeOn
    private var mDateOn = GlobalApplication.mUserData.dateOn

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mymenu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.run { setCommonHeaderText(getString(R.string.my_menu), view) }

        val userRadiusAdapter = ArrayAdapter.createFromResource(mainActivity, R.array.user_radius, R.layout.spinner_item)
        userRadiusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spUserRadius.adapter = userRadiusAdapter
        spUserRadius.setSelection(getRadiusSpinnerIndex(mUserRadius))
        spUserRadius.onItemSelectedListener = this

        val duplicatedIntervalAdapter = ArrayAdapter.createFromResource(mainActivity, R.array.duplicated_interval, R.layout.spinner_item)
        duplicatedIntervalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spDuplicatedInterval.adapter = duplicatedIntervalAdapter
        spDuplicatedInterval.setSelection(mDuplicatedInterval - 1)
        spDuplicatedInterval.onItemSelectedListener = this

        setupUI()

        tvMyPlaceList.setOnClickListener(this)
        rlUserRadiusBtn.setOnClickListener(this)
        rlLimitRecommendBtn.setOnClickListener(this)
        rlDuplicatedIntervalBtn.setOnClickListener(this)
        cvFilterUpdateBtn.setOnClickListener(this)
    }

    private fun setupUI() {
        toggleSpinnerUI(spUserRadius, mRadiusOn)
        toggleSpinnerUI(spDuplicatedInterval, mDateOn)

        toggleButtonUI(rlUserRadiusBtn, tvUserRadius, mRadiusOn)
        toggleButtonUI(rlLimitRecommendBtn, tvLimitRecommend, mPlaceOn)
        toggleButtonUI(rlDuplicatedIntervalBtn, tvDuplicatedInterval, mDateOn)

        tvRadius1.setTextColor(getCommentColor(mRadiusOn))
        tvRad2.setTextColor(getCommentColor(mRadiusOn))
        tvDup1.setTextColor(getCommentColor(mDateOn))
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when(parent?.id) {
            R.id.spUserRadius -> {
                when(position) {
                    0 -> mUserRadius = 100
                    1 -> mUserRadius = 200
                    2 -> mUserRadius = 300
                    3 -> mUserRadius = 400
                    4 -> mUserRadius = 500
                    5 -> mUserRadius = 600
                    6 -> mUserRadius = 700
                    7 -> mUserRadius = 800
                    8 -> mUserRadius = 900
                    9 -> mUserRadius = 1000
                }
            }
            R.id.spDuplicatedInterval -> {
                when(position) {
                    0 -> mDuplicatedInterval = 1
                    1 -> mDuplicatedInterval = 2
                    2 -> mDuplicatedInterval = 3
                    3 -> mDuplicatedInterval = 4
                    4 -> mDuplicatedInterval = 5
                    5 -> mDuplicatedInterval = 6
                    6 -> mDuplicatedInterval = 7
                    7 -> mDuplicatedInterval = 14
                    8 -> mDuplicatedInterval = 21
                    9 -> mDuplicatedInterval = 28
                }
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    private fun updateFilter(data: FilterCommonRequest) {
        mainViewModel?.run {
            updateFilter(data).observe(viewLifecycleOwner, {
                it.let { resource ->
                    when(resource.status) {
                        Status.PENDING -> {}
                        Status.SUCCESS -> {
                            resource.data?.let { res ->
                                if(res.resultCode == 200) {
                                    Toast.makeText(mainActivity, getString(R.string.filter_update_success), Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(mainActivity, getString(R.string.filter_update_fail), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        Status.FAILURE -> {
                            Dlog.e("updateFilter FAILURE : ${it.message}")
                            Toast.makeText(mainActivity, getString(R.string.filter_update_fail), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.tvMyPlaceList -> {
                if(mPlaceOn == 1) {
                    val intent = Intent(mainActivity, MyPlaceActivity::class.java)
                    startActivityForResult(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP), 0)
                }
            }
            R.id.rlUserRadiusBtn -> {
                mRadiusOn = toggleButtonState(mRadiusOn)
                toggleSpinnerUI(spUserRadius, mRadiusOn)
                toggleButtonUI(rlUserRadiusBtn, tvUserRadius, mRadiusOn)
                tvRadius1.setTextColor(getCommentColor(mRadiusOn))
                tvRad2.setTextColor(getCommentColor(mRadiusOn))
            }
            R.id.rlLimitRecommendBtn -> {
                mPlaceOn = toggleButtonState(mPlaceOn)
                toggleButtonUI(rlLimitRecommendBtn, tvLimitRecommend, mPlaceOn)
                tvMyPlaceList.setTextColor(getCommentColor(mPlaceOn))
            }
            R.id.rlDuplicatedIntervalBtn -> {
                mDateOn = toggleButtonState(mDateOn)
                toggleSpinnerUI(spDuplicatedInterval, mDateOn)
                toggleButtonUI(rlDuplicatedIntervalBtn, tvDuplicatedInterval, mDateOn)
                tvDup1.setTextColor(getCommentColor(mDateOn))
            }
            R.id.cvFilterUpdateBtn -> {
                updateFilter(
                    FilterCommonRequest(
                        id = userId,
                        radius = mUserRadius,
                        radiusOn = mRadiusOn,
                        placeOn = mPlaceOn,
                        setDate = mDuplicatedInterval,
                        dateOn = mDateOn
                    )
                )
            }
        }
    }

    private fun getRadiusSpinnerIndex(userRadius: Int): Int {
        return when(userRadius) {
            100 -> 0
            200 -> 1
            300 -> 2
            400 -> 3
            500 -> 4
            600 -> 5
            700 -> 6
            800 -> 7
            900 -> 8
            else ->  9
        }
    }

    private fun toggleSpinnerUI(spinner: Spinner, state: Int) {
        when(state) {
            0 -> {
                spinner.isEnabled = false
                spinner.isClickable = false
                val resourceId = resources.getIdentifier("spinner_disabled_background", "drawable", requireContext().packageName)
                spinner.background = ContextCompat.getDrawable(requireContext(), resourceId)
            }
            else -> {
                spinner.isEnabled = true
                spinner.isClickable = true
                val resourceId = resources.getIdentifier("spinner_background", "drawable", requireContext().packageName)
                spinner.background = ContextCompat.getDrawable(requireContext(), resourceId)
            }
        }
    }

    private fun toggleButtonUI(relativeLayout: RelativeLayout, textView: TextView, state: Int) {
        relativeLayout.setBackgroundColor(getButtonColor(state))
        textView.setTextColor(getTextColor(state))
        textView.setCompoundDrawablesWithIntrinsicBounds(getFireIcon(state), 0, 0, 0)
    }

    private fun toggleButtonState(state: Int): Int {
        return when(state) {
            0 -> 1
            else -> 0
        }
    }

    private fun getButtonColor(state: Int): Int {
        return when(state) {
            0 -> ContextCompat.getColor(mainActivity, R.color.button_disabled)
            else -> ContextCompat.getColor(mainActivity, R.color.lunch_red)
        }
    }

    private fun getTextColor(state: Int): Int {
        return when(state) {
            0 -> ContextCompat.getColor(mainActivity, R.color.gray)
            else -> ContextCompat.getColor(mainActivity, R.color.white)
        }
    }

    private fun getCommentColor(state: Int): Int {
        return when(state) {
            0 -> ContextCompat.getColor(mainActivity, R.color.gray)
            else -> ContextCompat.getColor(mainActivity, R.color.black)
        }
    }

    private fun getFireIcon(state: Int): Int {
        return when(state) {
            0 -> R.drawable.left_gray_fire_customize
            else -> R.drawable.left_fire_customize
        }
    }
}