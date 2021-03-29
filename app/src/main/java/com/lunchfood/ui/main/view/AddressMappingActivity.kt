package com.lunchfood.ui.main.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
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

class AddressMappingActivity : BaseActivity(TransitionMode.HORIZON) {

    private lateinit var adapter: AddressAdapter
    private val REQUEST_CODE = 1
    private lateinit var layout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_setting)
        GlobalApplication.setCurrentActivity(this@AddressMappingActivity)
        setupUI()
        setupEventListener()
    }

    private fun setupUI() {
        layout = addressSetting
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
                CommonUtil.hideKeyboard(this@AddressMappingActivity)
            }
        }

        adapter.setOnItemClickListener(object: BaseListener {
            override fun<T> onItemClickListener(v: View, item: T, pos: Int) {
                val addressItem = item as AddressItem
                Toast.makeText(applicationContext, addressItem.jibunAddr, Toast.LENGTH_LONG).show()
                addressInput.setText(addressItem.jibunAddr)
            }
        })

        gpsSearchBtn.setOnClickListener {
            isLocationPermissionGranted()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_CODE) {
            // 위치정보 승인
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isLocationPermissionGranted()
            } else {
                Toast.makeText(applicationContext, R.string.no_permission_msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 참고: https://github.com/manorgass/tistory/blob/master/android/PermissionTest/app/src/main/java/com/tistory/manorgass/permissiontest/MainActivity.kt
    private fun isLocationPermissionGranted() {
        val preference = getPreferences(Context.MODE_PRIVATE)
        val isFirstCheck = preference.getBoolean("isFirstPermissionCheck", true)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // 거부만 한 경우 사용자에게 왜 필요한지 이유를 설명해주는게 좋다
                val snackBar = Snackbar.make(layout, R.string.suggest_permissison_grant, Snackbar.LENGTH_INDEFINITE)
                snackBar.setAction("권한승인") {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
                }
                snackBar.show()
            } else {
                if (isFirstCheck) {
                    // 처음 물었는지 여부를 저장
                    preference.edit().putBoolean("isFirstPermissionCheck", false).apply()
                    // 권한요청
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
                } else {
                    // 사용자가 권한을 거부하면서 다시 묻지않음 옵션을 선택한 경우
                    // requestPermission을 요청해도 창이 나타나지 않기 때문에 설정창으로 이동한다.
                    val snackBar = Snackbar.make(layout, R.string.suggest_permissison_grant_in_setting, Snackbar.LENGTH_INDEFINITE)
                    snackBar.setAction("확인") {
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }
                    snackBar.show()
                }
            }
        } else {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val intent = Intent(this, KakaoMapActivity::class.java)
                    intent.putExtra("lat", location.latitude)   // 위도
                    intent.putExtra("lon", location.longitude)  // 경도
                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                } else {
                    Toast.makeText(applicationContext, R.string.no_location_msg, Toast.LENGTH_SHORT).show()
                }
            }
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
            // 주소 검색 component 위 쪽까지 이동
            var scrollHeight = scrollviewR.top - inputWrapper.top
            scrollview.smoothScrollTo(0, scrollviewR.top - (scrollHeight + 10))
        }
    }
}