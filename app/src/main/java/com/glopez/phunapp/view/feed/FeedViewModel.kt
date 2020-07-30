package com.glopez.phunapp.view.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glopez.phunapp.model.db.Resource
import com.glopez.phunapp.model.repository.FeedRepository
import com.glopez.phunapp.view.StarWarsUiEvent
import kotlinx.coroutines.launch

/**
 * [ViewModel] for the MainActivity which displays the list of StarWarsEvent objects.
 * @param[eventFeedRepo] The application's repository instance.
 */
class FeedViewModel(private val eventFeedRepo: FeedRepository) : ViewModel() {
    private var eventsFeedResource = MutableLiveData<Resource<List<StarWarsUiEvent>>>()
    val eventsFeed: LiveData<Resource<List<StarWarsUiEvent>>>
        get() = eventsFeedResource

    init {
        viewModelScope.launch {
            val resource = eventFeedRepo.getEvents()
            if (resource.isNullOrEmpty()) {
                eventsFeedResource.value = Resource.Loading(emptyList())
            } else {
                eventsFeedResource.value =
                    Resource.Success(StarWarsUiEvent.mapToUiModelList(resource))
            }
        }
    }

    fun refreshEvents() {
        viewModelScope.launch {
            eventFeedRepo.updateEvents()
            val resource = eventFeedRepo.getEvents()
            if (resource.isNullOrEmpty()) {
                eventsFeedResource.value = Resource.Error(Exception("no list found"))
            } else {
                eventsFeedResource.value = Resource.Success(StarWarsUiEvent.mapToUiModelList(resource))
            }
        }
    }
}

