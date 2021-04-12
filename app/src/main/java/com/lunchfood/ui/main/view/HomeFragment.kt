package com.lunchfood.ui.main.view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.lunchfood.R
import com.lunchfood.data.model.BestMenu
import com.lunchfood.data.model.BestMenuRequest
import com.lunchfood.data.model.HistoryRequest
import com.lunchfood.ui.base.BaseFragment
import com.lunchfood.ui.base.GlobalApplication
import com.lunchfood.utils.Constants
import com.lunchfood.utils.Dlog
import com.lunchfood.utils.PreferenceManager
import com.lunchfood.utils.Status
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.header.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class HomeFragment: BaseFragment() {

    private lateinit var homeView: View
    private lateinit var mMapView: MapView
    private var mLat: Double = Constants.LATITUDE_DEFAULT   // 가게 y좌표
    private var mLon: Double = Constants.LONGITUDE_DEFAULT  // 가게 x좌표
    private var mAddress: String = ""       // 가게 주소(도로명)
    private var mDistance: String = ""       // 가게까지 거리
    private var mRestaurant: String = ""     // 가게명
    private var x: Double = 0.0          // 사용자 x좌표
    private var y: Double = 0.0          // 사용자 y좌표
    private lateinit var roadAddr: String   // 사용자 설정 주소
    private lateinit var bestMenuList: List<BestMenu>
    private lateinit var mCurrentItem: BestMenu
    private var mSize = 0   // 추천 식당 전체 개수
    private var mNextIndex = 0
    private var prevMarker: MapPOIItem? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeView = inflater.inflate(R.layout.fragment_home, container, false)

        mMapView = MapView(activity)
        // mMapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading

        val mapViewContainer = homeView.findViewById<ViewGroup>(R.id.rlMainMapView)
        mapViewContainer.addView(mMapView)
        val extra = arguments
        if(extra != null) {
            x = extra.getDouble("lat")
            y = extra.getDouble("lon")
            roadAddr = extra.getString("roadAddr", "")
        }

        setupEventListener()

        return homeView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        headerBackBtn.visibility = View.GONE
        mNextIndex = 0
        getBestMenuList(BestMenuRequest(id = userId, x.toString(), y.toString()))
    }

    private fun setupEventListener() {
        homeView.findViewById<CardView>(R.id.nextPlace).setOnClickListener {
            insertHistory(makeRequestBody(0))
        }

        homeView.findViewById<CardView>(R.id.lunchChoice).setOnClickListener {
            insertHistory(makeRequestBody(1))
        }
    }

    private fun setRestaurantLocation() {
        if(prevMarker != null) {
            mMapView.removePOIItem(prevMarker)
        }

        val userNowLocation = MapPoint.mapPointWithGeoCoord(mLat, mLon)
        mMapView.setMapCenterPoint(userNowLocation, true)

        prevMarker = MapPOIItem()
        prevMarker?.let { marker ->
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
            mMapView.addPOIItem(prevMarker)
        }

        tvRestaurantTitleName.text = mRestaurant
        tvRestaurantName.text = mAddress
        "${mDistance}m".also { tvRestaurantDistance.text = it }
    }

    private fun getBestMenuList(data: BestMenuRequest) {
        GlobalApplication.getViewModel()!!.getBestMenuList(data).observe(viewLifecycleOwner, {
            it?.let { resource ->
                when(resource.status) {
                    Status.PENDING -> {
                        mainActivity.loadingStart()
                    }
                    Status.SUCCESS -> {
                        mainActivity.loadingEnd()
                        resource.data?.let { res ->
                            if(res.resultCode == 200) {
                                bestMenuList = res.data!!
                                mSize = bestMenuList.size
                                Dlog.i("메뉴목록 데이터::: $bestMenuList")
                                showRestaurant()
                            }
                        }
                    }
                    Status.FAILURE -> {
                        mainActivity.loadingEnd()
                        Dlog.e("getBestMenuList FAILURE : ${it.message}")
                    }
                }
            }
        })
    }

    private fun showRestaurant() {
        mCurrentItem = bestMenuList[mNextIndex]
        mLat = bestMenuList[mNextIndex].lat
        mLon = bestMenuList[mNextIndex].lon
        mAddress = bestMenuList[mNextIndex].addressName
        mDistance = bestMenuList[mNextIndex].distance
        mRestaurant = bestMenuList[mNextIndex].placeName
        mNextIndex++
        setRestaurantLocation()
    }

    private fun makeRequestBody(goodBad: Int): HistoryRequest {
        return HistoryRequest(
            id = userId,
            place_id = mCurrentItem.placeId,
            place_name = mCurrentItem.placeName,
            category_name = mCurrentItem.categoryName,
            good_bad = goodBad,
            x = mCurrentItem.lon.toString(),
            y = mCurrentItem.lat.toString()
        )
    }

    private fun insertHistory(data: HistoryRequest) {
        GlobalApplication.getViewModel()!!.insertHistory(data).observe(viewLifecycleOwner, {
            it?.let { resource ->
                when(resource.status) {
                    Status.PENDING -> {
                        mainActivity.loadingStart()
                    }
                    Status.SUCCESS -> {
                        mainActivity.loadingEnd()
                        resource.data?.let { res ->
                            if(res.resultCode == 200) {
                                if(mSize > mNextIndex) {
                                    // TODO: 선택했을 때 시나리오 필요할 듯
                                    showRestaurant()
                                }
                            }
                        }
                    }
                    Status.FAILURE -> {
                        mainActivity.loadingEnd()
                        Dlog.e("insertHistory FAILURE : ${it.message}")
                    }
                }
            }
        })
    }
}