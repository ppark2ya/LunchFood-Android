package com.lunchfood.data.model.history

import com.squareup.moshi.Json
import java.io.Serializable

data class DayMenuInsertParam(
    val id: Long,    // 사용자 id
    @Json(name = "place_id")
    val placeId: Int,    // 카카오 api에서 넘어온 식당 식별자(BestMenu.id)
    @Json(name = "place_name")
    val placeName: String,     // 식당이름
    @Json(name = "menu_name")
    val menuName: String? = "",     // 음식명
    val category: String? = "",  // 식당종류
    val score: Int = 0,     // 평점
    @Json(name = "menu_text")
    val menuText: String? = "",  // 먹방일지 (빈값이면 'Null' 입력)
    @Json(name = "menu_img_1")
    val menuImage1: String? = "",
    @Json(name = "menu_img_2")
    val menuImage2: String? = "",
    @Json(name = "menu_img_3")
    val menuImage3: String? = "",
    @Json(name = "menu_img_4")
    val menuImage4: String? = "",
    @Json(name = "menu_img_5")
    val menuImage5: String? = "",
    @Json(name = "inserted_date")
    val insertedDate: String
): Serializable