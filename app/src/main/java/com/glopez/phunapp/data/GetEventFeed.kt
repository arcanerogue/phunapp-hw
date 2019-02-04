package com.glopez.phunapp.data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface GetEventFeed {
    @GET("/phunware-services/dev-interview-homework/master/feed.json")
    fun getEvents(): Call<List<Event>>
}