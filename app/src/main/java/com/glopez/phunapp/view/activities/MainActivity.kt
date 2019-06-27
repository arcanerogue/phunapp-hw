package com.glopez.phunapp.view.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import android.widget.Toast
import com.glopez.phunapp.PhunApp
import com.glopez.phunapp.R
import com.glopez.phunapp.view.adapters.EventRecyclerAdapter
import com.glopez.phunapp.view.viewmodels.EventViewModel
import com.glopez.phunapp.ViewModelFactory
import com.glopez.phunapp.model.network.ApiResponse
import com.glopez.phunapp.utils.isNetworkAvailable
import kotlinx.android.synthetic.main.content_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity() {
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

        observeApiResponseStatus()

        observeEventsList(adapter)
    }

    private fun showLoading() { progress_bar.visibility = View.VISIBLE }

    private fun hideLoading() { progress_bar.visibility = View.GONE}

    private fun observeApiResponseStatus() {
        eventViewModel.apiResponseStatus.observe(this, Observer { status ->
            when(status) {
                is ApiResponse.Loading<*> -> {
                    Timber.d("Api Loading State")
                    showLoading()
                }
                is ApiResponse.Success<*> -> {
                    Timber.d( "Api Success State")
                    Timber.d(getString(R.string.repo_events_fetch_success))
                    hideLoading()
                }
                is ApiResponse.Error -> {
                    Timber.d("Api Error State")
                    Timber.e(status.error, getString(R.string.repo_fetch_error))
                    hideLoading()
                }
                is ApiResponse.ResponseEmptyBody -> {
                    Timber.d(getString(R.string.repo_events_fetch_empty_body, status.responseCode))
                }
                is ApiResponse.ResponseError<*> -> {
                    Timber.d(getString(R.string.repo_success_http_error,
                        status.responseCode, status.errorBody))
                }
            }
        })
    }

    private fun observeEventsList(adapter: EventRecyclerAdapter) {
        eventViewModel.events.observe(this, Observer { events ->
            events?.let {
                // Display toast when there was no model retrieved from the database and
                // a network connection is unavailable.
                val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                if (events.isEmpty() && !isNetworkAvailable(connectivityManager)) {
                    Timber.d(getString(R.string.main_no_network_no_database))
                    Toast.makeText(this, getString(R.string.main_toast_events_fetch_fail),
                        Toast.LENGTH_LONG).show()
                } else {
                    adapter.setEvents(it)
                }
            }
        })
    }
}


