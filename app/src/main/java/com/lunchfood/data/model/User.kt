package com.lunchfood.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
    val id: Long,
    val age: String? = "0",
    val birthday: String? = "0",
    val birthyear: String? = "0",
    val gender: String? = "0",
    val x: String? = "",
    val y: String? = "",
    val address: String? = "",    // 도로명으로 default
    val type: String? = ""  // UTMK or WGS84
)