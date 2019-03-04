package com.glopez.phunapp.ui.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import com.glopez.phunapp.R
import com.glopez.phunapp.ui.adapters.EventRecyclerAdapter
import com.glopez.phunapp.ui.viewmodels.EventViewModel
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var eventViewModel: EventViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create the layout manager for the Recycler View
        val gridColumnCount = resources.getInteger(R.integer.num_grid_columns)
        feed_list.layoutManager = GridLayoutManager(this, gridColumnCount)

        // Create the adapter for the Recycler View
        val adapter = EventRecyclerAdapter(this)
        feed_list.adapter = adapter

        // Get the ViewModel and observe the event feed being set by the adapter
        eventViewModel = ViewModelProviders.of(this).get(EventViewModel::class.java)
        eventViewModel.eventFeedList.observe(this, Observer { events ->
            events?.let { adapter.setEvents(it) }
        })
    }
}
