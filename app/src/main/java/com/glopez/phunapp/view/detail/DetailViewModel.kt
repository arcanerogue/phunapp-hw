package com.glopez.phunapp.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.glopez.phunapp.constants.DB_MINIMUM_ID_VALUE
import com.glopez.phunapp.model.StarWarsEvent
import com.glopez.phunapp.model.db.Resource
import com.glopez.phunapp.model.repository.FeedRepository
import com.glopez.phunapp.view.StarWarsUiEvent

/**
 * [ViewModel] for the EventDetailActivity which displays the details of
 * a single StarWarsEvent.
 * @param[eventRepo] The application's repository instance.
 */
class DetailViewModel(private val eventRepo: FeedRepository) : ViewModel() {
    private var eventDetailResource = MutableLiveData<Resource<StarWarsUiEvent>>()
    val eventDetail: LiveData<Resource<StarWarsUiEvent>>
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
    fun getEventById(id: Int): LiveData<Resource<StarWarsUiEvent>> {
        eventDetailResource = Transformations.map(eventRepo.getSingleEventFromDatabase(id)) {
            data: StarWarsEvent -> mapToResource(data)
        } as MutableLiveData<Resource<StarWarsUiEvent>>
        return eventDetailResource
    }

    private fun mapToResource(starWarsEvent: StarWarsEvent?): Resource<StarWarsUiEvent> {
        return if (starWarsEvent == null || starWarsEvent.id < DB_MINIMUM_ID_VALUE )
            Resource.Error(
                NoSuchElementException("Invalid EVENT_ID passed to EventDetailActivity. StarWarsEvent not found in the Room database.")
            )
        else
            Resource.Success(StarWarsUiEvent.mapToUiModel(starWarsEvent))
    }
}
