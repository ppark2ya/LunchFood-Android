package com.lunchfood.ui.main.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.lunchfood.R
import com.lunchfood.data.model.filter.FilterCommonRequest
import com.lunchfood.data.model.history.HistoryParam
import com.lunchfood.data.model.history.HistoryResponse
import com.lunchfood.ui.base.BaseFragment
import com.lunchfood.ui.base.GlobalApplication
import com.lunchfood.utils.Dlog
import com.lunchfood.utils.Status
import kotlinx.android.synthetic.main.fragment_mymenu.*

class MyMenuFragment: BaseFragment(), AdapterView.OnItemSelectedListener, View.OnClickListener {

    private val mainViewModel by lazy { GlobalApplication.getViewModel() }
    private var mUserRadius = 500
    private var mDuplicatedInterval = 7

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

        // resources.getStringArray(R.array.user_radius)
        val userRadiusAdapter = ArrayAdapter.createFromResource(mainActivity, R.array.user_radius, R.layout.spinner_item)
        userRadiusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spUserRadius.adapter = userRadiusAdapter
        spUserRadius.setSelection(4)
        spUserRadius.onItemSelectedListener = this

        val duplicatedMealAdapter = ArrayAdapter.createFromResource(mainActivity, R.array.duplicated_interval, R.layout.spinner_item)
        duplicatedMealAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spDuplicatedInterval.adapter = duplicatedMealAdapter
        spDuplicatedInterval.setSelection(6)
        spDuplicatedInterval.onItemSelectedListener = this

        tvMyPlaceList.setOnClickListener(this)
        cvUserRadiusBtn.setOnClickListener(this)
        cvLimitRecommendBtn.setOnClickListener(this)
        cvDuplicatedMealBtn.setOnClickListener(this)
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

    private fun updateRadius(data: FilterCommonRequest) {
        mainViewModel?.run {
            updateRadius(data).observe(viewLifecycleOwner, {
                it.let { resource ->
                    when(resource.status) {
                        Status.PENDING -> {
                            mainActivity.loadingStart()
                        }
                        Status.SUCCESS -> {
                            mainActivity.loadingEnd()
                            resource.data?.let { res ->
                                if(res.resultCode == 200) {

                                }
                            }
                        }
                        Status.FAILURE -> {
                            mainActivity.loadingEnd()
                            Dlog.e("updateRadius FAILURE : ${it.message}")
                        }
                    }
                }
            })
        }
    }

    private fun updateDate(data: FilterCommonRequest) {
        mainViewModel?.run {
            updateDate(data).observe(viewLifecycleOwner, {
                it.let { resource ->
                    when(resource.status) {
                        Status.PENDING -> {
                            mainActivity.loadingStart()
                        }
                        Status.SUCCESS -> {
                            mainActivity.loadingEnd()
                            resource.data?.let { res ->
                                if(res.resultCode == 200) {

                                }
                            }
                        }
                        Status.FAILURE -> {
                            mainActivity.loadingEnd()
                            Dlog.e("updateDate FAILURE : ${it.message}")
                        }
                    }
                }
            })
        }
    }

    private fun updatePlace(data: FilterCommonRequest) {
        mainViewModel?.run {
            updatePlace(data).observe(viewLifecycleOwner, {
                it.let { resource ->
                    when(resource.status) {
                        Status.PENDING -> {
                            mainActivity.loadingStart()
                        }
                        Status.SUCCESS -> {
                            mainActivity.loadingEnd()
                            resource.data?.let { res ->
                                if(res.resultCode == 200) {

                                }
                            }
                        }
                        Status.FAILURE -> {
                            mainActivity.loadingEnd()
                            Dlog.e("updatePlace FAILURE : ${it.message}")
                        }
                    }
                }
            })
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.tvMyPlaceList -> {
                val intent = Intent(mainActivity, MyPlaceActivity::class.java)
                startActivityForResult(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP), 0)
            }
            R.id.cvUserRadiusBtn -> {}
            R.id.cvLimitRecommendBtn -> {}
            R.id.cvDuplicatedMealBtn -> {}
        }
    }
}