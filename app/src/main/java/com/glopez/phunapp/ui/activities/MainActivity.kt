package com.glopez.phunapp.ui.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.glopez.phunapp.R
import com.glopez.phunapp.data.*
import com.glopez.phunapp.ui.adapters.EventRecyclerAdapter
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    val eventList = ArrayList<Event>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeEvents()

        // Create the layout manager for the Recycler View
        feed_list.layoutManager = LinearLayoutManager(this)

        // Create the adapter for the Recycler View
        feed_list.adapter = EventRecyclerAdapter(this, eventList)

    }

    private fun initializeEvents(){
        var event = Event(1, "Stop Rebel Forces","Hoth")
        eventList.add(event)

        event = Event(2, "Sith Academy Orientation","Korriban")
        eventList.add(event)

        event = Event(3, "Run the Naboo Blockade","Naboo")
        eventList.add(event)

        event = Event(4, "Rescue Queen Amidala","Naboo")
        eventList.add(event)
    }
}
