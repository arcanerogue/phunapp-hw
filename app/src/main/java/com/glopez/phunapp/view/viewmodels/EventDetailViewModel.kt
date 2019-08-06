package com.glopez.phunapp.view.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.glopez.phunapp.constants.DB_MINIMUM_ID_VALUE
import com.glopez.phunapp.model.Event
import com.glopez.phunapp.model.EventRepository
import com.glopez.phunapp.model.db.Resource

/**
 * [ViewModel] for the EventDetailActivity which displays the details of
 * a single Event.
 * @param[eventRepo] The application's repository instance.
 */
class EventDetailViewModel(private val eventRepo: EventRepository) : ViewModel() {

//    private val minValueSetForIdField: Int = 1

    /**
     * Retrieves the Event from the repository and transforms it into a Resource State object
     * to inform the View of the current state.
     * @param[id] The id value of the requested Event.
     * @return The requested Event object wrapped in a Resource State object.
     */
    fun getEventDetailResource(id: Int): LiveData<Resource<Event>> {
        return Transformations.map(eventRepo.getSingleEventFromDatabase(id)) {
            data -> createResource(data)
        }
    }

    private fun createResource(event: Event?): Resource<Event> {
        return if (event == null || event.id < DB_MINIMUM_ID_VALUE )
            Resource.Error(
                NoSuchElementException("Invalid EVENT_ID passed to EventDetailActivity. Event not found in the Room database."))
        else
            Resource.Success(event)
    }
}
