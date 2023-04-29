package com.glopez.phunapp.view.feed

import android.os.Bundle
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import com.glopez.phunapp.model.db.Resource
import com.glopez.phunapp.model.repository.FeedRepository
import com.glopez.phunapp.view.StarWarsUiEvent
import com.glopez.phunapp.view.StarWarsUiEventMapper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * [ViewModel] for the MainActivity which displays the list of StarWarsEvent objects.
 * @param[eventFeedRepo] The application's repository instance.
 */
class FeedViewModel(
    private val eventFeedRepo: FeedRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val FEED_SAVED_STATE_KEY = "FEED_SAVED_STATE_KEY"
    }

    private var eventsFeedResource = MutableLiveData<Resource<List<StarWarsUiEvent>>>()
//    private var eventsFeedResource: MutableLiveData<Resource<List<StarWarsUiEvent>>> =
//        savedStateHandle.getLiveData(FEED_SAVED_STATE_KEY)

    private val uiEventMapper by lazy { StarWarsUiEventMapper() }
    val eventsFeed: LiveData<Resource<List<StarWarsUiEvent>>>
        get() = eventsFeedResource

    init {
        viewModelScope.launch {
            val resource = eventFeedRepo.getEvents()
            if (resource.isEmpty()) {
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
            if (resource.isEmpty()) {
                eventsFeedResource.value = Resource.Error(Exception("no list found"))
            } else {
                eventsFeedResource.value =
                    Resource.Success(resource.map { uiEventMapper.mapToModel(it) })
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory @AssistedInject constructor(
        @Assisted owner: SavedStateRegistryOwner,
        defaultState: Bundle?,
        private val eventFeedRepository: FeedRepository
    ) : AbstractSavedStateViewModelFactory(owner, defaultState) {

        override fun <T : ViewModel> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            return FeedViewModel(eventFeedRepository, handle) as T
        }
    }
}

