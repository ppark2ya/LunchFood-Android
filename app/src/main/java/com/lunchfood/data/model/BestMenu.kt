package com.lunchfood.data.model

import com.google.gson.annotations.SerializedName
import com.lunchfood.utils.Constants.Companion.INTERVAL_DATE

data class BestMenuRequest(
    val id: Long,
    val x: String,
    val y: String,
    val interval_date: Int = INTERVAL_DATE
)

data class BestMenu(
    @SerializedName("address_name")
    val addressName: String,
    @SerializedName("category_group_code")
    val categoryGroupCode: String,
    @SerializedName("category_group_name")
    val categoryGroupName: String,
    @SerializedName("category_name")
    val categoryName: String,
    val distance: String,
    val id: String,
    val phone: String,
    @SerializedName("place_name")
    val placeName: String,
    @SerializedName("place_url")
    val placeUrl: String,
    @SerializedName("road_address_name")
    val roadAddressName: String,
    @SerializedName("x")
    val lat: Double,
    @SerializedName("y")
    val lon: Double,
)