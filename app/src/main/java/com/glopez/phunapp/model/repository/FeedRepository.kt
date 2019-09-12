package com.glopez.phunapp.model.repository

import android.arch.lifecycle.LiveData
import com.glopez.phunapp.model.StarWarsEvent
import com.glopez.phunapp.model.network.ApiResponse

interface FeedRepository {
    fun getApiResponseState(): LiveData<ApiResponse<List<StarWarsEvent>>>
    fun getEventsFromDatabase(): LiveData<List<StarWarsEvent>>
    fun getEventsFromNetwork()
    fun updateEventsFromNetwork()
    fun insertEventsIntoDatabase(starWarsEvents: List<StarWarsEvent>)
    fun getSingleEventFromDatabase(id: Int): LiveData<StarWarsEvent>
    fun clearDisposables()
}