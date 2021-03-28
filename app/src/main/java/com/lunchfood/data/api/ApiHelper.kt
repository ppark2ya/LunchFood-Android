package com.lunchfood.data.api

import com.lunchfood.data.api.RetrofitBuilder.koroadService
import com.lunchfood.data.model.User

class ApiHelper(private val apiService: ApiService) {

    suspend fun insertAccount(data: User) = apiService.insertAccount(data)
    suspend fun getAccount(id: Long) = apiService.getAccount(id)
    suspend fun getAddressList(addressParam: HashMap<String, Object>) = koroadService.getAddressList(addressParam)
}