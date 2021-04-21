package com.lunchfood.data.model

import com.lunchfood.utils.Constants.Companion.INTERVAL_DATE
import com.squareup.moshi.Json
import java.io.Serializable

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
    val intervalDate: Int? = INTERVAL_DATE
)

data class HistoryResponse(
    val id: Long,    // 사용자 id
    @Json(name = "place_id")
    val placeId: Int,    // 카카오 api에서 넘어온 식당 식별자(BestMenu.id)
    @Json(name = "place_name")
    val placeName: String,     // 식당이름
    @Json(name = "menu_name")
    val menuName: String?,     // 음식명
    val category: String,  // 식당종류
    val score: String?,     // 평점
    @Json(name = "menu_diary")
    val menuDiary: String?,
    @Json(name = "menu_image_1")
    val menuImage1: String?,
    @Json(name = "menu_image_2")
    val menuImage2: String?,
    @Json(name = "menu_image_3")
    val menuImage3: String?,
    @Json(name = "menu_image_4")
    val menuImage4: String?,
    @Json(name = "menu_image_5")
    val menuImage5: String?,
    @Json(name = "inserted_date")
    val insertedDate: String
): Serializable

data class HistoryParam(
    val id: Long,
    val year: String,
    val month: String,
    @Json(name = "interval_date")
    val intervalDate: Int
)