package com.glopez.phunapp.view.viewmodels

import android.arch.lifecycle.*
import com.glopez.phunapp.model.StarWarsEvent
import com.glopez.phunapp.model.db.Resource
import com.glopez.phunapp.model.repository.FeedRepository

/**
 * [ViewModel] for the MainActivity which displays the list of StarWarsEvent objects.
 * @param[eventFeedRepo] The application's repository instance.
 */
class FeedViewModel(private val eventFeedRepo: FeedRepository) : ViewModel() {

    fun getEventsResource(): LiveData<Resource<List<StarWarsEvent>>> {
        return eventFeedRepo.getEvents()
    }

    fun refreshEvents() {
        eventFeedRepo.updateEventsFromNetwork()
    }

    /**
     * Clears database insert disposables
     */
    override fun onCleared() {
        super.onCleared()
        eventFeedRepo.clearDisposables()
    }
}

