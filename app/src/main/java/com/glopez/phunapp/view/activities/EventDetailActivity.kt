package com.glopez.phunapp.view.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.glopez.phunapp.PhunApp
import com.glopez.phunapp.R
import com.glopez.phunapp.ViewModelFactory
import com.glopez.phunapp.model.createEventDateFormatString
import com.glopez.phunapp.model.createShareEventMessage
import com.glopez.phunapp.utils.*
import com.glopez.phunapp.view.viewmodels.EventDetailViewModel
import kotlinx.android.synthetic.main.activity_event_detail.*

private const val EVENT_ID: String = "event_id"

class EventDetailActivity : AppCompatActivity() {
    private lateinit var eventDetailViewModel: EventDetailViewModel
    private lateinit var eventPhoneNumber: String
    private lateinit var eventShareMessage: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)
        setSupportActionBar(detail_toolbar)

        val eventImage: ImageView = findViewById(R.id.detail_event_image)
        val eventDate: TextView = findViewById(R.id.detail_event_date)
        val eventTitle: TextView = findViewById(R.id.detail_event_title)
        val eventLocation: TextView = findViewById(R.id.detail_event_location)
        val eventDescription: TextView = findViewById(R.id.detail_event_description)
        val eventId: Int = intent.getIntExtra(EVENT_ID, 0)

        // Show the Up button in the action bar and hide the app name.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Get the ViewModel and observe the single event specified by the user click from
        // the list of events in MainActivity
        eventDetailViewModel = ViewModelProviders.of(this, ViewModelFactory
            .getInstance(application as PhunApp))
            .get(EventDetailViewModel::class.java)

        eventDetailViewModel.getEvent(eventId).observe(this, Observer { event ->
            event?.let {
                eventPhoneNumber = it.phone ?: ""

                if (it.date != null) {
                    eventDate.text = it.createEventDateFormatString()
                } else {
                    eventDate.visibility = View.GONE
                }
                eventShareMessage = it.createShareEventMessage()
                eventTitle.text = it.title
                eventLocation.text = it.location2
                eventDescription.text = it.description

                Glide.with(this)
                    .load(it.image)
                    .onlyRetrieveFromCache(true)
                    .error(R.drawable.placeholder_nomoon)
                    .centerCrop()
                    .into(eventImage)

            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail_menu, menu)
        val packageManager = this.applicationContext.packageManager
        if(deviceCanCall(packageManager) || eventPhoneNumber.isEmpty()) {
            val callIcon: MenuItem? = menu?.findItem(R.id.detail_action_call)
            callIcon?.isVisible = false
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            android.R.id.home -> {
                navigateUpTo(Intent(this, MainActivity::class.java))
                true
            }
            R.id.detail_action_call -> {
                createCallIntent(this, eventPhoneNumber)
                true
            }
            R.id.detail_action_share -> {
                createShareIntent(this, eventShareMessage)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
}

