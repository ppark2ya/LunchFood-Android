package com.lunchfood.ui.main.view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lunchfood.R
import com.lunchfood.utils.Constants
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapReverseGeoCoder
import net.daum.mf.map.api.MapView

class HomeFragment: Fragment() {

    private lateinit var homeView: View
    private lateinit var mMapView: MapView
    private lateinit var mReverseGeoCoder: MapReverseGeoCoder

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeView = inflater.inflate(R.layout.fragment_home, container, false)
        mMapView = MapView(activity)
        // mMapView.setCurrentLocationEventListener(this)
        // mMapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading

        val mapViewContainer = homeView.findViewById<ViewGroup>(R.id.rlMainMapView)
        mapViewContainer.addView(mMapView)

        val lat = Constants.LATITUDE_DEFAULT
        val lon = Constants.LONGITUDE_DEFAULT
        val userNowLocation = MapPoint.mapPointWithGeoCoord(lat, lon)
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

        return homeView
    }
}