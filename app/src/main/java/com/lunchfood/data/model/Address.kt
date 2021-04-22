package com.lunchfood.data.model

import com.lunchfood.utils.Constants.Companion.OPEN_API_ROAD_ADDRESS_KEY
import com.squareup.moshi.JsonClass

data class AddressRequest(
    val confmKey: String = OPEN_API_ROAD_ADDRESS_KEY,
    val currentPage: Int = 1,
    val countPerPage: Int = 10,
    val resultType: String = "json",
    val keyword: String = "",
    val admCd: String? = "",      // 행정구역코드
    val rnMgtSn: String? = "",    // 도로명코드
    val udrtYn:	String? = "",		// 지하여부(0 : 지상, 1 : 지하)
    val buldMnnm: Int? = 0,	    // 건물본번
    val buldSlno: Int? = 0,	    // 건물부번
    val roadAddr: String? = "", // 도로명주소
)

@JsonClass(generateAdapter = true)
data class AddressResponse(
    val results: AddressResult,
)

@JsonClass(generateAdapter = true)
data class AddressCoordResponse(
    val results: AddressCoordResult,
)

@JsonClass(generateAdapter = true)
data class AddressResult(
    val common: AddressCommonResult,
    val juso: List<AddressItem>?,
)

@JsonClass(generateAdapter = true)
data class AddressCoordResult(
    val common: AddressCommonResult,
    val juso: List<AddressCoordItem>?
)

@JsonClass(generateAdapter = true)
data class AddressCommonResult(
    val errorMessage: String,
    val errorCode: String,
    val currentPage: Int?,
    val countPerPage: Int?,
    val totalCount: String,
)

@JsonClass(generateAdapter = true)
data class AddressItem(
    val roadAddr: String,   // 전체 도로명주소
    val roadAddrPart1: String,  // 도로명주소(참고항목 제외)
    val roadAddrPart2: String?, // 도로명주소 참고항목
    val jibunAddr: String,  // 지번주소
    val engAddr: String,    // 도로명주소(영문)
    val zipNo: String,      // 우편번호
    val admCd: String,      // 행정구역코드
    val rnMgtSn: String,    // 도로명코드
    val bdMgtSn: String,    // 건물관리번호
    val detBdNmList: String?,   // 상세건물명
    val bdNm: String?,      // 건물명
    val bdKdcd: String,     // 공동주택여부(1 : 공동주택, 0 : 비공동주택)
    val siNm: String,       // 시도명
    val sggNm: String,      // 시군구명
    val emdNm: String,		// 읍면동명
    val liNm: String?,		// 법정리명
    val rn:	String, 	    // 도로명
    val udrtYn:	String,		// 지하여부(0 : 지상, 1 : 지하)
    val buldMnnm: Int,	    // 건물본번
    val buldSlno: Int,	    // 건물부번
    val mtYn: String,		// 산여부(0 : 대지, 1 : 산)
    val lnbrMnnm: Int,	    // 지번본번(번지)
    val lnbrSlno: Int,	    // 지번부번(호)
    val emdNo: String,		// 읍면동일련번호
    val hstryYn: String?,	// 변동이력여부(0: 현행 주소정보, 1: 요청변수의 keyword(검색어)가 변동된 주소정보에서 검색된 정보)
    val relJibun: String?,	// 관련지번
    val hemdNm:	String?		// 관할주민센터 ※ 참고정보이며, 실제와 다를 수 있습니다.
)

@JsonClass(generateAdapter = true)
data class AddressCoordItem(
    val admCd: String,      // 행정구역코드
    val rnMgtSn: String,    // 도로명코드
    val bdMgtSn: String,    // 건물관리번호
    val udrtYn:	String,		// 지하여부(0 : 지상, 1 : 지하)
    val buldMnnm: Int,	    // 건물본번
    val buldSlno: Int,	    // 건물부번
    val entX: String,       // X좌표
    val entY: String,       // Y좌표
    val bdNm: String?,      // 건물명
)