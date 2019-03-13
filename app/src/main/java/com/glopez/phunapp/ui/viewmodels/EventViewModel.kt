package com.glopez.phunapp.ui.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.glopez.phunapp.data.Event
import com.glopez.phunapp.data.EventRepository

class EventViewModel(application: Application) : AndroidViewModel(application) {
    private val eventRepo = EventRepository(application)
    val eventFeedList: LiveData<List<Event>>

    init {
        // Get events from repository
        eventFeedList = eventRepo.getEvents()
    }
}