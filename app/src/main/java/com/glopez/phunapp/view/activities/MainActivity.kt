package com.glopez.phunapp.view.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.widget.Toast
import com.glopez.phunapp.PhunApp
import com.glopez.phunapp.R
import com.glopez.phunapp.view.adapters.EventRecyclerAdapter
import com.glopez.phunapp.view.viewmodels.EventViewModel
import com.glopez.phunapp.ViewModelFactory
import com.glopez.phunapp.model.network.ApiResponse
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
        eventViewModel = ViewModelProviders.of(this, ViewModelFactory
            .getInstance(application as PhunApp))
            .get(EventViewModel::class.java)

        eventViewModel.apiResponseStatus.observe(this, Observer { status ->
            when(status) {
                is ApiResponse.Loading<*> -> Log.d(LOG_TAG, "Api Loading State")
                is ApiResponse.Success<*> -> Log.d(LOG_TAG, "Api Success State")
                is ApiResponse.Error -> Log.d(LOG_TAG, "Api Error State")
            }
        })

        eventViewModel.events.observe(this, Observer { events ->
            events?.let {
                // Display toast when there was no model retrieved from the database and
                // a network connection is unavailable.
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

    override fun onResume() {
        super.onResume()
//        eventViewModel.updateEventsFromNetwork()
    }
}
