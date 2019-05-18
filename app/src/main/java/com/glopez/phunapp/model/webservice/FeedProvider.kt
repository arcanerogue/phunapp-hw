package com.glopez.phunapp.model.webservice

import com.glopez.phunapp.model.Event
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface FeedProvider {
    @GET
    fun getEvents(@Url url: String): Call<List<Event>>
}