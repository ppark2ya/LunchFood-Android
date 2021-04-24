package com.lunchfood.ui.main.view.history

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lunchfood.R
import com.lunchfood.data.model.CommonParam
import com.lunchfood.ui.base.BaseActivity
import com.lunchfood.ui.base.BaseListener
import com.lunchfood.ui.base.GlobalApplication
import com.lunchfood.ui.main.adapter.FoodNameSearchAdapter
import com.lunchfood.ui.main.adapter.PlaceNameSearchAdapter
import com.lunchfood.utils.Dlog
import com.lunchfood.utils.Status
import kotlinx.android.synthetic.main.activity_menu_search.*
import kotlinx.android.synthetic.main.activity_place_search.*
import kotlinx.android.synthetic.main.header.*
import java.lang.Exception

class MenuSearchActivity : BaseActivity(TransitionMode.HORIZON) {

    private val adapter by lazy { FoodNameSearchAdapter(arrayListOf()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_search)
        setupUI()
        setupEventListener()
    }

    private fun setupUI() {
        setCommonHeaderText(getString(R.string.food_name_search_title))
        foodNameRecyclerView.layoutManager = LinearLayoutManager(this)
        foodNameRecyclerView.addItemDecoration(
            DividerItemDecoration(
                foodNameRecyclerView.context,
                (foodNameRecyclerView.layoutManager as LinearLayoutManager).orientation
            )
        )
        foodNameRecyclerView.adapter = adapter
    }

    private fun setupEventListener() {
        headerBackBtn.setOnClickListener {
            onBackPressed()
        }

        ivFoodNameSearchBtn.setOnClickListener {
            requestGetFoodAuto()
        }

        etFoodNameInput.setDebounceRequest {
            requestGetFoodAuto()
        }

        adapter.setOnItemClickListener(object: BaseListener {
            override fun<T> onItemClickListener(v: View, item: T, pos: Int) {
                val foodName = item as String
                val intent = Intent();
                intent.putExtra("foodName", foodName);
                setResult(RESULT_OK, intent);
                finish();
            }
        })
    }

    private fun requestGetFoodAuto() {
        val keyword = etFoodNameInput.text.toString()
        getFoodAuto(CommonParam(q = keyword))
    }

    private fun getFoodAuto(q: CommonParam) {
        GlobalApplication.getViewModel()!!.getFoodAuto(q).observe(this, {
            it?.let { resource ->
                when (resource.status) {
                    Status.PENDING -> {
                        foodNameRecyclerView.visibility = View.GONE
                    }
                    Status.SUCCESS -> {
                        foodNameRecyclerView.visibility = View.VISIBLE
                        resource.data?.let { res ->
                            try {
                                if (res.resultCode == 200) {
                                    retrieveList(res.data!!)
                                } else {
                                    foodNameRecyclerView.visibility = View.GONE
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    Status.FAILURE -> {
                        foodNameRecyclerView.visibility = View.GONE
                        Dlog.e("getFoodAuto FAILURE : ${it.message}")
                    }
                }
            }
        })
    }

    private fun retrieveList(foodList: List<String>) {
        adapter.apply {
            addPlaceInfo(foodList)
            notifyDataSetChanged()
        }
    }
}