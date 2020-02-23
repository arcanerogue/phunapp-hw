package com.glopez.phunapp.view.feed

import android.arch.lifecycle.Observer
import android.support.v4.app.Fragment
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.glopez.phunapp.PhunApp
import com.glopez.phunapp.R
import com.glopez.phunapp.model.db.Resource
import com.glopez.phunapp.utils.isNetworkAvailable
import com.glopez.phunapp.view.StarWarsUiEvent
import com.glopez.phunapp.view.adapters.EventRecyclerAdapter
import com.glopez.phunapp.view.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_event_list.*
import timber.log.Timber

class FeedFragment : Fragment() {
    private lateinit var eventRecyclerAdapter: EventRecyclerAdapter
    private lateinit var feedViewModel: FeedViewModel
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var activityContext: Context
    private lateinit var phunApp: PhunApp
    private var networkConnected: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val listener = activity as FeedFragmentListener
        activityContext = activity as Context
        phunApp = activity?.application as PhunApp

        connectivityManager =
            activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkConnected = isNetworkAvailable(connectivityManager)
        eventRecyclerAdapter = EventRecyclerAdapter(listener)

        feedViewModel = ViewModelProviders.of(this, ViewModelFactory)
            .get(FeedViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_event_list, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.feed_fragment_recycler_view)

        recyclerView.apply {
            val gridColumnCount: Int = resources.getInteger(R.integer.num_grid_columns)
            layoutManager = GridLayoutManager(activityContext, gridColumnCount)
            adapter = eventRecyclerAdapter
        }

        observeEventsList()
        return view
    }

    override fun onResume() {
        super.onResume()
        feedViewModel.refreshEvents()
    }

    private fun showLoading() { fragment_progress_bar.visibility = View.VISIBLE }

    private fun hideLoading() { fragment_progress_bar.visibility = View.GONE}

    private fun observeEventsList() {
        feedViewModel.eventsFeed.observe(this, Observer { resource ->
            resource?.let {
                when (resource) {
                    is Resource.Success -> {
                        showLoading()
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

    private fun handleViewOnSuccess(starWarsEventList: List<StarWarsUiEvent>?) {
        if (starWarsEventList.isNullOrEmpty() && !networkConnected) {
            Timber.d(getString(R.string.main_no_network_no_database))
        } else {
            if (starWarsEventList == null)
                Toast.makeText(activityContext, "Received a null EventList", Toast.LENGTH_LONG).show()
            else {
                eventRecyclerAdapter.setEvents(starWarsEventList)
                hideLoading()
            }
        }
    }

    private fun handleViewOnError(error: Throwable) {
        Timber.e(error, getString(R.string.main_resource_error))
        Toast.makeText(
            activityContext, getString(R.string.main_toast_events_fetch_fail),
            Toast.LENGTH_LONG
        ).show()
    }

    interface FeedFragmentListener {
        fun onFeedEventClicked(event: StarWarsUiEvent)
        fun onShareClicked(event: StarWarsUiEvent)
    }
}