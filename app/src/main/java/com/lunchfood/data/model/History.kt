package com.lunchfood.data.model

import com.lunchfood.utils.Constants.Companion.INTERVAL_DATE
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
    val x: String,      // lon
    val y: String,      // lat
    @Json(name = "interval_date")
    val intervalDate: Int? = INTERVAL_DATE
)

data class HistoryResponse(
    val id: Long,    // 사용자 id
    @Json(name = "place_id")
    val placeId: Int,    // 카카오 api에서 넘어온 식당 식별자(BestMenu.id)
    @Json(name = "place_name")
    val placeName: String,     // 식당이름
    val category: String,  // 식당종류
    @Json(name = "good_bad")
    val goodBad: Int,          // 선택:1, 거절:0
    val x: String,      // lon
    val y: String,      // lat
    @Json(name = "inserted_date")
    val insertedDate: String
)

data class HistoryParam(
    val id: Long,
    val year: String,
    val month: String,
    @Json(name = "interval_date")
    val intervalDate: Int
)