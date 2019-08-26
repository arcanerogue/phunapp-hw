package com.glopez.phunapp.model.repository

import android.arch.lifecycle.LiveData
import com.glopez.phunapp.model.Event
import com.glopez.phunapp.model.network.ApiResponse

interface FeedRepository {
    fun getApiResponseState(): LiveData<ApiResponse<List<Event>>>
    fun getEventsFromDatabase(): LiveData<List<Event>>
    fun getEventsFromNetwork()
    fun updateEventsFromNetwork()
    fun insertEventsIntoDatabase(events: List<Event>)
    fun getSingleEventFromDatabase(id: Int): LiveData<Event>
    fun clearDisposables()
}