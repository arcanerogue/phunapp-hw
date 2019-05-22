package com.glopez.phunapp.view.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.glopez.phunapp.PhunApp
import com.glopez.phunapp.model.Event
import com.glopez.phunapp.model.EventRepository

class EventDetailViewModel(private val eventRepo: EventRepository) : ViewModel() {
    fun getEvent(id: Int): LiveData<Event> {
        return eventRepo.getSingleEvent(id)
    }
}