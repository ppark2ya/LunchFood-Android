package com.lunchfood.ui.main.view

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.lunchfood.R
import com.lunchfood.data.api.ApiHelper
import com.lunchfood.data.api.RetrofitBuilder
import com.lunchfood.data.model.AddressRequest
import com.lunchfood.data.model.User
import com.lunchfood.ui.base.BaseActivity
import com.lunchfood.ui.base.GlobalApplication
import com.lunchfood.ui.base.ViewModelFactory
import com.lunchfood.ui.main.viewmodel.MainViewModel
import com.lunchfood.utils.CommonUtil
import com.lunchfood.utils.Constants.Companion.LATITUDE_DEFAULT
import com.lunchfood.utils.Constants.Companion.LONGITUDE_DEFAULT
import com.lunchfood.utils.Dlog
import com.lunchfood.utils.PreferenceManager
import com.lunchfood.utils.Status
import kotlinx.android.synthetic.main.activity_address_setting.*
import kotlinx.android.synthetic.main.activity_kakao_map_custom.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapReverseGeoCoder
import net.daum.mf.map.api.MapView
import java.lang.Exception

class KakaoMapActivity : BaseActivity(), MapView.CurrentLocationEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener {

    private lateinit var mMapView: MapView
    private lateinit var mapViewContainer: RelativeLayout
    private lateinit var mReverseGeoCoder: MapReverseGeoCoder
    private lateinit var viewModel: MainViewModel
    private var mLat: Double = LATITUDE_DEFAULT
    private var mLon: Double = LONGITUDE_DEFAULT
    private var mAddress: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kakao_map_custom)
        setupMapView()
        setupViewModel()
        GlobalScope.launch {
            setupAddress()
        }

        setUserAddrBtn.setOnClickListener {
            updateLocation(
                User(id = userId, lat = mLat.toString(), lon = mLon.toString(), address = mAddress, type = "WGS84")
            )
        }
    }

    private fun setupMapView() {
        mMapView = MapView(this)
        mMapView.setCurrentLocationEventListener(this)
        // mMapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading

        mapViewContainer = rlMapView
        mapViewContainer.addView(mMapView)

        val intent = intent
        mLat = intent.getDoubleExtra("lat", LATITUDE_DEFAULT)
        mLon = intent.getDoubleExtra("lon", LONGITUDE_DEFAULT)
        val userNowLocation = MapPoint.mapPointWithGeoCoord(mLat, mLon)
        mMapView.setMapCenterPoint(userNowLocation, true)

        val marker = MapPOIItem()
        marker.itemName = "나의 위치"
        marker.tag = 0
        marker.mapPoint = userNowLocation
        marker.markerType = MapPOIItem.MarkerType.CustomImage
        marker.customImageBitmap = BitmapFactory.decodeResource(resources, R.drawable.gps_img).let {
            Bitmap.createScaledBitmap(
                it, 80, 90, false
            )
        }
        marker.isCustomImageAutoscale = false
        marker.setCustomImageAnchor(0.5f, 1.0f)
        mMapView.addPOIItem(marker)
    }

    private suspend fun setupAddress() {
        delay(1000)
        mReverseGeoCoder = MapReverseGeoCoder(getString(R.string.kakao_app_key), mMapView.mapCenterPoint, this@KakaoMapActivity, this@KakaoMapActivity)
        mReverseGeoCoder.startFindingAddress()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
                this,
                ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(MainViewModel::class.java)
    }

    private fun getAddressList(addressParam: HashMap<String, Any>) {
        viewModel.getAddressList(addressParam).observe(this, {
            it?.let { resource ->
                when (resource.status) {
                    Status.PENDING -> {
                        Dlog.i("getAddressList PENDING")
                    }
                    Status.SUCCESS -> {
                        resource.data?.let { res ->
                            try {
                                val addressCommonResult = res.results.common
                                val addressJusoList = res.results.juso

                                if (addressCommonResult.errorCode == "0") {
                                    if (addressJusoList != null) {
                                        tvRoadAddr.text = addressJusoList[0].roadAddr
                                        tvJibunAddr.text = addressJusoList[0].jibunAddr
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    Status.FAILURE -> {
                        Dlog.e("getAddressList FAILURE : ${it.message}")
                    }
                }
            }
        })
    }

    private fun updateLocation(data: User) {
        GlobalApplication.getViewModel()!!.updateLocation(data).observe(this, {
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
                                Toast.makeText(this@KakaoMapActivity, "사용자 정보 업데이트에 실패했습니다.", Toast.LENGTH_SHORT)
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

    override fun onCurrentLocationUpdate(mapView: MapView?, currentLocation: MapPoint?, accuracyInMeters: Float) {
        val mapPointGeo: MapPoint.GeoCoordinate? = currentLocation?.mapPointGeoCoord

        if(mapPointGeo != null) {
            val userNowLocation = MapPoint.mapPointWithGeoCoord(mapPointGeo.latitude, mapPointGeo.longitude)
            mMapView.setMapCenterPoint(userNowLocation, true)
            launch {
                setupAddress()
            }
        }
    }

    override fun onCurrentLocationDeviceHeadingUpdate(p0: MapView?, p1: Float) {}

    override fun onCurrentLocationUpdateFailed(p0: MapView?) {}

    override fun onCurrentLocationUpdateCancelled(p0: MapView?) {}

    override fun onReverseGeoCoderFoundAddress(mapReverseGeoCoder: MapReverseGeoCoder?, s: String?) {
        mapReverseGeoCoder.toString()
        mAddress = s!!
        val fieldMap = CommonUtil.convertFromDataClassToMap(AddressRequest(keyword = mAddress))
        getAddressList(addressParam = fieldMap)
    }

    override fun onReverseGeoCoderFailedToFindAddress(p0: MapReverseGeoCoder?) {}

    override fun onStop() {
        super.onStop()
        mapViewContainer.removeView(rlMapView)
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
        mMapView.setShowCurrentLocationMarker(false)
    }
}