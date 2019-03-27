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
import com.glopez.phunapp.utils.Utils
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
                // Display toast when there was no data retrieved from the database and
                // a network connection is unavailable.
//                if (events.isEmpty() && !isNetworkAvailable(this)) {
                if (events.isEmpty() && !Utils.isNetworkAvailable(this)) {
                    Log.d(LOG_TAG, getString(R.string.main_no_network_no_database))
                    Toast.makeText(this, getString(R.string.main_toast_events_fetch_fail),
                        Toast.LENGTH_LONG).show()
                } else {
                    adapter.setEvents(it)
                }
            }
        })
    }
}
