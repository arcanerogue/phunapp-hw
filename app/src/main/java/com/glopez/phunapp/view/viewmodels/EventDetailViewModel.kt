package com.glopez.phunapp.view.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.glopez.phunapp.PhunApp
import com.glopez.phunapp.model.Event
import com.glopez.phunapp.model.EventRepository

class EventDetailViewModel(application: Application) : AndroidViewModel(application)  {

    private val phunApp = application as PhunApp
    private val eventRepo: EventRepository = phunApp.getRepository()

    fun getEvent(id: Int): LiveData<Event> {
        return eventRepo.getSingleEvent(id)
    }
}