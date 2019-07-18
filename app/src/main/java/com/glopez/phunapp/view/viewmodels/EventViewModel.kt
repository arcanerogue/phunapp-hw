package com.glopez.phunapp.view.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.glopez.phunapp.model.Event
import com.glopez.phunapp.model.EventRepository
import com.glopez.phunapp.model.db.Resource
import com.glopez.phunapp.model.network.ApiResponse

/**
 * The ViewModel for the MainActivity which displays the list of Event objects.
 * @param[eventRepo] The application's repository instance.
 */
class EventViewModel(private val eventRepo: EventRepository) : ViewModel() {

//    private val eventsList: LiveData<List<Event>>
//    private val eventsList: LiveData<Resource<List<Event>>>
    val apiResponseStatus: LiveData<ApiResponse<List<Event>>>
        get() = eventRepo.getApiResponseState()

    val dbResourceStatus: LiveData<Resource<List<Event>>>
            get() = eventRepo.getDbResultState()

    init {
//        eventsList = setEventsList()
    }

    /**
     * Provides the list of Events, wrapped in a LiveData object.
     * @return The list of Events wrapped in LiveData.
     */
//    fun getEventsList(): LiveData<List<Event>> {
//    fun getEventsList(): LiveData<Resource<List<Event>>> {
//        return eventsList
//    }

    /**
     * Retrieves the list of Events, wrapped in a LiveData object, from the
     * repository.
     * @return The list of Events wrapped in LiveData.
     */
//    private fun setEventsList(): LiveData<List<Event>> {
//    private fun setEventsList(): LiveData<Resource<List<Event>>> {
//        return eventRepo.getEventsFromDatabase()
//    }

//    fun updateEventsFromNetwork() {
//        eventRepo.getEventsFromNetwork()
//    }
}
