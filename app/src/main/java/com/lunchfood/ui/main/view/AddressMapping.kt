package com.lunchfood.ui.main.view

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lunchfood.R
import com.lunchfood.data.model.AddressCommonResult
import com.lunchfood.data.model.AddressItem
import com.lunchfood.data.model.AddressRequest
import com.lunchfood.ui.base.BaseActivity
import com.lunchfood.ui.base.BaseListener
import com.lunchfood.ui.base.GlobalApplication
import com.lunchfood.ui.main.adapter.AddressAdapter
import com.lunchfood.utils.CommonUtil
import com.lunchfood.utils.Status
import kotlinx.android.synthetic.main.activity_address_setting.*
import kotlinx.android.synthetic.main.header.*
import java.lang.Exception

class AddressMapping : BaseActivity(TransitionMode.HORIZON) {

    private lateinit var adapter: AddressAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_setting)
        GlobalApplication.setCurrentActivity(this@AddressMapping)
        setupUI()
        setupEventListener()
    }

    private fun setupUI() {
        addrRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AddressAdapter(arrayListOf())
        addrRecyclerView.addItemDecoration(
            DividerItemDecoration(
                addrRecyclerView.context,
                (addrRecyclerView.layoutManager as LinearLayoutManager).orientation
            )
        )
        addrRecyclerView.adapter = adapter
    }

    private fun setupEventListener() {
        headerBackBtn.setOnClickListener {
            onBackPressed()
        }

        addrSearchBtn.setOnClickListener {
            var keyword = addressInput.text.toString()
            val fieldMap = CommonUtil.convertFromDataClassToMap(AddressRequest(keyword = keyword))
            if (fieldMap != null) {
                getAddressList(addressParam = fieldMap)
                CommonUtil.hideKeyboard(this@AddressMapping)
            }
        }

        adapter.setOnItemClickListener(object: BaseListener {
            override fun<T> onItemClickListener(v: View, item: T, pos: Int) {
                val addressItem = item as AddressItem
                Toast.makeText(GlobalApplication.getCurrentActivity(), addressItem.jibunAddr, Toast.LENGTH_LONG).show()
                addressInput.setText(addressItem.jibunAddr)
            }
        })

        scrollviewR.setOnTouchListener { v, event ->
            scrollview.requestDisallowInterceptTouchEvent(true)
            false
        }
    }

    private fun getAddressList(addressParam: HashMap<String, Object>) {
        GlobalApplication.getViewModel()!!.getAddressList(addressParam).observe(this, {
            it?.let { resource ->
                when (resource.status) {
                    // 로딩
                    Status.PENDING -> {
//                        progressBar.visibility = View.VISIBLE
                        addrRecyclerView.visibility = View.GONE
                    }
                    Status.SUCCESS -> {
                        addrRecyclerView.visibility = View.VISIBLE
//                        progressBar.visibility = View.GONE
                        resource.data?.let { res ->
                            try {
                                val results = res["results"] as Map<String, Object>
                                val cmm = results["common"] as Map<String, String>
                                val addressCommonResult = AddressCommonResult.from(cmm)
                                if(addressCommonResult.errorCode == "0") {
                                    val tempList = results["juso"] as List<Map<String, String>>
                                    val addressList = mutableListOf<AddressItem>()
                                    for(addr in tempList) {
                                        val addressItem = AddressItem.from(addr)
                                        addressList.add(addressItem)
                                    }
                                    retrieveList(addressList)
                                    scrollToBottom()
                                }
                            } catch(e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    Status.FAILURE -> {
                        addrRecyclerView.visibility = View.GONE
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    private fun retrieveList(addressList: List<AddressItem>) {
        adapter.apply {
            addAddress(addressList)
            notifyDataSetChanged()
        }
    }

    private fun scrollToBottom() {
        scrollview.post {
            scrollview.smoothScrollTo(0, scrollviewR.top)
        }
    }
}