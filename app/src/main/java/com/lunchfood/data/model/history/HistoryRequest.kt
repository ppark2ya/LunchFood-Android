package com.lunchfood.data.model.history

import com.lunchfood.utils.Constants
import com.squareup.moshi.Json

data class HistoryRequest(
    val id: Long,    // 사용자 id
    @Json(name = "place_id")
    val placeId: String,    // 카카오 api에서 넘어온 식당 식별자(BestMenu.id)
    @Json(name = "place_name")
    val placeName: String,     // 식당이름
    @Json(name = "category_name")
    val categoryName: String,  // 식당종류
    @Json(name = "good_bad")
    val goodBad: Int,          // 선택:1, 거절:0
    @Json(name = "x")
    val lon: String,      // lon
    @Json(name = "y")
    val lat: String,      // lat
    @Json(name = "interval_date")
    val intervalDate: Int? = Constants.INTERVAL_DATE
)