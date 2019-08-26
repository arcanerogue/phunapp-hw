package com.glopez.phunapp.view.viewmodels

import android.arch.lifecycle.*
import android.support.annotation.VisibleForTesting
import com.glopez.phunapp.model.Event
import com.glopez.phunapp.model.db.Resource
import com.glopez.phunapp.model.network.ApiResponse
import com.glopez.phunapp.model.repository.FeedRepository
import java.lang.Exception

/**
 * [ViewModel] for the MainActivity w$4hich displays the list of Event objects.
 * @param[eventFeedRepo] The application's repository instance.
 */
//class FeedViewModel(private val eventFeedRepo: EventFeedRepository) : ViewModel() {
class FeedViewModel(private val eventFeedRepo: FeedRepository) : ViewModel() {
    private val eventsResourceStatus: MediatorLiveData<Resource<List<Event>>> = MediatorLiveData()
    private val apiResponseStatus: LiveData<ApiResponse<List<Event>>> = eventFeedRepo.getApiResponseState()
    private val dbSource = eventFeedRepo.getEventsFromDatabase()

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
        eventFeedRepo.updateEventsFromNetwork()
        setEventsResourceStatus()
    }

    /**
     * Listens for changes to the ApiResponse state from the EventFeedRepository and then pulls the latest
     * data from the database. When the database is empty and events can't be fetched from the network,
     * the Resource error state is set to inform the view.
     */
    private fun setEventsResourceStatus() {
        eventsResourceStatus.value = Resource.Loading(emptyList())
        val updatedEventsFromDatabase = eventFeedRepo.getEventsFromDatabase()
        eventsResourceStatus.addSource(apiResponseStatus) {
            when (it) {
                is ApiResponse.Success<List<Event>> -> {
                    eventsResourceStatus.addSource(updatedEventsFromDatabase) { data ->
                        eventsResourceStatus.value = Resource.Success(data)
                    }
                }
                is ApiResponse.EmptyBody,
                is ApiResponse.Error -> {
                    eventsResourceStatus.addSource(updatedEventsFromDatabase) { data ->
                        if (data.isNullOrEmpty())
                            eventsResourceStatus.value = Resource.Error(Exception("Unable to populate the database with events."))
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
        eventFeedRepo.clearDisposables()
    }
}

