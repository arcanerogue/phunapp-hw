package com.glopez.phunapp.view.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glopez.phunapp.model.db.Resource
import com.glopez.phunapp.model.repository.FeedRepository
import com.glopez.phunapp.view.StarWarsUiEvent
import com.glopez.phunapp.view.StarWarsUiEventMapper
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * [ViewModel] for the MainActivity which displays the list of StarWarsEvent objects.
 * @param[eventFeedRepo] The application's repository instance.
 */
class FeedViewModel(private val eventFeedRepo: FeedRepository) : ViewModel() {
    private var eventsFeedResource = MutableLiveData<Resource<List<StarWarsUiEvent>>>()
    private val uiEventMapper by lazy { StarWarsUiEventMapper() }
    val eventsFeed: LiveData<Resource<List<StarWarsUiEvent>>>
        get() = eventsFeedResource

    init {
        viewModelScope.launch {
            val resource = eventFeedRepo.getEvents()
            if (resource.isNullOrEmpty()) {
                eventsFeedResource.value = Resource.Loading(emptyList())
                Timber.d("init eventsFeedResource loading set on ${Thread.currentThread().name}")
            } else {
                eventsFeedResource.value =
                    Resource.Success(resource.map { uiEventMapper.mapToModel(it) })
                Timber.d("init eventsFeedResource success set on ${Thread.currentThread().name}")
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
                eventsFeedResource.value = Resource.Success(resource.map { uiEventMapper.mapToModel(it) })
            }
        }
    }
}

