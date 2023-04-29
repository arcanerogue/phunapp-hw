package com.glopez.phunapp.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.glopez.phunapp.model.repository.FeedRepository
import com.glopez.phunapp.view.detail.DetailViewModel
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("UNCHECKED_CAST")
@Singleton
class ViewModelFactory @Inject constructor(private val eventFeedRepository: FeedRepository) :
    ViewModelProvider.Factory {

    /**
     * Creates a new instance of the given class.
     * @param[modelClass] the class of the instance requested.
     * @return the ViewModel instance of the requested ViewModel class.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
//            modelClass.isAssignableFrom(FeedViewModel::class.java) ->
//                FeedViewModel(eventFeedRepository) as T
            modelClass.isAssignableFrom(DetailViewModel::class.java) ->
                DetailViewModel(eventFeedRepository) as T
            else ->
                throw IllegalArgumentException(
                    "Unknown ViewModel class provided: ${modelClass.name}"
                )
        }
    }
}