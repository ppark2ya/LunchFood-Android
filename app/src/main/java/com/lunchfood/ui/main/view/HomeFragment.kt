package com.lunchfood.ui.main.view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lunchfood.R
import com.lunchfood.data.model.BestMenu
import com.lunchfood.data.model.BestMenuRequest
import com.lunchfood.data.model.history.HistoryRequest
import com.lunchfood.ui.base.BaseFragment
import com.lunchfood.ui.base.GlobalApplication
import com.lunchfood.utils.Constants
import com.lunchfood.utils.Dlog
import com.lunchfood.utils.Status
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.header.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class HomeFragment: BaseFragment() {

    private val mainViewModel by lazy { GlobalApplication.getViewModel() }
    private lateinit var mMapView: MapView
    private var mLat: Double = Constants.LATITUDE_DEFAULT   // 가게 y좌표
    private var mLon: Double = Constants.LONGITUDE_DEFAULT  // 가게 x좌표
    private var mAddress: String = ""       // 가게 주소(도로명)
    private var mDistance: String = ""       // 가게까지 거리
    private var mRestaurant: String = ""     // 가게명
    private var mRestaurantUrl: String = ""  // 가게 url
    private var userLon: Double = 0.0
    private var userLat: Double = 0.0
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
        GlobalApplication.mUserData.let {
            userLat = it.lat!!.toDouble()
            userLon = it.lon!!.toDouble()
            roadAddr = it.address!!
        }

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        headerBackBtn.visibility = View.GONE

        mMapView = MapView(activity)
        rlMainMapView.addView(mMapView)

        setUserLocation()
        setupEventListener(view)

        mNextIndex = 0
        getBestMenuList(BestMenuRequest(id = userId))

        lunchChoice.visibility = View.GONE
    }

    private fun setupEventListener(view: View) {
        nextPlace.setOnClickListener {
            insertHistory(makeRequestBody(0))
        }

        lunchChoice.setOnClickListener {
            insertHistory(makeRequestBody(1))
        }
    }

    private fun setUserLocation() {
        val userNowLocation = MapPoint.mapPointWithGeoCoord(userLat, userLon)

        MapPOIItem().let { userMarker ->
            userMarker.itemName = roadAddr
            userMarker.tag = 1
            userMarker.mapPoint = userNowLocation
            userMarker.markerType = MapPOIItem.MarkerType.CustomImage
            userMarker.customImageBitmap = BitmapFactory.decodeResource(resources, R.drawable.gps_img_red).let {
                Bitmap.createScaledBitmap(
                    it, 80, 90, false
                )
            }
            userMarker.isCustomImageAutoscale = false
            userMarker.setCustomImageAnchor(0.5f, 1.0f)
            mMapView.addPOIItem(userMarker)
        }
    }

    private fun setRestaurantLocation() {
        if(prevMarker != null) {
            mMapView.removePOIItem(prevMarker)
        }

        if(mDistance.toInt() > 400) {
            mMapView.setZoomLevel(3, true)
        } else {
            mMapView.setZoomLevel(2, true)
        }

        val centerLocation = MapPoint.mapPointWithGeoCoord((userLat + mLat) / 2, (userLon + mLon) / 2)
        mMapView.setMapCenterPoint(centerLocation, true)

        prevMarker = MapPOIItem()
        prevMarker?.let { marker ->
            marker.itemName = mAddress
            marker.tag = 0
            marker.mapPoint = MapPoint.mapPointWithGeoCoord(mLat, mLon)
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
        tvRestaurantUrl.text = mRestaurantUrl
    }

    private fun getBestMenuList(data: BestMenuRequest) {
        mainViewModel?.let { model ->
            model.getBestMenuList(data).observe(viewLifecycleOwner, {
                it.let { resource ->
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
    }

    private fun showRestaurant() {
        mCurrentItem = bestMenuList[mNextIndex]
        mLat = bestMenuList[mNextIndex].lat
        mLon = bestMenuList[mNextIndex].lon
        mAddress = bestMenuList[mNextIndex].addressName
        mDistance = bestMenuList[mNextIndex].distance
        mRestaurant = bestMenuList[mNextIndex].placeName
        mRestaurantUrl = bestMenuList[mNextIndex].placeUrl
        mNextIndex++
        setRestaurantLocation()
    }

    private fun makeRequestBody(goodBad: Int): HistoryRequest {
        return HistoryRequest(
            id = userId,
            placeId = mCurrentItem.placeId,
            placeName = mCurrentItem.placeName,
            categoryName = mCurrentItem.categoryName,
            goodBad = goodBad,
            lon = mCurrentItem.lon.toString(),
            lat = mCurrentItem.lat.toString()
        )
    }

    private fun insertHistory(data: HistoryRequest) {
        mainViewModel?.let { model ->
            model.insertHistory(data).observe(viewLifecycleOwner, {
                it.let { resource ->
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
}