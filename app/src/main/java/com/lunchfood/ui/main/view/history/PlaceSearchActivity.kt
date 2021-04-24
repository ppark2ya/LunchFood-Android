package com.lunchfood.ui.main.view.history

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lunchfood.R
import com.lunchfood.data.model.CommonParam
import com.lunchfood.data.model.history.PlaceInfo
import com.lunchfood.ui.base.BaseActivity
import com.lunchfood.ui.base.BaseListener
import com.lunchfood.ui.base.GlobalApplication
import com.lunchfood.ui.main.adapter.PlaceNameSearchAdapter
import com.lunchfood.utils.Dlog
import com.lunchfood.utils.Status
import kotlinx.android.synthetic.main.activity_place_search.*
import kotlinx.android.synthetic.main.header.*
import java.lang.Exception

class PlaceSearchActivity : BaseActivity(TransitionMode.HORIZON) {

    private val adapter by lazy { PlaceNameSearchAdapter(arrayListOf()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_search)
        setupUI()
        setupEventListener()
    }

    private fun setupUI() {
        setCommonHeaderText(getString(R.string.placeName_search_title))
        placeNameRecyclerView.layoutManager = LinearLayoutManager(this)
        placeNameRecyclerView.addItemDecoration(
            DividerItemDecoration(
                placeNameRecyclerView.context,
                (placeNameRecyclerView.layoutManager as LinearLayoutManager).orientation
            )
        )
        placeNameRecyclerView.adapter = adapter
    }

    private fun setupEventListener() {
        headerBackBtn.setOnClickListener {
            onBackPressed()
        }

        ivPlaceNameSearchBtn.setOnClickListener {
            requestGetPlaceAuto()
        }

        etPlaceNameInput.setDebounceRequest {
            requestGetPlaceAuto()
        }

        adapter.setOnItemClickListener(object: BaseListener {
            override fun<T> onItemClickListener(v: View, item: T, pos: Int) {
                val placeInfo = item as PlaceInfo
                val intent = Intent();
                intent.putExtra("placeInfo", placeInfo);
                setResult(RESULT_OK, intent);
                finish();
            }
        })
    }

    private fun requestGetPlaceAuto() {
        val keyword = etPlaceNameInput.text.toString()
        getPlaceAuto(CommonParam(q = keyword))
    }

    private fun getPlaceAuto(q: CommonParam) {
        GlobalApplication.getViewModel()!!.getPlaceAuto(q).observe(this, {
            it?.let { resource ->
                when (resource.status) {
                    Status.PENDING -> {
                        placeNameRecyclerView.visibility = View.GONE
                    }
                    Status.SUCCESS -> {
                        placeNameRecyclerView.visibility = View.VISIBLE
                        resource.data?.let { res ->
                            try {
                                if (res.resultCode == 200) {
                                    retrieveList(res.data!!)
                                } else {
                                    placeNameRecyclerView.visibility = View.GONE
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    Status.FAILURE -> {
                        placeNameRecyclerView.visibility = View.GONE
                        Dlog.e("getPlaceAuto FAILURE : ${it.message}")
                    }
                }
            }
        })
    }

    private fun retrieveList(placeInfoList: List<PlaceInfo>) {
        adapter.apply {
            addPlaceInfo(placeInfoList)
            notifyDataSetChanged()
        }
    }
}