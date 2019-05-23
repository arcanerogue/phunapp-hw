package com.glopez.phunapp

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.glopez.phunapp.model.EventRepository
import com.glopez.phunapp.view.viewmodels.EventDetailViewModel
import com.glopez.phunapp.view.viewmodels.EventViewModel
import java.lang.IllegalArgumentException

/**
 * A utility class that creates ViewModels, passing in their dependency as a
 * constructor parameter.
 * @param[eventRepository] The application's repository instance.
 */
class ViewModelFactory private constructor(private val eventRepository: EventRepository
) : ViewModelProvider.NewInstanceFactory() {

    /**
     * Creates a new instance of the given class.
     * @param[modelClass] the class of the instance requested.
     * @return the ViewModel instance of the requested ViewModel class.
     */
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

        /**
         * Gets or creates the ViewModelFactory Singleton instance.
         * @param[application] The application object.
         * @return The ViewModelFactory instance.
         */
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