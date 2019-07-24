package com.glopez.phunapp.model

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.glopez.phunapp.model.db.EventDao
import com.glopez.phunapp.model.db.EventDatabase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.os.AsyncTask
import com.glopez.phunapp.model.network.ApiResponse
import com.glopez.phunapp.model.network.EventFeedProvider
import timber.log.Timber
import android.arch.lifecycle.MediatorLiveData
import android.os.SystemClock
import com.glopez.phunapp.R
import com.glopez.phunapp.model.db.Resource
import com.glopez.phunapp.utils.StringsResourceProvider
import java.util.concurrent.TimeUnit

class EventRepository(
    eventDatabase: EventDatabase,
    private val eventApi: EventFeedProvider) {
    private val eventDao: EventDao = eventDatabase.eventDao()
    private val defaultValueSetForMissingIdField: Int = 0
    private val apiResultState = MutableLiveData<ApiResponse<List<Event>>>()
    private val dataResult = MediatorLiveData<Resource<List<Event>>>()

    private var refreshTimestamp: Long = 0
    private val timeoutInMinutes = 1
    private val refreshTimeout: Long = TimeUnit.MINUTES.toMillis(timeoutInMinutes.toLong())


    companion object {
        private var INSTANCE: EventRepository? = null

        fun getInstance(eventDb: EventDatabase, eventApi: EventFeedProvider): EventRepository {
            synchronized(this) {
                if (INSTANCE == null) {
                    // Create repository
                    INSTANCE = EventRepository(eventDb, eventApi)
                }
                return INSTANCE as EventRepository
            }
        }
    }

//    init {
    fun loadEvents() {
        dataResult.value = Resource.Loading(emptyList())
        val dbSource = getEventsFromDatabase()
//        dataResult.addSource(dbSource) { dbData ->
        dataResult.addSource(dbSource) {
            dataResult.removeSource(dbSource)
//            if (dbData.isNullOrEmpty()) {
            if (shouldRefresh()) {
                // Call web service to fetch events from remote source
                refreshTimestamp = SystemClock.uptimeMillis()
//                getEventsFromNetwork()
                getEventsFromNetwork(dbSource)
            } else {
                dataResult.addSource(dbSource) { updatedDbData ->
                    dataResult.value = Resource.Success(updatedDbData)
                }
            }
        }
    }

    fun getEventsFromDatabase(): LiveData<List<Event>> {
        return eventDao.getAllEvents()
    }

//    private fun getEventsFromNetwork() {
    private fun getEventsFromNetwork(dbSource: LiveData<List<Event>>) {
        Timber.d("Retrieving events from network.")
//        dataResult.addSource(dbSource) {
//            dataResult.value = Resource.Loading(emptyList())
//        }
        apiResultState.value = ApiResponse.Loading(emptyList())
        dataResult.addSource(apiResultState) {
            eventApi.getEventFeed(object : Callback<List<Event>> {
                // Network exception occurred talking to the server or an unexpected exception
                // occurred creating the request or processing the response
                override fun onFailure(call: Call<List<Event>>, error: Throwable) {
                    dataResult.removeSource(apiResultState)
                    apiResultState.value = ApiResponse.onFailure(error.message)
                    Timber.d("Api Error State")
                    dataResult.addSource(dbSource) { dbData ->
                        dataResult.removeSource(dbSource)
                        if (dbData.isNullOrEmpty()) {
                            dataResult.value = Resource.Error(error)
                            Timber.e(error.message, StringsResourceProvider.getString(R.string.repo_fetch_error))
                        } else {
                            dataResult.addSource(dbSource) { updatedDbData ->
                                dataResult.value = Resource.Success(updatedDbData)
                            }
                        }
                    }
                }

                // Received an HTTP Response
                override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                    val apiResponse = ApiResponse.onResponse(response)
                    when (apiResponse) {
                        // HTTP Response Code is in the 200-300 range.
                        is ApiResponse.Success<List<Event>> -> {
                            dataResult.removeSource(apiResultState)
                            apiResultState.value = apiResponse // 2nd api call is made here
                            insertEventsIntoDatabase(apiResponse.body)
                            Timber.d("Received response with count: ${apiResponse.body.size}")
                            dataResult.addSource(getEventsFromDatabase()) {
                                dataResult.value = Resource.Success(it)
                            }
                            Timber.d( "Api Success State")
                            Timber.d(StringsResourceProvider.getString(R.string.repo_events_fetch_success))
                        }
                        // Received Response with empty body.
                        is ApiResponse.EmptyBody<List<Event>> -> {
                            dataResult.removeSource(apiResultState)
                            apiResultState.value = apiResponse
                            Timber.d(StringsResourceProvider.getString(R.string.repo_events_fetch_empty_body))
                            dataResult.addSource(getEventsFromDatabase()) {
                                dataResult.value = Resource.Success(it)
                            }
                        }
                        // HTTP Response Code is in the 300's, 400's, 500's, or application-level failure.
                        is ApiResponse.Error -> {
                            dataResult.removeSource(apiResultState)
                            apiResultState.value = apiResponse
                            Timber.d("Api Error State")
                            Timber.e(apiResponse.errorMessage, StringsResourceProvider.getString(R.string.repo_fetch_error))
                            dataResult.addSource(getEventsFromDatabase()) {
                                dataResult.value = Resource.Success(it)
                            }
                        }
                    }
                }
            })
        }
    }

    fun getApiResponseState() : LiveData<ApiResponse<List<Event>>> {
        return this.apiResultState
    }

    fun getDbResultState() : LiveData<Resource<List<Event>>> {
        return this.dataResult
    }

    private fun insertEventsIntoDatabase(eventList: List<Event>) {
        for (event: Event in eventList) {
            // If the id field was not present in the Response object, the default value of 0 will be set.
            // If this is the case, the Event will not be inserted into the database.
            if (event.id > defaultValueSetForMissingIdField) {
                AsyncTask.execute { eventDao.insert(event) }
            }
        }
    }

    fun getSingleEventFromDatabase(id: Int): LiveData<Event> {
        return eventDao.find(id)
    }

    private fun shouldRefresh(): Boolean {
        val lastRefresh = refreshTimestamp
        val currentTime = SystemClock.uptimeMillis()
        return if (currentTime - lastRefresh > refreshTimeout) {
            refreshTimestamp = currentTime
            true
        } else
            false
    }
}





