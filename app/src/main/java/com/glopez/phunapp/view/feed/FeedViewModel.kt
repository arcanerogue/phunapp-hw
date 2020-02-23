package com.glopez.phunapp.view.feed

import android.arch.lifecycle.*
import com.glopez.phunapp.model.StarWarsEvent
import com.glopez.phunapp.model.db.Resource
import com.glopez.phunapp.model.repository.FeedRepository
import com.glopez.phunapp.view.StarWarsUiEvent

/**
 * [ViewModel] for the MainActivity which displays the list of StarWarsEvent objects.
 * @param[eventFeedRepo] The application's repository instance.
 */
class FeedViewModel(private val eventFeedRepo: FeedRepository) : ViewModel() {
    private var eventsFeedResource = MutableLiveData<Resource<List<StarWarsUiEvent>>>()
    val eventsFeed: LiveData<Resource<List<StarWarsUiEvent>>> = getEventsResource()


    init {
        eventsFeedResource.value = Resource.Loading()
    }

    private fun getEventsResource(): LiveData<Resource<List<StarWarsUiEvent>>> {
        eventsFeedResource = Transformations.map(eventFeedRepo.getEvents()) {
            data -> mapToResource(data)
        } as MutableLiveData<Resource<List<StarWarsUiEvent>>>
        return eventsFeedResource
    }

    fun refreshEvents() {
        eventFeedRepo.updateEventsFromNetwork()
    }

    private fun mapToResource(starWarsEventList: Resource<List<StarWarsEvent>>): Resource<List<StarWarsUiEvent>> {
        return when (starWarsEventList) {
            is Resource.Success -> Resource.Success(starWarsEventList.data?.let { StarWarsUiEvent.mapToUiModelList(it) })
            is Resource.Loading -> Resource.Loading(emptyList())
            is Resource.Error -> Resource.Error(starWarsEventList.error)
        }
    }

    /**
     * Clears database insert disposables
     */
    override fun onCleared() {
        super.onCleared()
        eventFeedRepo.clearDisposables()
    }
}

