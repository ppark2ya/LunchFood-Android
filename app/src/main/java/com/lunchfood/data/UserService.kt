package com.lunchfood.data

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface UserService {
    @POST("/insert_acc")
    fun InsertAccount(@Body data: Map<String, Object>): Map<String, Object>

    @GET("/get_acc")
    fun GetAccount(@Query("id") id: Int): Map<String, Object>
}