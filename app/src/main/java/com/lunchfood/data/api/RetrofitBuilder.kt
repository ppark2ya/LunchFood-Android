package com.lunchfood.data.api

import com.lunchfood.BuildConfig.BASE_URL
import com.lunchfood.utils.Constants.Companion.KOROAD_URL
import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


object RetrofitBuilder {

    private val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    private fun getHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder().addInterceptor(interceptor).build()
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(getHttpClient())
            .build()
    }

    private fun getKoroadRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(KOROAD_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(getHttpClient())
            .build()
    }

    val apiService: ApiService by lazy { getRetrofit().create(ApiService::class.java) }
    val koroadService: ApiService by lazy { getKoroadRetrofit().create(ApiService::class.java) }
}