package com.lunchfood.data.model

import com.lunchfood.utils.Constants.Companion.DEFAULT_USER_RADIUS
import com.lunchfood.utils.Constants.Companion.INTERVAL_DATE
import com.squareup.moshi.Json

data class BestMenuRequest(
    val id: Long,
    @Json(name = "interval_date")
    val intervalDate: Int = INTERVAL_DATE
)

data class BestMenu(
    @Json(name = "address_name")
    val addressName: String,
    @Json(name = "category_group_code")
    val categoryGroupCode: String,
    @Json(name = "category_group_name")
    val categoryGroupName: String,
    @Json(name = "category_name")
    val categoryName: String,
    val distance: String,
    @Json(name = "id")
    val placeId: String,
    val phone: String,
    @Json(name = "place_name")
    val placeName: String,
    @Json(name = "place_url")
    val placeUrl: String,
    @Json(name = "road_address_name")
    val roadAddressName: String,
    @Json(name = "x")
    val lon: Double,
    @Json(name = "y")
    val lat: Double,
)