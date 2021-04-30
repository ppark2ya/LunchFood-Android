package com.lunchfood.data.model

import com.lunchfood.utils.Constants.Companion.DEFAULT_USER_RADIUS
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
    val id: Long,
    val age: String? = "0",
    val birthday: String? = "0",
    val birthyear: String? = "0",
    val gender: String? = "0",
    @Json(name = "y")
    val lat: String? = "",
    @Json(name = "x")
    val lon: String? = "",
    val address: String? = "",    // 도로명으로 default
    val type: String? = "",  // UTMK or WGS84
    val radius: Int = DEFAULT_USER_RADIUS,
    @Json(name = "radius_on")
    val radiusOn: Int = 0,
    @Json(name = "place_on")
    val placeOn: Int = 0,   // 0: 비활성, 1: 활성
    @Json(name = "date_on")
    val dateOn: Int = 0,
    @Json(name = "set_date")
    val setDate: Int = 0,
)