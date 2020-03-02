package com.glopez.phunapp.model.repository

import com.glopez.phunapp.model.db.EventDao
import com.glopez.phunapp.model.db.EventDatabase
import com.glopez.phunapp.model.network.ApiResponse
import com.glopez.phunapp.model.network.EventFeedProvider
import timber.log.Timber
import com.glopez.phunapp.R
import com.glopez.phunapp.constants.DB_MINIMUM_ID_VALUE
import com.glopez.phunapp.model.StarWarsEvent
import com.glopez.phunapp.utils.StringsResourceProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.lang.Exception
import java.util.concurrent.TimeUnit

object EventFeedRepository: FeedRepository {
    private val eventApi = EventFeedProvider()
    private lateinit var eventDao: EventDao
    private lateinit var apiResponse: ApiResponse<List<StarWarsEvent>>
    private var refreshTimestamp: Long = 0
    private val refreshTimeout: Long = TimeUnit.MINUTES.toMillis(1)

    fun init(eventDatabase: EventDatabase) {
        eventDao = eventDatabase.eventDao()
        apiResponse = ApiResponse.Loading(emptyList())
    }

    override suspend fun getEvents(): List<StarWarsEvent> {
        return withContext(Dispatchers.IO) { eventDao.getAllEvents() }
    }

    override suspend fun getEventById(id: Int): StarWarsEvent {
        return withContext(Dispatchers.IO) { eventDao.find(id) }
    }

    private fun shouldRefresh(): Boolean {
        val lastRefresh = refreshTimestamp
        val currentTime = System.currentTimeMillis()
        return currentTime - lastRefresh > refreshTimeout
    }

    override suspend fun updateEventsFromNetwork() {
        if (shouldRefresh()) {
            refreshTimestamp = System.currentTimeMillis()
            getEventsFromNetwork()
        }
    }

    override suspend fun getEventsFromNetwork() {
        Timber.d("Retrieving events from network.")
        apiResponse = ApiResponse.Loading(emptyList())
        withContext(Dispatchers.Default) {
           try {
               val response = eventApi.getEventFeed()
               apiResponse = ApiResponse.onResponse(response)
               when (apiResponse) {
                   is ApiResponse.Success -> {
                       insertEventsIntoDatabase((apiResponse as ApiResponse.Success<List<StarWarsEvent>>).body)
                   }
               }
           } catch(exception: Exception) {
               apiResponse = ApiResponse.onFailure(exception.message)
               Timber.e(String.format("${StringsResourceProvider.getString(R.string.repo_fetch_error)}. ${exception.message}"))
           }
        }
    }

    override suspend fun insertEventsIntoDatabase(starWarsEvents: List<StarWarsEvent>) {
        for (starWarsEvent: StarWarsEvent in starWarsEvents) {
            // If the id field was not present in the Response object, the default value of 0 will be set.
            // If this is the case, the StarWarsEvent will not be inserted into the database.
            if (starWarsEvent.id < DB_MINIMUM_ID_VALUE) {
                Timber.d("Skipping starWarsEvent with invalid Id value.")
            } else {
                withContext(Dispatchers.IO) {
                    try {
                        eventDao.insert(starWarsEvent)
                    } catch (exception: IOException) {
                        Timber.e(exception)
                    }
                }
            }
        }
    }
}





