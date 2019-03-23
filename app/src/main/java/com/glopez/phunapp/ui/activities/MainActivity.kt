package com.glopez.phunapp.ui.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.widget.Toast
import com.glopez.phunapp.R
import com.glopez.phunapp.ui.adapters.EventRecyclerAdapter
import com.glopez.phunapp.ui.viewmodels.EventViewModel
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private val LOG_TAG: String = MainActivity::class.java.simpleName
    private lateinit var eventViewModel: EventViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create the layout manager for the Recycler View
        val gridColumnCount: Int = resources.getInteger(R.integer.num_grid_columns)
        val gridLayoutManager = GridLayoutManager(this, gridColumnCount)
        feed_list.layoutManager = gridLayoutManager

        // Create the adapter for the Recycler View
        val adapter = EventRecyclerAdapter(this)
        feed_list.adapter = adapter

        // Get the ViewModel and observe the event feed being set by the adapter
        eventViewModel = ViewModelProviders.of(this).get(EventViewModel::class.java)
        eventViewModel.eventFeedList.observe(this, Observer { events ->
            events?.let {
                // Display toast when there was no data retrieved from the database
                // and a network connection is unavailable.
                if (events.isEmpty() && !isNetworkAvailable(this)) {
                    Log.d(LOG_TAG, "Unable to retrieve data from the database to populate the " +
                            "RecyclerView adapter. No network connection to populate database from" +
                            " remote source.")
                    Toast.makeText(this, "Unable to retrieve events from the server.",
                        Toast.LENGTH_LONG).show()
                } else {
                    adapter.setEvents(it)
                }
            }
        })
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}
