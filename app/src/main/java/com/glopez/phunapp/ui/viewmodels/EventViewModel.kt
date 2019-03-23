package com.glopez.phunapp.ui.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.glopez.phunapp.PhunApp
import com.glopez.phunapp.data.Event
import com.glopez.phunapp.data.EventRepository

class EventViewModel(application: Application) : AndroidViewModel(application) {

    private val phunApp = application as PhunApp
    private val eventRepo: EventRepository = phunApp.getRepository()
    val eventFeedList: LiveData<List<Event>>

    init {
        eventFeedList = eventRepo.getEvents()
    }
}