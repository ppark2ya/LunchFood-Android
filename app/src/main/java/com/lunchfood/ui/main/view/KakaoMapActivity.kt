package com.lunchfood.ui.main.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.lunchfood.R
import com.lunchfood.data.api.ApiHelper
import com.lunchfood.data.api.RetrofitBuilder
import com.lunchfood.ui.base.GlobalApplication
import com.lunchfood.ui.base.ViewModelFactory
import com.lunchfood.ui.main.viewmodel.MainViewModel
import com.lunchfood.utils.Constants.Companion.LATITUDE_DEFAULT
import com.lunchfood.utils.Constants.Companion.LONGITUDE_DEFAULT
import com.lunchfood.utils.Dlog
import kotlinx.android.synthetic.main.activity_kakao_map_custom.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapReverseGeoCoder
import net.daum.mf.map.api.MapView

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
    }

    private fun setupMapView() {
        mMapView = MapView(this)
        mMapView.setCurrentLocationEventListener(this)

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
        mReverseGeoCoder = MapReverseGeoCoder(getString(R.string.kakao_app_key), mMapView.mapCenterPoint, this@KakaoMapActivity, this@KakaoMapActivity)
        mReverseGeoCoder.startFindingAddress()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
                this,
                ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(MainViewModel::class.java)
    }

    override fun onCurrentLocationUpdate(mapView: MapView?, currentLocation: MapPoint?, accuracyInMeters: Float) {
        val mapPointGeo: MapPoint.GeoCoordinate? = currentLocation?.mapPointGeoCoord
        mReverseGeoCoder = MapReverseGeoCoder(getString(R.string.kakao_app_key), mMapView.mapCenterPoint, this@KakaoMapActivity, this@KakaoMapActivity)
        mReverseGeoCoder.startFindingAddress()
    }

    override fun onCurrentLocationDeviceHeadingUpdate(p0: MapView?, p1: Float) {}

    override fun onCurrentLocationUpdateFailed(p0: MapView?) {}

    override fun onCurrentLocationUpdateCancelled(p0: MapView?) {}

    override fun onReverseGeoCoderFoundAddress(mapReverseGeoCoder: MapReverseGeoCoder?, s: String?) {
        mapReverseGeoCoder.toString()
        Dlog.e("현재주소::: $s")
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }

    override fun onReverseGeoCoderFailedToFindAddress(p0: MapReverseGeoCoder?) {
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
        mMapView.setShowCurrentLocationMarker(false)
    }
}