package com.glopez.phunapp.model.network

sealed class ApiResponse {
    data class Success<T> (val body: T): ApiResponse ()
    data class Error(val error: Throwable) : ApiResponse ()
    data class ResponseError<T> (val responseCode: String, val errorBody: T): ApiResponse()
    data class ResponseEmptyBody(val responseCode: String) : ApiResponse()
    data class Loading<T> (val body: T) : ApiResponse()
}