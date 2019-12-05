package com.glopez.phunapp.view.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.glopez.phunapp.constants.DB_MINIMUM_ID_VALUE
import com.glopez.phunapp.model.StarWarsEvent
import com.glopez.phunapp.model.db.Resource
import com.glopez.phunapp.model.repository.FeedRepository

/**
 * [ViewModel] for the EventDetailActivity which displays the details of
 * a single StarWarsEvent.
 * @param[eventRepo] The application's repository instance.
 */
class DetailViewModel(private val eventRepo: FeedRepository) : ViewModel() {
    private var eventDetailResource = MutableLiveData<Resource<StarWarsEvent>>()
    val eventDetail: LiveData<Resource<StarWarsEvent>>
        get() = eventDetailResource

    init {
        eventDetailResource.value = Resource.Loading()
    }

    /**
     * Retrieves the StarWarsEvent from the repository and transforms it into a Resource State object
     * to inform the View of the current state.
     * @param[id] The id value of the requested StarWarsEvent.
     * @return The requested StarWarsEvent object wrapped in a Resource State object.
     */
    fun getEventById(id: Int): LiveData<Resource<StarWarsEvent>> {
        eventDetailResource = Transformations.map(eventRepo.getSingleEventFromDatabase(id)) { data ->
            mapToResource(data)
        } as MutableLiveData<Resource<StarWarsEvent>>
        return eventDetailResource
    }

    private fun mapToResource(starWarsEvent: StarWarsEvent?): Resource<StarWarsEvent> {
        return if (starWarsEvent == null || starWarsEvent.id < DB_MINIMUM_ID_VALUE )
            Resource.Error(
                NoSuchElementException("Invalid EVENT_ID passed to EventDetailActivity. StarWarsEvent not found in the Room database.")
            )
        else
            Resource.Success(starWarsEvent)
    }
}
