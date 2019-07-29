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
import com.glopez.phunapp.model.db.Resource
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

        observeEventsList(adapter)
    }

    override fun onResume() {
        super.onResume()
        eventViewModel.refreshEvents()
    }

    override fun onPause() {
        super.onPause()
        eventViewModel.removeObservables()
    }

    private fun showLoading() { progress_bar.visibility = View.VISIBLE }

    private fun hideLoading() { progress_bar.visibility = View.GONE}

    private fun observeEventsList(adapter: EventRecyclerAdapter) {
//        eventViewModel.getEventsResourceStatus().observe(this, Observer { resource ->
        eventViewModel.getEventsResourceStatus().observe(this, Observer { resource ->
            resource?.let {
                when (resource) {
                    is Resource.Success -> {
                        hideLoading()
                        val connectivityManager =
                            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                        if (resource.data.isNullOrEmpty() && !isNetworkAvailable(connectivityManager)) {
                            Timber.d(getString(R.string.main_no_network_no_database))
                        } else {
                            if (resource.data == null)
                                Toast.makeText(this, "null resource data", Toast.LENGTH_LONG).show()
                            else
                                adapter.setEvents(resource.data)
                        }
                    }
                    is Resource.Loading -> {
                        showLoading()
                    }
                    is Resource.Error -> {
                        Timber.e(resource.errorMessage, getString(R.string.main_resource_error))
                        Toast.makeText(
                            this, getString(R.string.main_toast_events_fetch_fail),
                            Toast.LENGTH_LONG
                        ).show()
                        hideLoading()
                    }
                }
            }
        })
    }
}


