package com.lunchfood.data.model.filter

import com.squareup.moshi.Json

data class FilterCommonRequest(
    val id: Long,
    @Json(name = "place_id")
    val placeId: Int? = null,
    @Json(name = "place_name")
    val placeName: String? = null,
    val radius: Int? = null,
    val on: Int? = null,
    @Json(name = "set_date")
    val setDate: Int? = null
)
