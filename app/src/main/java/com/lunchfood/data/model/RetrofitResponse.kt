package com.lunchfood.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RetrofitResponse<T>(
    val resultCode: Int,
    val resultMsg: String,
    val data: T?
)
