package com.lunchfood.data.model.history

import com.squareup.moshi.Json
import java.io.Serializable

data class PlaceInfo(
    @Json(name = "address_name")
    val addressName: String,
    @Json(name = "category_group_code")
    val categoryGroupCode: String,
    @Json(name = "category_group_name")
    val categoryGroupName: String,
    @Json(name = "category_name")
    val categoryName: String,
    val distance: String,
    val id: Long,
    val phone: String,
    @Json(name = "place_name")
    val placeName: String,
    @Json(name = "place_url")
    val placeUrl: String,
    @Json(name = "road_address_name")
    val roadAddressName: String,
    @Json(name = "x")
    val lon: String,
    @Json(name = "y")
    val lat: String,
): Serializable
