package com.glopez.phunapp.view.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.glopez.phunapp.model.Event
import com.glopez.phunapp.model.EventRepository
import com.glopez.phunapp.model.createEventDateFormatString
import com.glopez.phunapp.model.db.Resource

/**
 * The ViewModel for the EventDetailActivity which displays the details of
 * a single Event.
 * @param[eventRepo] The application's repository instance.
 */
class EventDetailViewModel(private val eventRepo: EventRepository) : ViewModel() {

    private val minValueSetForIdField: Int = 1
    /**
     * Retrieves the Event, wrapped in a LiveData object, from the repository.
     * @param[id] The value of the id property of the requested Event.
     * @return The requested Event object.
     */
    fun getEvent(id: Int): LiveData<Event> {
        return Transformations.map(eventRepo.getSingleEventFromDatabase(id)) {
            data -> updateDateString(data)
        }
//        return eventRepo.getSingleEventFromDatabase(id)
    }

    fun getEventAsResource(id: Int): LiveData<Resource<Event>> {
        return Transformations.map(eventRepo.getSingleEventFromDatabase(id)) {
            data -> createResource(data)
        }
    }

//    var singleEventForActivity: MutableLiveData<Resource<Event>> = MutableLiveData()
//
//    fun getEventAsResource(id: Int): LiveData<Resource<Event>> {
//        val singleEvent = eventRepo.getSingleEventFromDatabase(id)
//
////        singleEventForActivity = Transformations.map(singleEvent) { data ->
////            if (data == null || data.id < minValueSetForIdField) {
////                Resource.Error("error fetching event detail.")
////            } else {
////                Resource.Success(data)
//        return Transformations.map(singleEvent) { data ->
////            Resource.Success(data)
//            if (data != null)
//                Resource.Success(data)
//            else
//                Resource.Success(null)
//        }
//    }

    private fun updateDateString(event: Event): Event {
        val updatedDateString = event.createEventDateFormatString()
        return event.copy(date = updatedDateString)
    }

    private fun createResource(event: Event?): Resource<Event> {
//        val error = NoSuchElementException("Event not found in the Room database.")
        return if (event == null || event.id < minValueSetForIdField )
//            Resource.Error("no event found.")
            Resource.Error(
                NoSuchElementException("Invalid EVENT_ID passed to EventDetailActivity. Event not found in the Room database."))
        else
            Resource.Success(event)
    }
}

//    if (data == null || data.id < minValueSetForIdField)
//    Resource.Error("event not found.")
//    else {
//        if (data.date != null) {
//            data.createEventDateFormatString()
//        }
//        Resource.Success(data)
//    }
