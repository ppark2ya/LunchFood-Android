package com.lunchfood.ui.main.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.lunchfood.BuildConfig
import com.lunchfood.R
import com.lunchfood.data.model.AddressItem
import com.lunchfood.data.model.AddressRequest
import com.lunchfood.data.model.User
import com.lunchfood.ui.base.BaseFragment
import com.lunchfood.ui.base.BaseListener
import com.lunchfood.ui.base.GlobalApplication
import com.lunchfood.ui.main.adapter.AddressAdapter
import com.lunchfood.utils.CommonUtil
import com.lunchfood.utils.Dlog
import com.lunchfood.utils.PreferenceManager
import com.lunchfood.utils.Status
import kotlinx.android.synthetic.main.activity_address_setting.addrRecyclerView
import kotlinx.android.synthetic.main.activity_address_setting.addrSearchBtn
import kotlinx.android.synthetic.main.activity_address_setting.addressInput
import kotlinx.android.synthetic.main.activity_address_setting.gpsSearchBtn
import kotlinx.android.synthetic.main.fragment_location_setting.*
import java.lang.Exception

class LocationSettingFragment: BaseFragment() {

    private val mainViewModel by lazy { GlobalApplication.getViewModel() }
    private val adapter by lazy { AddressAdapter(arrayListOf()) }
    private val REQUEST_CODE = 1
    private var mType: String = ""
    private lateinit var mTargetItem: Any

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_location_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.run {
            view.findViewById<ImageView>(R.id.headerBackBtn).visibility = View.GONE
            setCommonHeaderText(getString(R.string.address_setting_title), view)
        }
        setupUI()
        setupEventListener()
        isLocationPermissionGranted()
    }

    private fun setupUI() {
        addrRecyclerView.layoutManager = LinearLayoutManager(mainActivity)
        addrRecyclerView.addItemDecoration(
            DividerItemDecoration(
                addrRecyclerView.context,
                (addrRecyclerView.layoutManager as LinearLayoutManager).orientation
            )
        )
        addrRecyclerView.adapter = adapter
    }

    private fun setupEventListener() {
        addrSearchBtn.setOnClickListener {
            requestGetAddressList()
        }

        // ????????? ????????? ?????? ????????????
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
            // ???????????? ??????
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if(mType != "") {
                    goNextActivity(mType, mTargetItem as AddressItem)
                }
                CommonUtil.hideKeyboard(mainActivity)
            } else {
                Toast.makeText(context, R.string.no_permission_msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        val isFirstCheck = PreferenceManager.getBoolean("isFirstPermissionCheck", true)
        if (ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // ????????? ??? ?????? ??????????????? ??? ???????????? ????????? ?????????????????? ??????
                val snackBar = Snackbar.make(locationSetting, R.string.suggest_location_permission_grant, Snackbar.LENGTH_INDEFINITE)
                snackBar.setAction(R.string.permission_granted) {
                    ActivityCompat.requestPermissions(mainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
                }
                snackBar.show()
            } else {
                if (isFirstCheck) {
                    // ?????? ???????????? ????????? ??????
                    PreferenceManager.setBoolean("isFirstPermissionCheck", false)
                    // ????????????
                    ActivityCompat.requestPermissions(mainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
                } else {
                    // ???????????? ????????? ??????????????? ?????? ???????????? ????????? ????????? ??????
                    // requestPermission??? ???????????? ?????? ???????????? ?????? ????????? ??????????????? ????????????.
                    val snackBar = Snackbar.make(locationSetting, R.string.suggest_location_permission_grant_in_setting, Snackbar.LENGTH_INDEFINITE)
                    snackBar.setAction(R.string.ok) {
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts("package", mainActivity.packageName, null)
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
        if (ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(mainActivity)
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                if(type == "KakaoMap") {
                    val intent = Intent(context, KakaoMapActivity::class.java)
                    intent.putExtra("lat", location.latitude)   // ??????(y)
                    intent.putExtra("lon", location.longitude)  // ??????(x)
                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                } else {
                    val addressItem = item as AddressItem
                    val requestBody = AddressRequest(
                        confmKey = BuildConfig.OPEN_API_LOCATION_KEY,
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
                Toast.makeText(context, R.string.no_location_msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestGetAddressList() {
        val keyword = addressInput.text.toString()
        val fieldMap = CommonUtil.convertFromDataClassToMap(AddressRequest(keyword = keyword))
        getAddressList(addressParam = fieldMap)
    }

    private fun getAddressList(addressParam: HashMap<String, Any>) {
        mainViewModel?.run {
            getAddressList(addressParam).observe(viewLifecycleOwner, {
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
        mainViewModel?.run {
            getAddressCoord(addressParam).observe(viewLifecycleOwner, {
                it.let { resource ->
                    when (resource.status) {
                        Status.PENDING -> {
                            mainActivity.loadingStart()
                        }
                        Status.SUCCESS -> {
                            mainActivity.loadingEnd()
                            resource.data?.let { res ->
                                try {
                                    val addressCommonResult = res.results.common
                                    val addressCoord = res.results.juso

                                    if (addressCommonResult.errorCode == "0") {
                                        addressCoord?.let {
                                            val coordItem = addressCoord[0]
                                            val roadAddr = addressParam["roadAddr"]
                                            this@LocationSettingFragment.updateLocation(
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
                            mainActivity.loadingEnd()
                            Dlog.e("getAddressCoord FAILURE : ${it.message}")
                        }
                    }
                }
            })
        }
    }

    private fun updateLocation(data: User) {
        mainViewModel?.run {
            updateLocation(data).observe(viewLifecycleOwner, {
                it?.let { resource ->
                    when (resource.status) {
                        Status.PENDING -> {
                            mainActivity.loadingStart()
                        }
                        Status.SUCCESS -> {
                            mainActivity.loadingEnd()
                            resource.data?.let { res ->
                                if(res.resultCode == 200) {
                                    val intent = Intent(context, BridgeActivity::class.java)
                                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                                } else {
                                    Toast.makeText(context, getString(R.string.user_update_fail_msg), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        Status.FAILURE -> {
                            mainActivity.loadingEnd()
                            Dlog.e("getAddressList FAILURE : ${it.message}")
                        }
                    }
                }
            })
        }
    }
}