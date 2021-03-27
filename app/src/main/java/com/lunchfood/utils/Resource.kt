package com.lunchfood.utils

data class Resource<out T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T): Resource<T> = Resource(status = Status.SUCCESS, data = data, message = null)
        fun <T> failure(data: T, message: String): Resource<T> = Resource(status = Status.FAILURE, data = data, message = message)
        fun <T> pending(data: T): Resource<T> = Resource(status = Status.PENDING, data = data, message = null)
    }
}
