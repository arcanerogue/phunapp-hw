package com.glopez.phunapp.view.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.glopez.phunapp.model.Event
import com.glopez.phunapp.model.EventRepository

/**
 * The ViewModel for the EventDetailActivity which displays the details of
 * a single Event.
 * @param[eventRepo] The application's repository instance.
 */
class EventDetailViewModel(private val eventRepo: EventRepository) : ViewModel() {
    /**
     * Retrieves the Event, wrapped in a LiveData object, from the repository.
     * @param[id] The value of the id property of the requested Event.
     * @return The requested Event object.
     */
    fun getEvent(id: Int): LiveData<Event> {
        return eventRepo.getSingleEvent(id)
    }
}