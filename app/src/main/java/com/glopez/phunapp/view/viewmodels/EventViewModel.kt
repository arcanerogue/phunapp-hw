package com.glopez.phunapp.view.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.glopez.phunapp.model.Event
import com.glopez.phunapp.model.EventRepository

/**
 * The ViewModel for the MainActivity which displays the list of Event objects.
 * @param[eventRepo] The application's repository instance.
 */
class EventViewModel(private val eventRepo: EventRepository) : ViewModel() {

    private val eventsList: LiveData<List<Event>>

    init {
        eventsList = setEventsList()
    }

    /**
     * Retrieves the list of Events, wrapped in a LiveData object, from the
     * repository.
     * @return The list of Events wrapped in LiveData.
     */
    fun getEventsList(): LiveData<List<Event>> {
        return eventsList
    }

    private fun setEventsList(): LiveData<List<Event>> {
        return eventRepo.getEvents()
    }
}
