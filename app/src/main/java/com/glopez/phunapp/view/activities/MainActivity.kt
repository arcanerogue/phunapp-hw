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
import com.glopez.phunapp.view.viewmodels.FeedViewModel
import com.glopez.phunapp.view.viewmodels.ViewModelFactory
import com.glopez.phunapp.model.StarWarsEvent
import com.glopez.phunapp.model.db.Resource
import com.glopez.phunapp.utils.isNetworkAvailable
import kotlinx.android.synthetic.main.content_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private lateinit var feedViewModel: FeedViewModel
    private lateinit var adapter: EventRecyclerAdapter
    private lateinit var connectivityManager: ConnectivityManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        connectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val gridColumnCount: Int = resources.getInteger(R.integer.num_grid_columns)
        val gridLayoutManager = GridLayoutManager(this, gridColumnCount)
        feed_list.layoutManager = gridLayoutManager

        adapter = EventRecyclerAdapter(this)
        feed_list.adapter = adapter

        feedViewModel = ViewModelProviders.of(this, ViewModelFactory
            .getInstance(application as PhunApp))
            .get(FeedViewModel::class.java)

        observeEventsList()
    }

    override fun onResume() {
        super.onResume()
        feedViewModel.refreshEvents()
    }

    override fun onPause() {
        super.onPause()
        feedViewModel.removeSources()
    }

    private fun showLoading() { progress_bar.visibility = View.VISIBLE }

    private fun hideLoading() { progress_bar.visibility = View.GONE}

    private fun observeEventsList() {
        feedViewModel.getEventsResource().observe(this, Observer { resource ->
            resource?.let {
                when (resource) {
                    is Resource.Success -> {
                        hideLoading()
                        handleViewOnSuccess(resource.data)
                    }
                    is Resource.Loading -> {
                        showLoading()
                    }
                    is Resource.Error -> {
                        hideLoading()
                        handleViewOnError(resource.error)
                    }
                }
            }
        })
    }

    private fun handleViewOnSuccess(starWarsEventList: List<StarWarsEvent>?) {
        if (starWarsEventList.isNullOrEmpty() && !isNetworkAvailable(connectivityManager)) {
            Timber.d(getString(R.string.main_no_network_no_database))
        } else {
            if (starWarsEventList == null)
                Toast.makeText(this, "Received a null EventList", Toast.LENGTH_LONG).show()
            else
                adapter.setEvents(starWarsEventList)
        }
    }

    private fun handleViewOnError(error: Throwable) {
        Timber.e(error, getString(R.string.main_resource_error))
        Toast.makeText(
            this, getString(R.string.main_toast_events_fetch_fail),
            Toast.LENGTH_LONG
        ).show()
    }
}


