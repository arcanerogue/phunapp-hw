package com.glopez.phunapp.view.viewmodels

import android.arch.lifecycle.*
import com.glopez.phunapp.model.Event
import com.glopez.phunapp.model.EventRepository
import com.glopez.phunapp.model.db.Resource
import com.glopez.phunapp.model.network.ApiResponse
import java.lang.Exception

/**
 * The ViewModel for the MainActivity which displays the list of Event objects.
 * @param[eventRepo] The application's repository instance.
 */
class EventViewModel(private val eventRepo: EventRepository) : ViewModel() {

    private val eventsResourceStatus: MediatorLiveData<Resource<List<Event>>> = MediatorLiveData()
    private val apiResponseStatus: LiveData<ApiResponse<List<Event>>> = eventRepo.getApiResponseState()
    private val dbSource = eventRepo.getEventsFromDatabase()

    init {
        eventsResourceStatus.addSource(dbSource) { data ->
            if (data.isNullOrEmpty()) {
                eventsResourceStatus.removeSource(dbSource)
                eventsResourceStatus.value = Resource.Loading(emptyList())
            } else {
                eventsResourceStatus.removeSource(dbSource)
                eventsResourceStatus.value = Resource.Success(data)
            }
        }
    }

    fun getEventsResourceStatus(): LiveData<Resource<List<Event>>> {
        return eventsResourceStatus
    }

    fun refreshEvents() {
        eventRepo.updateEventsFromNetwork()
        updateEventsResourceStatus()
    }

    private fun updateEventsResourceStatus() {
        eventsResourceStatus.value = Resource.Loading(emptyList())
        val updatedEventsFromDatabase = eventRepo.getEventsFromDatabase()
        eventsResourceStatus.addSource(apiResponseStatus) {
            when (it) {
                is ApiResponse.Success<List<Event>>,
                is ApiResponse.EmptyBody<List<Event>> -> {
                    eventsResourceStatus.addSource(updatedEventsFromDatabase) { data ->
                        eventsResourceStatus.removeSource(apiResponseStatus)
                        eventsResourceStatus.value = Resource.Success(data)
                    }
                }
                is ApiResponse.Error -> {
                    eventsResourceStatus.addSource(updatedEventsFromDatabase) { data ->
                        eventsResourceStatus.removeSource(apiResponseStatus)
                        if (data.isNullOrEmpty())
                            eventsResourceStatus.value = Resource.Error(Exception(it.errorMessage))
                        else
                            eventsResourceStatus.value = Resource.Success(data)
                    }
                }
            }
        }
    }

    // Covers the case of a user rotating the device as the Main Activity is fetching events
    fun removeSources() {
        eventsResourceStatus.removeSource(dbSource)
        eventsResourceStatus.removeSource(apiResponseStatus)
        eventRepo.clearDisposables()
    }


//    override fun onCleared() {
//        super.onCleared()
//        eventRepo.clearDisposables()
//    }

//    fun clearDisposables() {
//        eventRepo.clearDisposables()
//    }
}

    /**
     * Retrieves the list of Events, wrapped in a LiveData object, from the
     * repository.
     * @return The list of Events wrapped in LiveData.
     */
//    private fun setEventsList(): LiveData<List<Event>> {
//    private fun setEventsList(): LiveData<Resource<List<Event>>> {
//        return eventRepo.getEventsFromDatabase()
//    }

    /**
     * Provides the list of Events, wrapped in a LiveData object.
     * @return The list of Events wrapped in LiveData.
     */
//    fun getEventsList(): LiveData<List<Event>> {
//    fun getEventsList(): LiveData<Resource<List<Event>>> {
//        return eventsList
//    }

