package com.glopez.phunapp.testutil

import com.glopez.phunapp.model.StarWarsEvent
import com.glopez.phunapp.model.network.FeedProvider
import retrofit2.Response

class FeedProviderMock(private val events: List<StarWarsEvent>) : FeedProvider {
    override suspend fun getEvents(): Response<List<StarWarsEvent>> {
        return Response.success(events)
    }

}