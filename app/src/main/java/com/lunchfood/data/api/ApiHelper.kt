package com.lunchfood.data.api

import com.lunchfood.data.api.RetrofitBuilder.koroadService
import com.lunchfood.data.model.*

class ApiHelper(private val apiService: ApiService) {

    suspend fun insertAccount(data: User) = apiService.insertAccount(data)
    suspend fun getAccount(data: User) = apiService.getAccount(data)
    suspend fun updateLocation(data: User) = apiService.updateLocation(data)
    suspend fun getAddressList(addressParam: HashMap<String, Any>) = koroadService.getAddressList(addressParam)
    suspend fun getAddressCoord(addressParam: HashMap<String, Any>) = koroadService.getAddressCoord(addressParam)
}