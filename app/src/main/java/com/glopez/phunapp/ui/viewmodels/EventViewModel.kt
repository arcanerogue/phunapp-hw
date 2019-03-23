package com.glopez.phunapp.ui.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.glopez.phunapp.PhunApp
import com.glopez.phunapp.data.Event

class EventViewModel(application: Application) : AndroidViewModel(application) {

    private val phunApp = application as PhunApp
    private val eventRepo = phunApp.getRepository()
    val eventFeedList: LiveData<List<Event>>

    init {
        eventFeedList = eventRepo.getEvents()
    }
}