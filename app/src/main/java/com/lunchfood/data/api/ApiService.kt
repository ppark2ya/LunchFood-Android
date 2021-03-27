package com.lunchfood.data.api

import com.lunchfood.data.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("/insert_acc")
    fun insertAccount(@Body data: User): Map<String, Object>

    @GET("/get_acc")
    fun getAccount(@Query("id") id: Long): User
}