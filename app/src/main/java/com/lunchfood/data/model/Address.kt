package com.lunchfood.data.model

import com.lunchfood.utils.Constants.Companion.KOROAD_DEV_KEY

data class AddressRequest(
    val confmKey: String = KOROAD_DEV_KEY,
    val currentPage: Int = 1,
    val countPerPage: Int = 10,
    val resultType: String = "json",
    val keyword: String = ""
)

data class AddressResponse(
    val results: AddressResult,
)

data class AddressResult(
    val common: AddressCommonResult,
    val juso: List<AddressItem>?
)

data class AddressCommonResult(
    val errorMessage: String,
    val errorCode: String,
    val currentPage: String,
    val countPerPage: String,
    val totalCount: String,
) {
    companion object {
        fun from(map: Map<String, String>) = object {
            val errorMessage by map
            val errorCode by map
            val currentPage by map
            val countPerPage by map
            val totalCount by map
            val data = AddressCommonResult(
                errorMessage,
                errorCode,
                currentPage,
                countPerPage,
                totalCount
            )
        }.data
    }
}

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
    val buldMnnm: String,	// 건물본번
    val buldSlno: String,	// 건물부번
    val mtYn: String,		// 산여부(0 : 대지, 1 : 산)
    val lnbrMnnm: String,	// 지번본번(번지)
    val lnbrSlno: String,	// 지번부번(호)
    val emdNo: String,		// 읍면동일련번호
    val hstryYn: String?,	// 변동이력여부(0: 현행 주소정보, 1: 요청변수의 keyword(검색어)가 변동된 주소정보에서 검색된 정보)
    val relJibun: String?,	// 관련지번
    val hemdNm:	String?		// 관할주민센터 ※ 참고정보이며, 실제와 다를 수 있습니다.
) {
    companion object {
        fun from(map: Map<String, String>) = object {
            val roadAddr by map
            val roadAddrPart1 by map
            val roadAddrPart2 = map["roadAddrPart2"]
            val jibunAddr by map
            val engAddr by map
            val zipNo by map
            val admCd by map
            val rnMgtSn by map
            val bdMgtSn by map
            val detBdNmList = map["detBdNmList"]
            val bdNm by map
            val bdKdcd by map
            val siNm by map
            val sggNm by map
            val emdNm by map
            val liNm = map["liNm"]
            val rn by map
            val udrtYn by map
            val buldMnnm by map
            val buldSlno by map
            val mtYn by map
            val lnbrMnnm by map
            val lnbrSlno by map
            val emdNo by map
            val hstryYn = map["hstryYn"]
            val relJibun = map["relJibun"]
            val hemdNm = map["hemdNm"]

            val data = AddressItem(
                roadAddr,
                roadAddrPart1,
                roadAddrPart2,
                jibunAddr,
                engAddr,
                zipNo,
                admCd,
                rnMgtSn,
                bdMgtSn,
                detBdNmList,
                bdNm,
                bdKdcd,
                siNm,
                sggNm,
                emdNm,
                liNm,
                rn,
                udrtYn ,
                buldMnnm,
                buldSlno,
                mtYn,
                lnbrMnnm,
                lnbrSlno,
                emdNo,
                hstryYn,
                relJibun,
                hemdNm,
            )
        }.data
        
        fun copyItem(item: AddressItem) = object {
            val data = item.copy()
        }.data
    }
}