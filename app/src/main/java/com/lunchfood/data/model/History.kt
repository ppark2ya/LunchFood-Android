package com.lunchfood.data.model

import com.lunchfood.utils.Constants.Companion.INTERVAL_DATE

data class HistoryRequest(
    val id: Long,    // 사용자 id
    val place_id: String,    // 카카오 api에서 넘어온 식당 식별자(BestMenu.id)
    val place_name: String,     // 식당이름
    val category_name: String,  // 식당종류
    val good_bad: Int,          // 선택:1, 거절:0
    val x: String,      // lon
    val y: String,      // lat
    val interval_date: Int? = INTERVAL_DATE
)