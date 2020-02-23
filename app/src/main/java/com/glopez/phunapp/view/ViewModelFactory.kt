package com.glopez.phunapp.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.glopez.phunapp.model.repository.EventFeedRepository
import com.glopez.phunapp.view.detail.DetailViewModel
import com.glopez.phunapp.view.feed.FeedViewModel
import java.lang.IllegalArgumentException

@Suppress("UNCHECKED_CAST")
object ViewModelFactory : ViewModelProvider.NewInstanceFactory() {
    private val eventFeedRepository = EventFeedRepository

    /**
     * Creates a new instance of the given class.
     * @param[modelClass] the class of the instance requested.
     * @return the ViewModel instance of the requested ViewModel class.
     */
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(FeedViewModel::class.java) ->
                FeedViewModel(eventFeedRepository) as T
            modelClass.isAssignableFrom(DetailViewModel::class.java) ->
                DetailViewModel(eventFeedRepository) as T
            else ->
                throw IllegalArgumentException(
                    "Unknown ViewModel class provided: ${modelClass.name}"
                )
        }
    }
}