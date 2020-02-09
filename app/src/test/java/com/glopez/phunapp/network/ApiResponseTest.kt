package com.glopez.phunapp.network

import com.glopez.phunapp.constants.HTTP_BAD_REQUEST
import com.glopez.phunapp.constants.HTTP_NO_CONTENT
import com.glopez.phunapp.model.network.ApiResponse
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.hamcrest.core.IsInstanceOf.instanceOf
import org.junit.Test
import org.junit.Assert.*
import retrofit2.Response

class ApiResponseTest {
    @Test
    fun `verify api response on error`() {
        val errorMessage = "Api returned an error response"
        val errorResponse = ApiResponse.onFailure<String>(errorMessage)
        assertThat(errorResponse, instanceOf(ApiResponse.Error::class.java))
        assertEquals(errorMessage, errorResponse.message)
    }

    @Test
    fun `verify api response on success`() {
        val successBody = "Api returned success"
        val response = Response.success(successBody)
        val successResponse = ApiResponse.onResponse(response)
        assertThat(successResponse, instanceOf(ApiResponse.Success::class.java))
        assertEquals((successResponse as ApiResponse.Success).body, successBody)
    }

    @Test
    fun `verify api response on empty response body`() {
        val response = Response.success(null)
        val emptyBodyResponse = ApiResponse.onResponse(response)
        assertThat(emptyBodyResponse, instanceOf(ApiResponse.EmptyBody::class.java))
    }

    @Test
    fun `verify api response on 204 response code`() {
        val responseBody = ""
        val response = Response.success(HTTP_NO_CONTENT, responseBody)
        val emptyBodyResponse = ApiResponse.onResponse(response)
        assertThat(emptyBodyResponse, instanceOf(ApiResponse.EmptyBody::class.java))
    }

    @Test
    fun `very onResponse on error response`() {
        val errorMessage = "Api returned an error response"
        val response = Response
            .error<String>(HTTP_BAD_REQUEST,
                ResponseBody.create(MediaType.get("application/txt"), errorMessage))
        val errorResponse = ApiResponse.onResponse(response)
        assertThat(errorResponse, instanceOf(ApiResponse.Error::class.java))
    }
}