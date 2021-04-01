package com.lunchfood.ui.main.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.lunchfood.R
import com.lunchfood.data.api.ApiHelper
import com.lunchfood.data.api.RetrofitBuilder
import com.lunchfood.data.model.AddressRequest
import com.lunchfood.ui.base.GlobalApplication
import com.lunchfood.ui.base.ViewModelFactory
import com.lunchfood.ui.main.viewmodel.MainViewModel
import com.lunchfood.utils.CommonUtil
import com.lunchfood.utils.Constants.Companion.LATITUDE_DEFAULT
import com.lunchfood.utils.Constants.Companion.LONGITUDE_DEFAULT
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

class KakaoMapActivity : AppCompatActivity(), MapView.CurrentLocationEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener {

    private lateinit var mMapView: MapView
    private lateinit var mReverseGeoCoder: MapReverseGeoCoder
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kakao_map_custom)
        GlobalApplication.setCurrentActivity(this@KakaoMapActivity)
        setupMapView()
        setupViewModel()
        GlobalScope.launch {
            setupAddress()
        }

    }

    private fun setupMapView() {
        mMapView = MapView(this)
        mMapView.setCurrentLocationEventListener(this)
        // mMapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading

        val mapViewContainer = rlMapView
        mapViewContainer.addView(mMapView)

        val intent = intent
        val lat = intent.getDoubleExtra("lat", LATITUDE_DEFAULT)
        val lon = intent.getDoubleExtra("lon", LONGITUDE_DEFAULT)
        val userNowLocation = MapPoint.mapPointWithGeoCoord(lat, lon)
        mMapView.setMapCenterPoint(userNowLocation, true)

        val marker = MapPOIItem()
        marker.itemName = "나의 위치"
        marker.tag = 0
        marker.mapPoint = userNowLocation
        marker.markerType = MapPOIItem.MarkerType.RedPin
        marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin
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
                    // 로딩
                    Status.PENDING -> {
//                        progressBar.visibility = View.VISIBLE
                    }
                    Status.SUCCESS -> {
//                        progressBar.visibility = View.GONE
                        resource.data?.let { res ->
                            try {
                                val addressCommonResult = res.results.common
                                val addressJusoList = res.results.juso

                                if(addressCommonResult.errorCode == "0") {
                                    if (addressJusoList != null) {
                                        tvRoadAddr.text = addressJusoList[0].roadAddr
                                        tvJibunAddr.text = addressJusoList[0].jibunAddr
                                    }
                                }
                            } catch(e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    Status.FAILURE -> {
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
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
            GlobalScope.launch {
                setupAddress()
            }
        }
    }

    override fun onCurrentLocationDeviceHeadingUpdate(p0: MapView?, p1: Float) {}

    override fun onCurrentLocationUpdateFailed(p0: MapView?) {}

    override fun onCurrentLocationUpdateCancelled(p0: MapView?) {}

    override fun onReverseGeoCoderFoundAddress(mapReverseGeoCoder: MapReverseGeoCoder?, s: String?) {
        mapReverseGeoCoder.toString()
        val fieldMap = CommonUtil.convertFromDataClassToMap(AddressRequest(keyword = s!!))
        getAddressList(addressParam = fieldMap)
    }

    override fun onReverseGeoCoderFailedToFindAddress(p0: MapReverseGeoCoder?) {
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
        mMapView.setShowCurrentLocationMarker(false)
    }
}