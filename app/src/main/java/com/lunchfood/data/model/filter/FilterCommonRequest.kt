package com.lunchfood.data.model.filter

import com.squareup.moshi.Json

data class FilterCommonRequest(
    val id: Long,
    @Json(name = "place_id")
    val placeId: Int? = null,
    @Json(name = "place_name")
    val placeName: String? = null,
    val radius: Int? = null,    // 제한거리 (on=0 일때, 0값 입력하면 됨)
    @Json(name = "radius_on")
    val radiusOn: Int? = null,  // 거리 제한 필터 (0:비활성, 1:활성)
    @Json(name = "place_on")
    val placeOn: Int? = null,   // 식당 필터 (0:비활성, 1:활성)
    @Json(name = "set_date")
    val setDate: Int? = null,   // 제한일자
    @Json(name = "date_on")
    val dateOn: Int? = null     // 제한 일자 필터 (0:비활성, 1:활성)
)
