package com.lunchfood.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.lunchfood.data.model.User
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

    fun getAccount(id: Long) = liveData(Dispatchers.IO) {
        emit(Resource.pending(data = null))
        try {
            emit(Resource.success(data = mainRepository.getAccount(id)))
        } catch (exception: Exception) {
            emit(Resource.failure(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getAddressList(addressParam: HashMap<String, Object>) = liveData(Dispatchers.IO) {
        emit(Resource.pending(data = null))
        try {
            emit(Resource.success(data = mainRepository.getAddressList(addressParam)))
        } catch (exception: Exception) {
            emit(Resource.failure(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}