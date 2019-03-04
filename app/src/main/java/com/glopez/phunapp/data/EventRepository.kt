package com.glopez.phunapp.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventRepository {
    private val eventFeedRetriever = EventFeedRetriever()
    var eventFeedList: List<Event> = emptyList()
    var events: MutableLiveData<List<Event>> = MutableLiveData()

    // Get event feed from network
    fun getEvents(): LiveData<List<Event>> {
        eventFeedRetriever.getEventFeed(eventRepoCallback())
        return events
    }

    private fun eventRepoCallback(): Callback<List<Event>> {
        return object: Callback<List<Event>> {
            override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                Log.e("EventRepository", "Problem fetching feed data", t)
            }

            override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>?) {
                response?.isSuccessful.let {
                    eventFeedList = response?.body()?.toList() ?: emptyList()
                    events.value = eventFeedList
                    Log.d("EventRepository", "Retrieved events successfully")
                }

            }
        }
    }
}