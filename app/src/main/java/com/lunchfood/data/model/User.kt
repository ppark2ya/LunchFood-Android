package com.lunchfood.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserResponse(
    val result: User,
)

data class User(
    val id: Long,
    val age: String? = null,
    val birthday: String? = null,
    val birthyear: String? = null,
    val gender: String? = null
)
