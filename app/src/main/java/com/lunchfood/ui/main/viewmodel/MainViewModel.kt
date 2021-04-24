package com.lunchfood.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.lunchfood.data.model.*
import com.lunchfood.data.model.history.HistoryParam
import com.lunchfood.data.model.history.HistoryRequest
import com.lunchfood.data.repository.MainRepository
import com.lunchfood.utils.Resource
import kotlinx.coroutines.Dispatchers

class MainViewModel(private val mainRepository: MainRepository) : ViewModel() {

    fun insertAccount(data: User) = liveData(Dispatchers.IO) {
        emit(Resource.pending(data = null))
        try {
            emit(Resource.success(data = mainRepository.insertAccount(data)))
        } catch (exception: Exception) {
            emit(Resource.failure(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getAccount(data: User) = liveData(Dispatchers.IO) {
        emit(Resource.pending(data = null))
        try {
            emit(Resource.success(data = mainRepository.getAccount(data)))
        } catch (exception: Exception) {
            emit(Resource.failure(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun updateLocation(data: User) = liveData(Dispatchers.IO) {
        emit(Resource.pending(data = null))
        try {
            emit(Resource.success(data = mainRepository.updateLocation(data)))
        } catch (exception: Exception) {
            emit(Resource.failure(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getAddressList(addressParam: HashMap<String, Any>) = liveData(Dispatchers.IO) {
        emit(Resource.pending(data = null))
        try {
            emit(Resource.success(data = mainRepository.getAddressList(addressParam)))
        } catch (exception: Exception) {
            emit(Resource.failure(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getAddressCoord(addressParam: HashMap<String, Any>) = liveData(Dispatchers.IO) {
        emit(Resource.pending(data = null))
        try {
            emit(Resource.success(data = mainRepository.getAddressCoord(addressParam)))
        } catch (exception: Exception) {
            emit(Resource.failure(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getBestMenuList(data: BestMenuRequest) = liveData(Dispatchers.IO) {
        emit(Resource.pending(data = null))
        try {
            emit(Resource.success(data = mainRepository.getBestMenuList(data)))
        } catch (exception: Exception) {
            emit(Resource.failure(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun insertHistory(data: HistoryRequest) = liveData(Dispatchers.IO) {
        emit(Resource.pending(data = null))
        try {
            emit(Resource.success(data = mainRepository.insertHistory(data)))
        } catch (exception: Exception) {
            emit(Resource.failure(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getPlaceHistory(data: HistoryParam) = liveData(Dispatchers.IO) {
        emit(Resource.pending(data = null))
        try {
            emit(Resource.success(data = mainRepository.getPlaceHistory(data)))
        } catch (exception: Exception) {
            emit(Resource.failure(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getPlaceAuto(data: CommonParam) = liveData(Dispatchers.IO) {
        emit(Resource.pending(data = null))
        try {
            emit(Resource.success(data = mainRepository.getPlaceAuto(data)))
        } catch (exception: Exception) {
            emit(Resource.failure(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getFoodAuto(data: CommonParam) = liveData(Dispatchers.IO) {
        emit(Resource.pending(data = null))
        try {
            emit(Resource.success(data = mainRepository.getFoodAuto(data)))
        } catch (exception: Exception) {
            emit(Resource.failure(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}