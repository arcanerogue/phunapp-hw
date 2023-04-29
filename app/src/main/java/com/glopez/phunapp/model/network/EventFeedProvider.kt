package com.glopez.phunapp.model.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://raw.githubusercontent.com/"

class EventFeedProvider {
    private val service: FeedProvider

    init {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .build()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(FeedProvider::class.java)
    }

    fun getService(): FeedProvider = service
}