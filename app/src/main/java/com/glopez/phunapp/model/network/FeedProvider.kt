package com.glopez.phunapp.model.network

import com.glopez.phunapp.model.StarWarsEvent
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface FeedProvider {
    @GET
    suspend fun getEvents(@Url url: String): Response<List<StarWarsEvent>>
}