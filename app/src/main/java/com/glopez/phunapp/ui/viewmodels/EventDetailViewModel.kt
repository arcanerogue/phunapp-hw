package com.glopez.phunapp.ui.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.glopez.phunapp.PhunApp
import com.glopez.phunapp.data.Event

class EventDetailViewModel(application: Application) : AndroidViewModel(application)  {

    private val phunApp = application as PhunApp
    private val eventRepo = phunApp.getRepository()

    fun getEvent(id: Int): LiveData<Event> {
        return eventRepo.getSingleEvent(id)
    }
}