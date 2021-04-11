package com.lunchfood.ui.main.view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.lunchfood.R
import com.lunchfood.data.model.BestMenuRequest
import com.lunchfood.ui.base.BaseFragment
import com.lunchfood.ui.base.GlobalApplication
import com.lunchfood.utils.Constants
import com.lunchfood.utils.Dlog
import com.lunchfood.utils.PreferenceManager
import com.lunchfood.utils.Status
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.header.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class HomeFragment: BaseFragment(), View.OnClickListener {

    private lateinit var homeView: View
    private lateinit var mMapView: MapView
    private var mLat: Double = Constants.LATITUDE_DEFAULT   // 가게 x좌표
    private var mLon: Double = Constants.LONGITUDE_DEFAULT  // 가게 y좌표
    private var mAddress: String = ""       // 가게 주소(도로명)
    private var mDistance: String = ""       // 가게까지 거리
    private var mRestourant: String = ""     // 가게명
    private var x: Double = 0.0          // 사용자 x좌표
    private var y: Double = 0.0          // 사용자 y좌표
    private lateinit var roadAddr: String   // 사용자 설정 주소

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeView = inflater.inflate(R.layout.fragment_home, container, false)

        mMapView = MapView(activity)
        mMapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading

        val mapViewContainer = homeView.findViewById<ViewGroup>(R.id.rlMainMapView)
        mapViewContainer.addView(mMapView)
        val extra = arguments
        if(extra != null) {
            x = extra.getDouble("lat")
            y = extra.getDouble("lon")
            roadAddr = extra.getString("roadAddr", "")
        }

        return homeView
    }

    override fun onResume() {
        super.onResume()
        headerBackBtn.visibility = View.GONE
        val userId = PreferenceManager.getLong("userId")
        getBestMenuList(BestMenuRequest(id = userId, x.toString(), y.toString()))
    }

    private fun setRestaurantLocation() {
        val userNowLocation = MapPoint.mapPointWithGeoCoord(mLat, mLon)
        mMapView.setMapCenterPoint(userNowLocation, true)

        val marker = MapPOIItem()
        marker.itemName = mAddress
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

        tvRestaurantTitleName.text = mRestourant
        tvRestaurantName.text = mAddress
        "${mDistance}m".also { tvRestaurantDistance.text = it }
    }

    private fun getBestMenuList(data: BestMenuRequest) {
        GlobalApplication.getViewModel()!!.getBestMenuList(data).observe(this, {
            it?.let { resource ->
                when(resource.status) {
                    Status.PENDING -> {
                        loadingStart()
                    }
                    Status.SUCCESS -> {
                        loadingEnd()
                        resource.data?.let { res ->
                            if(res.resultCode == 200) {
                                val bestMenuList = res.data!!
                                Dlog.i("메뉴목록 데이터::: $bestMenuList")
                                mLat = bestMenuList[0].lat
                                mLon = bestMenuList[0].lon
                                mAddress = bestMenuList[0].addressName
                                mDistance = bestMenuList[0].distance
                                mRestourant = bestMenuList[0].placeName
                                setRestaurantLocation()
                            }
                        }
                    }
                    Status.FAILURE -> {
                        loadingEnd()
                        Dlog.e("getBestMenuList FAILURE : ${it.message}")
                    }
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.nextPlace -> {
                Toast.makeText(activity, "asdad", Toast.LENGTH_SHORT)
            }
        }
    }
}