package com.lunchfood.utils

class Constants {
    companion object {
        const val BASE_URL = "http://13.209.115.50:3500/"
        const val KOROAD_URL = "https://www.juso.go.kr"
        // 도로명주소 API
        const val OPEN_API_ROAD_ADDRESS_KEY = "U01TX0FVVEgyMDIxMDQxMDIxMzQyNzExMTAzNDU="
        // 좌표제공 API
        const val OPEN_API_LOCATION_KEY = "U01TX0FVVEgyMDIxMDQxMDIxMzExODExMTAzNDQ="
        // 서울역을 기본 좌표값으로 설정
        const val LATITUDE_DEFAULT = 37.5076415
        const val LONGITUDE_DEFAULT = 127.0556521
        // 며칠 전 메뉴까지 고려해서 추천할 것인지
        const val INTERVAL_DATE = 3
        // 제한 거리 기본 값
        const val DEFAULT_USER_RADIUS = 1000
        // AWS Cognito 인증값
        const val AWS_COGNITO_CREDENTIAL_POOL_ID = "ap-northeast-2:7be65578-c769-4ee4-85ba-e38f24e77da7"
    }
}