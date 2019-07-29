package com.glopez.phunapp.model.db

sealed class Resource<T> {
    class Loading<T> (val data: T? = null) : Resource<T>()
    data class Success<T> (val data: T?) : Resource<T>()
//    data class Error<T>(val error: Throwable) : Resource<T>()
    data class Error<T>(val errorMessage: String?) : Resource<T>()
}