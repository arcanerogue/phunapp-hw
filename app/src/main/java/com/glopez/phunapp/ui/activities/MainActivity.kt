package com.glopez.phunapp.ui.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.glopez.phunapp.R
import com.glopez.phunapp.data.*
import com.glopez.phunapp.ui.adapters.EventRecyclerAdapter
import kotlinx.android.synthetic.main.content_main.*
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    private val eventFeedRetriever = EventFeedRetriever()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initializeEvents()

        // Get event feed from network and create the adapter for the Recycler View
        eventFeedRetriever.getEventFeed(eventRepoCallback())


        // Create the layout manager for the Recycler View
        feed_list.layoutManager = LinearLayoutManager(this)
    }

    private fun eventRepoCallback(): Callback<List<Event>>{
        return object: Callback<List<Event>> {
            override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                Log.e("MainActivity", "Problem fetching feed data", t)
            }

            override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>?) {
                response?.isSuccessful.let {
                    val eventFeedList = response?.body()?.toList() ?: emptyList()
                    feed_list.adapter = EventRecyclerAdapter(eventFeedList)
                }

            }
        }
    }
}
