package com.glopez.phunapp.model.network

import com.glopez.phunapp.model.StarWarsEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://raw.githubusercontent.com/"
private const val FEED_PATH = "phunware-services/dev-interview-homework/master/feed.json"

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

    suspend fun getEventFeed(): Response<List<StarWarsEvent>> = withContext(Dispatchers.Default) {
        service.getEvents(FEED_PATH)
    }
}