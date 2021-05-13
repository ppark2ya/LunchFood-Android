package com.lunchfood.data.repository

import com.lunchfood.data.api.ApiHelper
import com.lunchfood.data.model.*
import com.lunchfood.data.model.filter.FilterCommonRequest
import com.lunchfood.data.model.history.DayMenuDeleteParam
import com.lunchfood.data.model.history.DayMenuInsertParam
import com.lunchfood.data.model.history.HistoryParam
import com.lunchfood.data.model.history.HistoryRequest

class MainRepository(private val apiHelper: ApiHelper) {

    suspend fun insertAccount(data: User) = apiHelper.insertAccount(data)
    suspend fun getAccount(data: User) = apiHelper.getAccount(data)
    suspend fun updateLocation(data: User) = apiHelper.updateLocation(data)
    suspend fun getAddressList(addressParam: HashMap<String, Any>) = apiHelper.getAddressList(addressParam)
    suspend fun getAddressCoord(addressParam: HashMap<String, Any>) = apiHelper.getAddressCoord(addressParam)
    suspend fun getBestMenuList(data: BestMenuRequest) = apiHelper.getBestMenuList(data)
    suspend fun insertHistory(data: HistoryRequest) = apiHelper.insertHistory(data)
    suspend fun getPlaceHistory(data: HistoryParam) = apiHelper.getPlaceHistory(data)
    suspend fun checkToday(id: Long) = apiHelper.checkToday(id)
    suspend fun getPlaceAuto(data: CommonParam) = apiHelper.getPlaceAuto(data)
    suspend fun getFoodAuto(data: CommonParam) = apiHelper.getFoodAuto(data)
    suspend fun insertDayMenu(data: DayMenuInsertParam) = apiHelper.insertDayMenu(data)
    suspend fun deleteDayMenu(data: DayMenuDeleteParam) = apiHelper.deleteDayMenu(data)
    suspend fun insertSelectedPlace(data: FilterCommonRequest) = apiHelper.insertSelectedPlace(data)
    suspend fun updateFilter(data: FilterCommonRequest) = apiHelper.updateFilter(data)
    suspend fun getSelectedPlace(data: FilterCommonRequest) = apiHelper.getSelectedPlace(data)
    suspend fun deleteSelectedPlace(data: FilterCommonRequest) = apiHelper.deleteSelectedPlace(data)
}