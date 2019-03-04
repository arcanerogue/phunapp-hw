package com.glopez.phunapp.data

import retrofit2.Call
import retrofit2.http.GET

interface GetEventFeed {
    @GET("/phunware-services/dev-interview-homework/master/feed.json")
    fun getEvents(): Call<List<Event>>
}