package com.glopez.phunapp.model.repository

import androidx.lifecycle.LiveData
import com.glopez.phunapp.model.StarWarsEvent
import com.glopez.phunapp.model.db.Resource
import com.glopez.phunapp.model.network.ApiResponse

interface FeedRepository {
    fun getEvents(): LiveData<Resource<List<StarWarsEvent>>>
    fun getEventsFromDatabase(): LiveData<List<StarWarsEvent>>
    fun getSingleEventFromDatabase(id: Int): LiveData<StarWarsEvent>
    fun getApiResponseState(): LiveData<ApiResponse<List<StarWarsEvent>>>
    fun updateEventsFromNetwork()
    fun getEventsFromNetwork()
    fun insertEventsIntoDatabase(starWarsEvents: List<StarWarsEvent>)
    fun clearDisposables()
}