package com.glopez.phunapp.model.network

import retrofit2.Response

sealed class ApiResponse<T> {
    companion object {
        // Network exception occurred talking to the server or an unexpected exception
        // occurred creating the request or processing the response
        fun <T> onFailure(errorMessage: String?) : Error<T> {
            return Error(errorMessage)
        }

        fun <T> onResponse(response: Response<T>) : ApiResponse<T> {
            val responseBody = response.body()
            val responseCode = response.code()
            val errorBody = response.errorBody()

            return if (response.isSuccessful) {
                if(responseBody == null || responseCode == 204) {
                    EmptyBody("Success Response with Empty Body", responseCode)
                } else {
                    Success(responseBody)
                }
            } else {
                Error(errorBody.toString(), responseCode)
            }
        }
    }

    // HTTP Response Code is in the 200-300 range.
    data class Success<T> (val body: T): ApiResponse<T>()

    // HTTP Response Code is in the 300's, 400's, 500's, or application-level failure.
    data class Error<T>(val message: String?, val responseCode: Int = 0) : ApiResponse<T> ()

    // Received Response with empty body.
    data class EmptyBody<T>(val message: String?, val responseCode: Int) : ApiResponse<T>()
    data class Loading<T> (val body: T) : ApiResponse<T>()
}