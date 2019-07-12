package com.glopez.phunapp.model

import android.arch.lifecycle.LiveData
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
import com.glopez.phunapp.model.webservice.EventFeedProvider

private val LOG_TAG = EventRepository::class.java.simpleName

class EventRepository(context: Context, eventDatabase: EventDatabase) {
    private val eventFeedRetriever = EventFeedProvider()
    var eventFeedList: List<Event> = emptyList()
    private val eventDao: EventDao
    private val resources: Resources = context.resources

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
        eventFeedRetriever.getEventFeed(eventRepoCallback())
        eventDao = eventDatabase.eventDao()
    }

    fun getEvents(): LiveData<List<Event>> {
        return eventDao.getAllEvents()
    }

    fun insertEvent(eventList: List<Event>) {
        for (event: Event in eventList) {
            // If the id field was not present in the Response object, the default value of 0 will be set.
            // If this is the case, the Event will not be inserted into the database.
            if (event.id > 0) {
                AsyncTask.execute { eventDao.insert(event) }
            }
        }
    }

    fun getSingleEvent(id: Int): LiveData<Event> {
        return eventDao.find(id)
    }

    private fun eventRepoCallback(): Callback<List<Event>> {
        return object: Callback<List<Event>> {
            // Network exception occurred talking to the server or an unexpected exception
            // occurred creating the request or processing the response
            override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                Log.e(LOG_TAG, resources.getString(R.string.repo_fetch_error), t)
            }

            // Received an HTTP Response
            override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                // HTTP Response Code is in the 200-300 range
                if (response.isSuccessful) {
                    // If the Response body is not empty, populate eventFeedList with list of
                    // events from body. Otherwise, populate with an empty list
                    eventFeedList = response.body() ?: emptyList()
                    Log.d(LOG_TAG, resources.getString(R.string.repo_response_body_count,
                        eventFeedList.size))
                    if (eventFeedList.isNotEmpty()) {
                        Log.d(LOG_TAG, resources.getString(R.string.repo_events_fetch_success))
                        insertEvent(eventFeedList)
                    } else {
                        Log.d(LOG_TAG, resources.getString(R.string.repo_events_fetch_empty_body,
                            response.body()))
                    }
                } else {
                    // HTTP Response Code is in the 300's, 400's, 500's, or
                    // application-level failure.
                    Log.d(LOG_TAG, resources.getString(R.string.repo_success_http_error,
                        response.code().toString(), response.body()))
                }
            }
        }
    }
}