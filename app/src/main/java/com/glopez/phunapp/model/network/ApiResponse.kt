package com.glopez.phunapp.model.network

import com.glopez.phunapp.constants.HTTP_DEFAULT_ERROR_CODE
import com.glopez.phunapp.constants.HTTP_NO_CONTENT
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
                if(responseBody == null || responseCode == HTTP_NO_CONTENT)
                    EmptyBody(responseCode)
                else
                    Success(responseBody)
            } else {
                Error(errorBody.toString(), responseCode)
            }
        }
    }

    data class Success<T> (val body: T): ApiResponse<T> ()
    data class Error<T>(val errorMessage: String?, val responseCode: Int = HTTP_DEFAULT_ERROR_CODE) : ApiResponse<T> ()
    data class EmptyBody<T>(val responseCode: Int) : ApiResponse<T>()
    data class Loading<T> (val body: T) : ApiResponse<T>()
}