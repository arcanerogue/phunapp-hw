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

class EventRepository(eventDatabase: EventDatabase) {
    private val eventApi = EventFeedProvider()
    private var eventFeedList: List<Event> = emptyList()
    private val eventDao: EventDao
    val apiResultState = MutableLiveData<ApiResponse>()

    companion object {
        private var INSTANCE: EventRepository? = null

        fun getInstance(eventDb: EventDatabase): EventRepository {
            synchronized(this) {
                if (INSTANCE == null) {
                    // Create repository
                    INSTANCE = EventRepository(eventDb)
                }
                return INSTANCE as EventRepository
            }
        }
    }

    init {
        // Call web service to fetch events from remote source
        getEventsFromNetwork()
        eventDao = eventDatabase.eventDao()
    }

    fun getEventsFromDatabase(): LiveData<List<Event>> {
        return eventDao.getAllEvents()
    }

    fun getEventsFromNetwork() {
        Timber.d("Retrieving events from network.")
        apiResultState.postValue(ApiResponse.Loading(eventFeedList))
        eventApi.getEventFeed(object: Callback<List<Event>> {
            // Network exception occurred talking to the server or an unexpected exception
            // occurred creating the request or processing the response
            override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                apiResultState.postValue(ApiResponse.Error(t))
            }
            // Received an HTTP Response
            override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                setApiResponseState(response)
            }
        })
    }

    private fun setApiResponseState(response: Response<List<Event>>) {
        val responseBody = response.body()
        val responseCode:String = response.code().toString()
        val errorBody = response.errorBody().toString()

        // HTTP Response Code is in the 200-300 range.
        if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
            // If the Response body is not empty, populate eventFeedList with list of
            // events from body. Populate database with events retrieved from remote data source.
            apiResultState.postValue(ApiResponse.Success(responseBody))
            insertEventsIntoDatabase(responseBody)
            Timber.d("Received response with count: ${responseBody.size}")
        } else if (response.isSuccessful && responseBody.isNullOrEmpty()) {
            // Received Response with empty body.
            apiResultState.postValue(ApiResponse.ResponseEmptyBody(responseCode))
        } else {
            // HTTP Response Code is in the 300's, 400's, 500's, or application-level failure.
            apiResultState.postValue(ApiResponse.ResponseError(responseCode, errorBody))
        }
    }

    fun getApiResponseState() : LiveData<ApiResponse> {
        return this.apiResultState
    }

    private fun insertEventsIntoDatabase(eventList: List<Event>) {
        for (event: Event in eventList) {
            // If the id field was not present in the Response object, the default value of 0 will be set.
            // If this is the case, the Event will not be inserted into the database.
            if (event.id > 0) {
                AsyncTask.execute { eventDao.insert(event) }
            }
        }
    }

    fun getSingleEventFromDatabase(id: Int): LiveData<Event> {
        return eventDao.find(id)
    }
}
