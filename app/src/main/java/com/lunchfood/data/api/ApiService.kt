package com.lunchfood.data.api

import com.lunchfood.data.model.AddressResponse
import com.lunchfood.data.model.User
import retrofit2.http.*

interface ApiService {
    @POST("/insert_acc")
    suspend fun insertAccount(@Body data: User): Map<String, Object>

    @GET("/get_acc")
    suspend fun getAccount(@Query("id") id: Long): User

    @FormUrlEncoded
    @POST("/addrlink/addrLinkApi.do")
    suspend fun getAddressList(@FieldMap addressParam: HashMap<String, Object>): HashMap<String, Object>
}