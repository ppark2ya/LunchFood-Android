package com.lunchfood.data.api

import com.lunchfood.data.model.*
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
    suspend fun getPlaceHistory(@Body data: HistoryParam): RetrofitResponse<HistoryResponse>
}