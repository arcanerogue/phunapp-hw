package com.glopez.phunapp.data

import android.app.Application
import android.arch.lifecycle.LiveData
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.glopez.phunapp.data.db.EventDao
import com.glopez.phunapp.data.db.EventDatabase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.net.ConnectivityManager
import com.glopez.phunapp.data.webservice.EventFeedRetriever

class EventRepository(application: Application) {
    private val LOG_TAG = EventRepository::class.java.simpleName
    private val eventFeedRetriever = EventFeedRetriever()
    var eventFeedList: List<Event> = emptyList()
    val context = application.applicationContext

    private val eventDao: EventDao

    init {
        val eventDb: EventDatabase = EventDatabase.getDatabase(context)
        eventDao = eventDb.eventDao()
    }

    fun getEvents(): LiveData<List<Event>> {
        // Call web service to fetch events from remote source
        // if a network connection is available
        if (isNetworkAvailable(context)) {
                eventFeedRetriever.getEventFeed(eventRepoCallback())
            }
        return eventDao.getAllEvents()
    }

    fun insertEvent(eventList: List<Event>) {
        for(event in eventList) {
            eventDao.insert(event)
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    private fun eventRepoCallback(): Callback<List<Event>> {
        return object: Callback<List<Event>> {
            // Network exception occurred talking to the server or
            // Or an unexpected exception occurred creating the request
            // Or processing the response
            override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                Log.e(LOG_TAG, "Problem fetching feed data", t)
                Toast.makeText(context, "Problem fetching feed data from the server", Toast.LENGTH_LONG).show()
            }
            // Received an HTTP Response
            override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                // HTTP Response Code is in the 200-300 range
                if (response.isSuccessful) {
                    let {
                        // If the Response body is not empty, populate eventFeedList with
                        // list of events from body. Otherwise, populate with an empty list
                        eventFeedList = response.body() ?: emptyList()
                        Log.d(LOG_TAG, "Response Body list count: ${eventFeedList.size}")
                        // events.value = eventFeedList
                        if (eventFeedList.isNotEmpty()) {
                            Log.d(LOG_TAG, "Retrieved events successfully.")
                            insertEvent(eventFeedList)
                        } else {
                            Log.d(LOG_TAG, "Received Response with empty body.")
                            Toast.makeText(context, "Error retrieving events from the server.", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                // HTTP Response Code is in the 300's, 400's, 500's, or
                // application-level failure.
                else {
                    Log.d(LOG_TAG, "Failed to retrieve events. Response code: ${response.code()}")
                    Toast.makeText(context, "Unable to retrieve events from the server.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
