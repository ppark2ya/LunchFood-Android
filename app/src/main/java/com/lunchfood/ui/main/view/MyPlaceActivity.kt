package com.lunchfood.ui.main.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lunchfood.R
import com.lunchfood.data.model.filter.FilterCommonRequest
import com.lunchfood.data.model.filter.SelectedPlace
import com.lunchfood.data.model.history.PlaceInfo
import com.lunchfood.ui.base.BaseActivity
import com.lunchfood.ui.base.BaseListener
import com.lunchfood.ui.base.GlobalApplication
import com.lunchfood.ui.main.adapter.MyFavoritePlaceAdapter
import com.lunchfood.ui.main.view.history.PlaceSearchActivity
import com.lunchfood.utils.Dlog
import com.lunchfood.utils.Status
import kotlinx.android.synthetic.main.activity_myplace.*
import kotlinx.android.synthetic.main.header.*

class MyPlaceActivity : BaseActivity(TransitionMode.HORIZON) {

    private val mainViewModel by lazy { GlobalApplication.getViewModel() }
    private val adapter by lazy { MyFavoritePlaceAdapter(arrayListOf()) }
    private val REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myplace)
        setupUI()
        setupEventListener()
    }

    private fun setupUI() {
        setCommonHeaderText(getString(R.string.frequently_used_restaurants))
        favoritePlaceRecyclerView.layoutManager = LinearLayoutManager(this)
        favoritePlaceRecyclerView.addItemDecoration(
            DividerItemDecoration(
                favoritePlaceRecyclerView.context,
                (favoritePlaceRecyclerView.layoutManager as LinearLayoutManager).orientation
            )
        )
        favoritePlaceRecyclerView.adapter = adapter
        getSelectedPlace(FilterCommonRequest(id = userId))
    }

    private fun setupEventListener() {
        headerBackBtn.setOnClickListener {
            onBackPressed()
        }

        cvPlaceAddBtn.setOnClickListener {
            val intent = Intent(this@MyPlaceActivity, PlaceSearchActivity::class.java)
            startActivityForResult(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP), REQUEST_CODE)
        }

        adapter.setOnItemClickListener(object: BaseListener {
            override fun <T> onItemClickListener(v: View, item: T, pos: Int) {
                val selectedPlace = item as SelectedPlace
                deleteSelectedPlace(FilterCommonRequest(id = userId, placeId = selectedPlace.placeId))
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                val placeInfo = data?.getSerializableExtra("placeInfo") as PlaceInfo?
                placeInfo?.let {
                    insertSelectedPlace(FilterCommonRequest(id = userId, placeId = placeInfo.id.toInt(), placeName = placeInfo.placeName))
                }
            }
        }
    }

    private fun getSelectedPlace(data: FilterCommonRequest) {
        mainViewModel?.let { model ->
            model.getSelectedPlace(data).observe(this@MyPlaceActivity, {
                it.let { resource ->
                    when (resource.status) {
                        Status.PENDING -> {
                            favoritePlaceRecyclerView.visibility = View.GONE
                        }
                        Status.SUCCESS -> {
                            favoritePlaceRecyclerView.visibility = View.VISIBLE
                            resource.data?.let { res ->
                                if(res.resultCode == 200) {
                                    retrieveList(res.data!!)
                                } else {
                                    favoritePlaceRecyclerView.visibility = View.GONE
                                }

                            }
                        }
                        Status.FAILURE -> {
                            favoritePlaceRecyclerView.visibility = View.GONE
                            Dlog.e("getSelectedPlace FAILURE : ${it.message}")
                        }
                    }
                }
            })
        }
    }

    private fun retrieveList(myFavoritePlaceList: List<SelectedPlace>) {
        adapter.apply {
            addPlaceInfo(myFavoritePlaceList)
            notifyDataSetChanged()
        }
    }

    private fun insertSelectedPlace(data: FilterCommonRequest) {
        mainViewModel?.let { model ->
            model.insertSelectedPlace(data).observe(this@MyPlaceActivity, {
                it.let { resource ->
                    when (resource.status) {
                        Status.PENDING -> {
                            loadingStart()
                        }
                        Status.SUCCESS -> {
                            loadingEnd()
                            resource.data?.let { res ->
                                if(res.resultCode == 200) {
                                    getSelectedPlace(FilterCommonRequest(id = userId))
                                } else {
                                    Toast.makeText(this@MyPlaceActivity, getString(R.string.user_update_fail_msg), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        Status.FAILURE -> {
                            loadingEnd()
                            Dlog.e("insertSelectedPlace FAILURE : ${it.message}")
                        }
                    }
                }
            })
        }
    }

    private fun deleteSelectedPlace(data: FilterCommonRequest) {
        mainViewModel?.let { model ->
            model.deleteSelectedPlace(data).observe(this@MyPlaceActivity, {
                it.let { resource ->
                    when (resource.status) {
                        Status.PENDING -> {
                            loadingStart()
                        }
                        Status.SUCCESS -> {
                            loadingEnd()
                            resource.data?.let { res ->
                                if(res.resultCode == 200) {
                                    getSelectedPlace(FilterCommonRequest(id = userId))
                                } else {
                                    Toast.makeText(this@MyPlaceActivity, getString(R.string.user_update_fail_msg), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        Status.FAILURE -> {
                            loadingEnd()
                            Dlog.e("getSelectedPlace FAILURE : ${it.message}")
                        }
                    }
                }
            })
        }
    }
}