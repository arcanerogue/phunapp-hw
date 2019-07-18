package com.glopez.phunapp.model.network

import retrofit2.Response

sealed class ApiResponse<T> {
    companion object {
        fun <T> onFailure(error: Throwable) : Error<T> {
            return Error(error)
        }

        fun <T> onResponse(response: Response<T>) : ApiResponse<T> {
            val responseBody = response.body()
            val responseCode = response.code()
            val errorBody = response.errorBody()

            return if (response.isSuccessful) {
                if(responseBody == null || responseCode == 204)
                    EmptyBody(responseCode)
                else
                    Success(responseBody)
            } else {
                ResponseError(responseCode, errorBody.toString())
            }
        }
    }

    data class Success<T> (val body: T): ApiResponse<T> ()
    data class Error<T>(val error: Throwable) : ApiResponse<T> ()
    data class ResponseError<T> (val responseCode: Int, val errorBody: String): ApiResponse<T>()
    data class EmptyBody<T>(val responseCode: Int) : ApiResponse<T>()
    data class Loading<T> (val body: T) : ApiResponse<T>()
}