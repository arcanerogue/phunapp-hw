package com.glopez.phunapp

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.glopez.phunapp.model.EventRepository
import com.glopez.phunapp.view.viewmodels.EventDetailViewModel
import com.glopez.phunapp.view.viewmodels.EventViewModel
import java.lang.IllegalArgumentException

class ViewModelFactory private constructor(private val eventRepository: EventRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(EventViewModel::class.java) ->
                EventViewModel(eventRepository) as T
            modelClass.isAssignableFrom(EventDetailViewModel::class.java) ->
                EventDetailViewModel(eventRepository) as T
            else ->
                throw IllegalArgumentException(
                    "Unknown ViewModel class provided: ${modelClass.name}"
                )
        }
    }
    companion object {
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(application: PhunApp): ViewModelFactory {
            synchronized(this) {
                if (INSTANCE == null) {
                    // Create ViewModelFactory
                    INSTANCE = ViewModelFactory(application.getRepository())
                }
                return INSTANCE as ViewModelFactory
            }
        }
    }
}