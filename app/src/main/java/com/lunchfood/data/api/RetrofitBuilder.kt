package com.lunchfood.data.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.lunchfood.utils.Constants.Companion.BASE_URL
import com.lunchfood.utils.Constants.Companion.KOROAD_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilder {

    val gson: Gson = GsonBuilder()
        .setLenient()
        .create();

    private fun getHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder().addInterceptor(interceptor).build()
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(SynchronousCallAdapterFactory.create())
            .client(getHttpClient())
            .build()
    }

    private fun getKoroadRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(KOROAD_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(SynchronousCallAdapterFactory.create())
            .client(getHttpClient())
            .build()
    }

    val apiService: ApiService = getRetrofit().create(ApiService::class.java)
    val koroadService: ApiService = getKoroadRetrofit().create(ApiService::class.java)
}