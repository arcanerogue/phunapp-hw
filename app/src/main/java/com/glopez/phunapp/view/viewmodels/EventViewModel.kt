package com.glopez.phunapp.view.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.glopez.phunapp.model.Event
import com.glopez.phunapp.model.EventRepository

class EventViewModel(private val eventRepo: EventRepository) : ViewModel() {
    val events: LiveData<List<Event>>
        get() = getEventsList()

    private fun getEventsList(): LiveData<List<Event>> {
        return eventRepo.getEvents()
    }
}
