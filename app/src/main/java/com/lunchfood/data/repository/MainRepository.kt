package com.lunchfood.data.repository

import com.lunchfood.data.api.ApiHelper
import com.lunchfood.data.model.*

class MainRepository(private val apiHelper: ApiHelper) {

    suspend fun insertAccount(data: User) = apiHelper.insertAccount(data)
    suspend fun getAccount(data: User) = apiHelper.getAccount(data)
    suspend fun updateLocation(data: User) = apiHelper.updateLocation(data)
    suspend fun getAddressList(addressParam: HashMap<String, Any>) = apiHelper.getAddressList(addressParam)
    suspend fun getAddressCoord(addressParam: HashMap<String, Any>) = apiHelper.getAddressCoord(addressParam)
    suspend fun getBestMenuList(data: BestMenuRequest) = apiHelper.getBestMenuList(data)
    suspend fun insertHistory(data: HistoryRequest) = apiHelper.insertHistory(data)
    suspend fun getPlaceHistory(data: HistoryParam) = apiHelper.getPlaceHistory(data)
}