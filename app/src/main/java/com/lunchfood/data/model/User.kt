package com.lunchfood.data.model

data class User(
        val id: Long,
        val age: String? = null,
        val birthday: String? = null,
        val birthyear: String? = null,
        val gender: String? = null
)
