package com.glopez.phunapp.view.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.glopez.phunapp.R
import com.glopez.phunapp.di.Injector
import com.glopez.phunapp.model.db.Resource
import com.glopez.phunapp.utils.isNetworkAvailable
import com.glopez.phunapp.view.StarWarsUiEvent
import com.glopez.phunapp.view.adapters.EventRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_event_list.*
import timber.log.Timber

class FeedFragment : Fragment() {
    private lateinit var eventRecyclerAdapter: EventRecyclerAdapter

    //    private lateinit var feedViewModel: FeedViewModel
    private var recyclerView: RecyclerView? = null
//    @Inject
//    lateinit var viewModelFactory: ViewModelFactory

    private val feedViewModel by viewModels<FeedViewModel> { Injector.get().getViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_event_list, container, false)
        recyclerView = rootView.findViewById(R.id.feed_fragment_recycler_view)
        eventRecyclerAdapter = EventRecyclerAdapter(activity as FeedFragmentListener)

        recyclerView?.apply {
            val gridColumnCount: Int = resources.getInteger(R.integer.num_grid_columns)
            layoutManager = GridLayoutManager(rootView?.context, gridColumnCount)
            adapter = eventRecyclerAdapter
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        feedViewModel = ViewModelProvider(this, viewModelFactory)
//            .get(FeedViewModel::class.java)

        observeEventsList()
    }

    override fun onResume() {
        super.onResume()
        feedViewModel.refreshEvents()
    }

    private fun showLoading() {
        fragment_progress_bar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        fragment_progress_bar.visibility = View.GONE
    }

    private fun observeEventsList() {
        feedViewModel.eventsFeed.observe(viewLifecycleOwner,
            Observer { resource ->
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
        context?.run {
            if (starWarsEventList.isNullOrEmpty() && !isNetworkAvailable(this)) {
                Timber.d(getString(R.string.main_no_network_no_database))
            } else {
                if (starWarsEventList == null)
                    Toast.makeText(
                        this,
                        "Received a null EventList",
                        Toast.LENGTH_LONG
                    ).show()
                else {
                    eventRecyclerAdapter.setEvents(starWarsEventList)
                    hideLoading()
                }
            }
        }
    }

    private fun handleViewOnError(error: Throwable) {
        Timber.e(error, getString(R.string.main_resource_error))
        context?.run {
            Toast.makeText(
                this,
                getString(R.string.main_toast_events_fetch_fail),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    interface FeedFragmentListener {
        fun onFeedEventClicked(event: StarWarsUiEvent)
        fun onShareClicked(event: StarWarsUiEvent)
    }
}