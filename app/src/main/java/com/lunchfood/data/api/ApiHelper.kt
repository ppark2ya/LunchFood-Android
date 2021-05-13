package com.lunchfood.data.api

import com.lunchfood.data.api.RetrofitBuilder.koroadService
import com.lunchfood.data.model.*
import com.lunchfood.data.model.filter.FilterCommonRequest
import com.lunchfood.data.model.history.DayMenuDeleteParam
import com.lunchfood.data.model.history.DayMenuInsertParam
import com.lunchfood.data.model.history.HistoryParam
import com.lunchfood.data.model.history.HistoryRequest

class ApiHelper(private val apiService: ApiService) {

    suspend fun insertAccount(data: User) = apiService.insertAccount(data)
    suspend fun getAccount(data: User) = apiService.getAccount(data)
    suspend fun updateLocation(data: User) = apiService.updateLocation(data)
    suspend fun getAddressList(addressParam: HashMap<String, Any>) = koroadService.getAddressList(addressParam)
    suspend fun getAddressCoord(addressParam: HashMap<String, Any>) = koroadService.getAddressCoord(addressParam)
    suspend fun getBestMenuList(data: BestMenuRequest) = apiService.getBestMenuList(data)
    suspend fun insertHistory(data: HistoryRequest) = apiService.insertHistory(data)
    suspend fun getPlaceHistory(data: HistoryParam) = apiService.getPlaceHistory(data)
    suspend fun checkToday(id: Long) = apiService.checkToday(id)
    suspend fun getPlaceAuto(data: CommonParam) = apiService.getPlaceAuto(data)
    suspend fun getFoodAuto(data: CommonParam) = apiService.getFoodAuto(data)
    suspend fun insertSelectedPlace(data: FilterCommonRequest) = apiService.insertSelectedPlace(data)
    suspend fun insertDayMenu(data: DayMenuInsertParam) = apiService.insertDayMenu(data)
    suspend fun deleteDayMenu(data: DayMenuDeleteParam) = apiService.deleteDayMenu(data)
    suspend fun updateFilter(data: FilterCommonRequest) = apiService.updateFilter(data)
    suspend fun getSelectedPlace(data: FilterCommonRequest) = apiService.getSelectedPlace(data)
    suspend fun deleteSelectedPlace(data: FilterCommonRequest) = apiService.deleteSelectedPlace(data)
}