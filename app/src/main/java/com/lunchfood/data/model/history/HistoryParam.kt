package com.lunchfood.data.model.history

import com.squareup.moshi.Json

data class HistoryParam(
    val id: Long,
    val year: String,
    val month: String,
    @Json(name = "interval_date")
    val intervalDate: Int
)