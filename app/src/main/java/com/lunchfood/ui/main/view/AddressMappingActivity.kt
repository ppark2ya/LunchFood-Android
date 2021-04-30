package com.lunchfood.ui.main.view

import android.Manifest
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
import com.lunchfood.BuildConfig.OPEN_API_LOCATION_KEY
import com.lunchfood.R
import com.lunchfood.data.model.AddressItem
import com.lunchfood.data.model.AddressRequest
import com.lunchfood.data.model.User
import com.lunchfood.ui.base.BaseActivity
import com.lunchfood.ui.base.BaseListener
import com.lunchfood.ui.base.GlobalApplication
import com.lunchfood.ui.main.adapter.AddressAdapter
import com.lunchfood.utils.CommonUtil
import com.lunchfood.utils.Dlog
import com.lunchfood.utils.PreferenceManager
import com.lunchfood.utils.Status
import kotlinx.android.synthetic.main.activity_address_setting.*
import kotlinx.android.synthetic.main.header.*
import java.lang.Exception

class AddressMappingActivity : BaseActivity(TransitionMode.HORIZON) {

    private val mainViewModel by lazy { GlobalApplication.getViewModel() }
    private lateinit var adapter: AddressAdapter
    private val REQUEST_CODE = 1
    private lateinit var layout: View
    private var mType: String = ""
    private lateinit var mTargetItem: Any

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_setting)
        setupUI()
        setupEventListener()
        isLocationPermissionGranted()
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
            requestGetAddressList()
        }

        // 입력할 때마다 주소 보이게끔
        addressInput.setDebounceRequest {
            requestGetAddressList()
        }

        adapter.setOnItemClickListener(object: BaseListener {
            override fun<T> onItemClickListener(v: View, item: T, pos: Int) {
                mType = "Bridge"
                mTargetItem = item as AddressItem
                if(isLocationPermissionGranted()) {
                    goNextActivity(mType, item as AddressItem)
                }
            }
        })

        gpsSearchBtn.setOnClickListener {
            mType = "KakaoMap"
            if(isLocationPermissionGranted()) {
                goNextActivity(mType, null)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_CODE) {
            // 위치정보 승인
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if(mType != "") {
                    goNextActivity(mType, mTargetItem as AddressItem)
                }
                CommonUtil.hideKeyboard(this@AddressMappingActivity)
            } else {
                Toast.makeText(applicationContext, R.string.no_permission_msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 참고: https://github.com/manorgass/tistory/blob/master/android/PermissionTest/app/src/main/java/com/tistory/manorgass/permissiontest/MainActivity.kt
    private fun isLocationPermissionGranted(): Boolean {
        val isFirstCheck = PreferenceManager.getBoolean("isFirstPermissionCheck", true)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // 거부만 한 경우 사용자에게 왜 필요한지 이유를 설명해주는게 좋다
                val snackBar = Snackbar.make(layout, R.string.suggest_location_permission_grant, Snackbar.LENGTH_INDEFINITE)
                snackBar.setAction(R.string.permission_granted) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
                }
                snackBar.show()
            } else {
                if (isFirstCheck) {
                    // 처음 물었는지 여부를 저장
                    PreferenceManager.setBoolean("isFirstPermissionCheck", false)
                    // 권한요청
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
                } else {
                    // 사용자가 권한을 거부하면서 다시 묻지않음 옵션을 선택한 경우
                    // requestPermission을 요청해도 창이 나타나지 않기 때문에 설정창으로 이동한다.
                    val snackBar = Snackbar.make(layout, R.string.suggest_location_permission_grant_in_setting, Snackbar.LENGTH_INDEFINITE)
                    snackBar.setAction(R.string.ok) {
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }
                    snackBar.show()
                }
            }
            return false
        } else {
            return true
        }
    }

    private fun goNextActivity(type: String, item: AddressItem?) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                if(type == "KakaoMap") {
                    val intent = Intent(this, KakaoMapActivity::class.java)
                    intent.putExtra("lat", location.latitude)   // 위도(y)
                    intent.putExtra("lon", location.longitude)  // 경도(x)
                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                } else {
                    val addressItem = item as AddressItem
                    val requestBody = AddressRequest(
                        confmKey = OPEN_API_LOCATION_KEY,
                        admCd = addressItem.admCd,
                        rnMgtSn = addressItem.rnMgtSn,
                        udrtYn = addressItem.udrtYn,
                        buldMnnm = addressItem.buldMnnm,
                        buldSlno = addressItem.buldSlno,
                        roadAddr = addressItem.roadAddr
                    )
                    val fieldMap = CommonUtil.convertFromDataClassToMap(requestBody)
                    getAddressCoord(addressParam = fieldMap)
                }
            } else {
                Toast.makeText(applicationContext, R.string.no_location_msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestGetAddressList() {
        val keyword = addressInput.text.toString()
        val fieldMap = CommonUtil.convertFromDataClassToMap(AddressRequest(keyword = keyword))
        getAddressList(addressParam = fieldMap)
    }

    private fun getAddressList(addressParam: HashMap<String, Any>) {
        mainViewModel?.let { model ->
            model.getAddressList(addressParam).observe(this@AddressMappingActivity, {
                it.let { resource ->
                    when (resource.status) {
                        Status.PENDING -> {
                            addrRecyclerView.visibility = View.GONE
                        }
                        Status.SUCCESS -> {
                            addrRecyclerView.visibility = View.VISIBLE
                            resource.data?.let { res ->
                                try {
                                    val addressCommonResult = res.results.common
                                    val addressJusoList = res.results.juso

                                    if (addressCommonResult.errorCode == "0") {
                                        if (addressJusoList != null) {
                                            retrieveList(addressJusoList)
//                                        scrollToBottom()
                                        }
                                    } else {
                                        addrRecyclerView.visibility = View.GONE
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                        Status.FAILURE -> {
                            addrRecyclerView.visibility = View.GONE
                            Dlog.e("getAddressList FAILURE : ${it.message}")
                        }
                    }
                }
            })
        }
    }

    private fun retrieveList(addressList: List<AddressItem>) {
        adapter.apply {
            addAddress(addressList)
            notifyDataSetChanged()
        }
    }

    private fun getAddressCoord(addressParam: HashMap<String, Any>) {
        mainViewModel?.let { model ->
            model.getAddressCoord(addressParam).observe(this@AddressMappingActivity, {
                it.let { resource ->
                    when (resource.status) {
                        Status.PENDING -> {
                            loadingStart()
                        }
                        Status.SUCCESS -> {
                            loadingEnd()
                            resource.data?.let { res ->
                                try {
                                    val addressCommonResult = res.results.common
                                    val addressCoord = res.results.juso

                                    if (addressCommonResult.errorCode == "0") {
                                        addressCoord?.let {
                                            val coordItem = addressCoord[0]
                                            val roadAddr = addressParam["roadAddr"]
                                            updateLocation(
                                                User(id = userId, lat = coordItem.entY, lon = coordItem.entX, address = roadAddr.toString(), type = "UTMK")
                                            )
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                        Status.FAILURE -> {
                            loadingEnd()
                            Dlog.e("getAddressCoord FAILURE : ${it.message}")
                        }
                    }
                }
            })
        }
    }

    private fun updateLocation(data: User) {
        mainViewModel?.let { model ->
            model.updateLocation(data).observe(this@AddressMappingActivity, {
                it?.let { resource ->
                    when (resource.status) {
                        Status.PENDING -> {
                            loadingStart()
                        }
                        Status.SUCCESS -> {
                            loadingEnd()
                            resource.data?.let { res ->
                                if(res.resultCode == 200) {
                                    val intent = Intent(this, BridgeActivity::class.java)
                                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                                } else {
                                    Toast.makeText(this@AddressMappingActivity, getString(R.string.user_update_fail_msg), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        Status.FAILURE -> {
                            loadingEnd()
                            Dlog.e("getAddressList FAILURE : ${it.message}")
                        }
                    }
                }
            })
        }
    }

    private fun scrollToBottom() {
        scrollview.post {
            // 주소 검색 component 위 쪽까지 이동
            val scrollHeight = scrollviewR.top - inputWrapper.top
            scrollview.smoothScrollTo(0, scrollviewR.top - (scrollHeight + 10))
        }
    }
}