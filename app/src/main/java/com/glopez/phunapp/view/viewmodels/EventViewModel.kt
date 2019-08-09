package com.glopez.phunapp.view.viewmodels

import android.arch.lifecycle.*
import com.glopez.phunapp.model.Event
import com.glopez.phunapp.model.EventRepository
import com.glopez.phunapp.model.db.Resource
import com.glopez.phunapp.model.network.ApiResponse
import java.lang.Exception

/**
 * [ViewModel] for the MainActivity which displays the list of Event objects.
 * @param[eventRepo] The application's repository instance.
 */
class EventViewModel(private val eventRepo: EventRepository) : ViewModel() {

    private val eventsResourceStatus: MediatorLiveData<Resource<List<Event>>> = MediatorLiveData()
    private val apiResponseStatus: LiveData<ApiResponse<List<Event>>> = eventRepo.getApiResponseState()
    private val dbSource = eventRepo.getEventsFromDatabase()

    init {
        eventsResourceStatus.addSource(dbSource) { data ->
            if (data.isNullOrEmpty()) {
                eventsResourceStatus.value = Resource.Loading(emptyList())
            } else {
                eventsResourceStatus.value = Resource.Success(data)
            }
        }
    }

    fun getEventsResource(): LiveData<Resource<List<Event>>> {
        return eventsResourceStatus
    }

    fun refreshEvents() {
        eventRepo.updateEventsFromNetwork()
        setEventsResourceStatus()
    }

    /**
     * Listens for changes to the ApiResponse state from the EventRepository and then pulls the latest
     * data from the database. When the database is empty and events can't be fetched from the network,
     * the Resource error state is set to inform the view.
     */
    private fun setEventsResourceStatus() {
        eventsResourceStatus.value = Resource.Loading(emptyList())
        val updatedEventsFromDatabase = eventRepo.getEventsFromDatabase()
        eventsResourceStatus.addSource(apiResponseStatus) {
            when (it) {
                is ApiResponse.Success<List<Event>>,
                is ApiResponse.EmptyBody<List<Event>> -> {
                    eventsResourceStatus.addSource(updatedEventsFromDatabase) { data ->
                        eventsResourceStatus.value = Resource.Success(data)
                    }
                }
                is ApiResponse.Error -> {
                    eventsResourceStatus.addSource(updatedEventsFromDatabase) { data ->
                        if (data.isNullOrEmpty())
                            eventsResourceStatus.value = Resource.Error(Exception(it.errorMessage))
                        else
                            eventsResourceStatus.value = Resource.Success(data)
                    }
                }
            }
        }
    }

    /**
     * Removes all LiveData sources and clears database insert disposables
     */
    fun removeSources() {
        eventsResourceStatus.removeSource(dbSource)
        eventsResourceStatus.removeSource(apiResponseStatus)
        eventRepo.clearDisposables()
    }
}

