package com.glopez.phunapp.model.db

sealed class Resource<out T> {
    class Loading<out T> (val data: T? = null) : Resource<T>()
    data class Success<out T> (val data: T?) : Resource<T>()
    data class Error<out T>(val error: Throwable) : Resource<T>()
//    data class Error<out T>(val errorMessage: String?) : Resource<T>()
}