package com.glopez.phunapp.model.network

import com.glopez.phunapp.model.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://raw.githubusercontent.com/"
private const val FEED_PATH = "phunware-services/dev-interview-homework/master/feed.json"

class EventFeedProvider {
    private val service: FeedProvider

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(FeedProvider::class.java)
    }

    fun getEventFeed(callback: Callback<List<Event>>) {
        val call: Call<List<Event>> = service.getEvents(FEED_PATH)
        call.enqueue(callback)
    }
}