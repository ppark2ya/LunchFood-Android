package com.lunchfood.data.api

import com.lunchfood.data.model.*
import com.lunchfood.data.model.filter.FilterCommonRequest
import com.lunchfood.data.model.filter.SelectedPlace
import com.lunchfood.data.model.history.*
import retrofit2.http.*

interface ApiService {
    /**
     * @param id: 사용자 ID
     * @param age: 나이
     * @param birthday: 생일
     * @param birthyear:
     * @param gender: 성별
     * @desc 회원가입
     */
    @POST("/account/insert_acc")
    suspend fun insertAccount(@Body data: User): RetrofitResponse<Any>

    /**
     * @param id: 사용자 ID
     * @desc 사용자 정보 조회
     */
    @POST("/account/get_acc")
    suspend fun getAccount(@Body data: User): RetrofitResponse<User>

    /**
     * @param id: 사용자 ID
     * @param x: x좌표
     * @param y: y좌표
     * @param address: 도로명주소
     * @desc 사용자 정보 조회
     */
    @POST("/account/update_location")
    suspend fun updateLocation(@Body data: User): RetrofitResponse<User>

    /**
     * @param keyword: 사용자 주소
     * @desc 주소지 조회
     */
    @FormUrlEncoded
    @POST("/addrlink/addrLinkApi.do")
    suspend fun getAddressList(@FieldMap addressParam: HashMap<String, Any>): AddressResponse

    /**
     * @param admCd: 행정구역코드
     * @param rnMgtSn: 도로명코드
     * @param udrtYn: 지하여부(0 : 지상, 1 : 지하)
     * @param buldMnnm: 건물본번
     * @param buldSlno: 건물부번
     * @desc 도로명주소 좌표계 조회(UTM-K 좌표계 사용)
     */
    @FormUrlEncoded
    @POST("/addrlink/addrCoordApi.do")
    suspend fun getAddressCoord(@FieldMap addressParam: HashMap<String, Any>): AddressCoordResponse

    /**
     * @param id: 사용자 ID
     * @param x
     * @param y
     * @param interval_date
     * @desc 추천 식당 정보 반환
     */
    @POST("/recommend/best_menu")
    suspend fun getBestMenuList(@Body data: BestMenuRequest): RetrofitResponse<List<BestMenu>>

    /**
     * @param id: 사용자 ID
     * @param place_id: 식당 식별자
     * @param place_name: 식당명
     * @param category_name: 식당종류
     * @param good_bad: 선택: 1, 거절: 0
     * @param x: lon
     * @param y: lat
     * @param interval_date
     * @desc 추천 메뉴 선택 혹은 거절 했을 때 log 입력
     */
    @POST("/history/insert_history")
    suspend fun insertHistory(@Body data: HistoryRequest): RetrofitResponse<Any>

    /**
     * @param id: 사용자 ID
     * @param interval_date
     * @desc 최근 선택한 음식점 히스토리 반환
     */
    @POST("/history/get_place_history")
    suspend fun getPlaceHistory(@Body data: HistoryParam): RetrofitResponse<List<HistoryResponse>>

    /**
     * @param id: 사용자 ID
     * @param interval_date
     * @desc 최근 선택한 음식점 히스토리 반환
     */
    @GET("/history/check_today")
    suspend fun checkToday(@Query("id") id: Long): RetrofitResponse<List<BestMenu>>

    /**
     * @param q: 사용자 입력 문자열
     * @desc 음식점 자동완성 api
     */
    @POST("/history/place_auto")
    suspend fun getPlaceAuto(@Body data: CommonParam): RetrofitResponse<List<PlaceInfo>>

    /**
     * @param q: 사용자 입력 문자열
     * @desc 음식명 자동완성 api
     */
    @POST("/history/food_auto")
    suspend fun getFoodAuto(@Body data: CommonParam): RetrofitResponse<List<String>>

    /**
     * @param DayMenuInsertParam
     * @desc 데이메뉴 등록 api
     */
    @POST("/history/insert_day_menu")
    suspend fun insertDayMenu(@Body data: DayMenuInsertParam): RetrofitResponse<Any>

    /**
     * @param DayMenuDeleteParam
     * @desc 데이메뉴 삭제 api
     */
    @POST("/history/delete_day_menu")
    suspend fun deleteDayMenu(@Body data: DayMenuDeleteParam): RetrofitResponse<Any>

    /**
     * @param id: 계정 식별자
     * @param place_id: 카카오 api에서 넘어온 식당 식별자
     * @param place_name: 식당이름
     * @desc 선호 음식점 입력
     */
    @POST("/filter/insert_selected_place")
    suspend fun insertSelectedPlace(@Body data: FilterCommonRequest): RetrofitResponse<Any>

    /**
     * @param id: 계정 식별자
     * @param radius: 거리 필터링 값 (on=0 일때, 0값 입력하면 됨)
     * @param radius_on: 거리 제한 필터 (0:비활성, 1:활성)
     * @param place_on: 식당 필터 (0:비활성, 1:활성)
     * @param set_date: 제한 일자
     * @param date_on: 제한 일자 필터 (0:비활성, 1:활성)
     * @desc 계정 정보에 필터링 정보 업데이트
     */
    @POST("/filter/update_filter")
    suspend fun updateFilter(@Body data: FilterCommonRequest): RetrofitResponse<Any>

    /**
     * @param id: 계정 식별자
     * @desc 음식점 필터링 정보 반환
     */
    @POST("/filter/get_selected_place")
    suspend fun getSelectedPlace(@Body data: FilterCommonRequest): RetrofitResponse<List<SelectedPlace>>

    /**
     * @param id: 계정 식별자
     * @param place_id: 음식점 식별자
     * @desc 음식점 필터링 정보 삭제
     */
    @POST("/filter/delete_selected_place")
    suspend fun deleteSelectedPlace(@Body data: FilterCommonRequest): RetrofitResponse<Any>
}