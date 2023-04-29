package com.glopez.phunapp.model.repository

import com.glopez.phunapp.R
import com.glopez.phunapp.constants.DB_MINIMUM_ID_VALUE
import com.glopez.phunapp.model.StarWarsEvent
import com.glopez.phunapp.model.db.EventDao
import com.glopez.phunapp.model.network.ApiResponse
import com.glopez.phunapp.model.network.FeedProvider
import com.glopez.phunapp.utils.DispatcherProvider
import com.glopez.phunapp.utils.StringsResourceProvider
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class EventFeedRepository @Inject constructor(
    private val eventApi: FeedProvider,
    private val eventDao: EventDao,
    private val dispatcher: DispatcherProvider
) : FeedRepository {
    private var apiResponse: ApiResponse<List<StarWarsEvent>> = ApiResponse.Loading(emptyList())
    private var refreshTimestamp: Long = 0
    private val refreshTimeout: Long = TimeUnit.MINUTES.toMillis(1)

    override suspend fun getEvents(): List<StarWarsEvent> {
        return withContext(dispatcher.io()) { eventDao.getAllEvents() }
    }

    override suspend fun getEvent(id: Int): StarWarsEvent? {
        return withContext(dispatcher.io()) { eventDao.find(id) }
    }

    private fun shouldRefresh(): Boolean {
        val lastRefresh = refreshTimestamp
        val currentTime = System.currentTimeMillis()
        return currentTime - lastRefresh > refreshTimeout
    }

    override suspend fun updateEvents() {
        if (shouldRefresh()) {
            refreshTimestamp = System.currentTimeMillis()
            getEventsFromNetwork()
        }
    }

    private suspend fun getEventsFromNetwork() {
        Timber.d("Retrieving events from network.")
        apiResponse = ApiResponse.Loading(emptyList())
        withContext(dispatcher.default()) {
            try {
                val response = eventApi.getEvents()
                apiResponse = ApiResponse.onResponse(response)
                when (apiResponse) {
                    is ApiResponse.Success -> {
                        saveEvents((apiResponse as ApiResponse.Success<List<StarWarsEvent>>).body)
                    }
                }
            } catch (exception: Exception) {
                apiResponse = ApiResponse.onFailure(exception.message)
                Timber.e(String.format("${StringsResourceProvider.getString(R.string.repo_fetch_error)}. ${exception.message}"))
            }
        }
    }

    override suspend fun saveEvents(starWarsEvents: List<StarWarsEvent>) {
        for (starWarsEvent: StarWarsEvent in starWarsEvents) {
            // If the id field was not present in the Response object, the default value of 0 will be set.
            // If this is the case, the StarWarsEvent will not be inserted into the database.
            if (starWarsEvent.id < DB_MINIMUM_ID_VALUE) {
                Timber.d("Skipping starWarsEvent with invalid Id value.")
            } else {
                withContext(dispatcher.io()) {
                    try {
                        eventDao.insert(starWarsEvent)
                    } catch (exception: IOException) {
                        Timber.e("Unable to insert starWarsEvent into the database: $exception")
                    }
                }
            }
        }
    }
}





