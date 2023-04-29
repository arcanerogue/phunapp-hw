package com.glopez.phunapp.model.network

import com.glopez.phunapp.model.StarWarsEvent
import retrofit2.Response
import retrofit2.http.GET

private const val FEED_PATH = "phunware-services/dev-interview-homework/master/feed.json"
interface FeedProvider {
    @GET(FEED_PATH)
    suspend fun getEvents(): Response<List<StarWarsEvent>>
}