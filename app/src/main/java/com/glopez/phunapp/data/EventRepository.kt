package com.glopez.phunapp.data

import android.arch.lifecycle.LiveData
import android.content.Context
import android.util.Log
import com.glopez.phunapp.data.db.EventDao
import com.glopez.phunapp.data.db.EventDatabase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.os.AsyncTask
import com.glopez.phunapp.data.webservice.EventFeedProvider

class EventRepository(context: Context, eventDatabase: EventDatabase) {
    private val LOG_TAG = EventRepository::class.java.simpleName
    private val eventFeedRetriever = EventFeedProvider()
    var eventFeedList: List<Event> = emptyList()
    private val eventDao: EventDao

    companion object {
        private var INSTANCE: EventRepository? = null

        fun getInstance(context: Context, eventDb: EventDatabase): EventRepository {
            synchronized(this) {
                if (INSTANCE == null) {
                    // Create repository
                    INSTANCE = EventRepository(context, eventDb)
                }
                return INSTANCE!!
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
           AsyncTask.execute{ eventDao.insert(event) }
        }
    }

    fun getSingleEvent(id: Int): LiveData<Event> {
        return eventDao.find(id)
    }

    private fun eventRepoCallback(): Callback<List<Event>> {
        return object: Callback<List<Event>> {
            // Network exception occurred talking to the server or
            // Or an unexpected exception occurred creating the request
            // Or processing the response
            override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                Log.d(LOG_TAG, "Problem fetching feed data.")
                t.printStackTrace()
            }

            // Received an HTTP Response
            override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                // HTTP Response Code is in the 200-300 range
                if (response.isSuccessful) {
                    // If the Response body is not empty, populate eventFeedList with
                    // list of events from body. Otherwise, populate with an empty list
                    eventFeedList = response.body() ?: emptyList()
                    Log.d(LOG_TAG, "Response Body list count: ${eventFeedList.size}")
                    if (eventFeedList.isNotEmpty()) {
                        Log.d(LOG_TAG, "Retrieved events successfully.")
                        insertEvent(eventFeedList)
                    } else {
                        Log.d(LOG_TAG, "Received Response with empty body." +
                                "\n${response.body().toString()}")
                    }
                } else {
                    // HTTP Response Code is in the 300's, 400's, 500's,
                    // or application-level failure.
                    Log.d(LOG_TAG, "Failed to retrieve events. Response code: " +
                            "${response.code()} with error body:\n${response.body()}")
                }
            }
        }
    }
}
