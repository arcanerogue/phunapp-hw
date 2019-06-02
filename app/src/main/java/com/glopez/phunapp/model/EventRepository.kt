package com.glopez.phunapp.model

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.res.Resources
import android.util.Log
import com.glopez.phunapp.model.db.EventDao
import com.glopez.phunapp.model.db.EventDatabase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.os.AsyncTask
import com.glopez.phunapp.R
import com.glopez.phunapp.model.network.ApiResponse
import com.glopez.phunapp.model.network.EventFeedProvider

class EventRepository(context: Context, eventDatabase: EventDatabase) {
    private val LOG_TAG = EventRepository::class.java.simpleName
    private val eventApi = EventFeedProvider()
    private var eventFeedList: List<Event> = emptyList()
    private val eventDao: EventDao
    private val resources: Resources = context.resources
    val apiResultState = MutableLiveData<ApiResponse>()

    companion object {
        private var INSTANCE: EventRepository? = null

        fun getInstance(context: Context, eventDb: EventDatabase): EventRepository {
            synchronized(this) {
                if (INSTANCE == null) {
                    // Create repository
                    INSTANCE = EventRepository(context, eventDb)
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
        Log.d(LOG_TAG,
            "Retrieving events from network.")
        apiResultState.value = ApiResponse.Loading(eventFeedList)
//        eventApi.getEventFeed(eventRepoCallback())
        eventApi.getEventFeed(object: Callback<List<Event>> {
            // Network exception occurred talking to the server or an unexpected exception
            // occurred creating the request or processing the response
            override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                apiResultState.value = ApiResponse.Error(t)
                Log.e(LOG_TAG, resources.getString(R.string.repo_fetch_error), t)
            }
            // Received an HTTP Response
            override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                processNetworkResponse(response)
            }
        })
    }

    private fun processNetworkResponse(response: Response<List<Event>>) {
        val responseBody = response.body()
        val responseCode:String = response.code().toString()
        val errorBody = response.errorBody()

        // HTTP Response Code is in the 200-300 range. Populate database with events
        // retrieved from remote data source.
        if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
            // If the Response errorBody is not empty, populate eventFeedList with list of
            // events from errorBody.
            apiResultState.value = ApiResponse.Success(responseBody)
            insertEventsIntoDatabase(responseBody)
            Log.d(LOG_TAG, resources.getString(
                R.string.repo_response_body_count,
                eventFeedList.size))
            Log.d(LOG_TAG, resources.getString(R.string.repo_events_fetch_success))
        } else if (response.isSuccessful && responseBody.isNullOrEmpty()) {
            // Received Response with empty errorBody.
            apiResultState.value = ApiResponse.ResponseEmptyBody(responseCode)
            Log.d(LOG_TAG, resources.getString(R.string.repo_events_fetch_empty_body,
                response.body()))
        } else {
            // HTTP Response Code is in the 300's, 400's, 500's, or application-level failure.
            apiResultState.value = ApiResponse.ResponseError(responseCode, errorBody)
            Log.d(LOG_TAG, resources.getString(R.string.repo_success_http_error,
                response.code().toString(), response.body()))
        }
    }

    fun getNetworkResponseStatus() : LiveData<ApiResponse> {
        return apiResultState
    }

    private fun insertEventsIntoDatabase(eventList: List<Event>) {
        for (event: Event in eventList) AsyncTask.execute{ eventDao.insert(event) }
    }

    fun getSingleEventFromDatabase(id: Int): LiveData<Event> {
        return eventDao.find(id)
    }
}
