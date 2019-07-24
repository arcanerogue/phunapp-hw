package com.glopez.phunapp.model.network

import retrofit2.Response

sealed class ApiResponse<T> {
    companion object {
        fun <T> onFailure(errorMessage: String?) : Error<T> {
            return Error(errorMessage)
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
                Error(errorBody.toString(), responseCode)
            }
        }
    }

    data class Success<T> (val body: T): ApiResponse<T> ()
    data class Error<T>(val errorMessage: String?, val responseCode: Int = 0) : ApiResponse<T> ()
    data class EmptyBody<T>(val responseCode: Int) : ApiResponse<T>()
    data class Loading<T> (val body: T) : ApiResponse<T>()
}