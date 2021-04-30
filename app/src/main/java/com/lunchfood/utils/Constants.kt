package com.lunchfood.utils

class Constants {
    companion object {
        const val KOROAD_URL = "https://www.juso.go.kr"
        // 서울역을 기본 좌표값으로 설정
        const val LATITUDE_DEFAULT = 37.5076415
        const val LONGITUDE_DEFAULT = 127.0556521
        // 며칠 전 메뉴까지 고려해서 추천할 것인지
        const val INTERVAL_DATE = 3
        // 제한 거리 기본 값
        const val DEFAULT_USER_RADIUS = 600
    }
}