package com.lunchfood.data.model.filter

import com.squareup.moshi.Json

data class SelectedPlace(
    @Json(name = "place_id")
    val placeId: Int,
    @Json(name = "place_name")
    val placeName: String,
)
