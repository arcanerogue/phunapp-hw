package com.glopez.phunapp.data

import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EventFeedRetriever{
    private val service: GetEventFeed

    companion object {
        const val BASE_URL = "https://raw.githubusercontent.com/"
    }

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(GetEventFeed::class.java)
    }

    fun getEventFeed(callback: Callback<List<Event>>){
        val call = service.getEvents()
        call.enqueue(callback)
    }
}