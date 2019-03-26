package com.glopez.phunapp.ui.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.glopez.phunapp.R
import com.glopez.phunapp.data.Event
import com.glopez.phunapp.ui.viewmodels.EventDetailViewModel
import kotlinx.android.synthetic.main.activity_event_detail.*

class EventDetailActivity : AppCompatActivity() {
    private lateinit var eventDetailViewModel: EventDetailViewModel
    private lateinit var eventPhoneNumber: String
    private lateinit var eventDetail: Event

    companion object {
        private const val EVENT_ID: String = "event_id"
        private val LOG_TAG: String = EventDetailActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)
        setSupportActionBar(detail_toolbar)

        val eventImage: ImageView = findViewById(R.id.detail_event_image)
        val eventDate: TextView = findViewById(R.id.detail_event_date)
        val eventTitle: TextView = findViewById(R.id.detail_event_title)
        val eventDescription: TextView = findViewById(R.id.detail_event_description)
        val ID: Int = intent.getIntExtra(EVENT_ID, 0)

        // Show the Up button in the action bar and hide the app name.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Get the ViewModel and observe the single event specified by the user click from
        // the list of events in MainActivity
        eventDetailViewModel = ViewModelProviders.of(this).get(EventDetailViewModel::class.java)
        eventDetailViewModel.getEvent(ID).observe(this, Observer { event ->
            event?.let {
                eventDetail = it
                eventPhoneNumber = it.phone ?: ""

                if (it.date != null) {
                    eventDate.text = it.getEventDateFormatString()
                } else {
                    eventDate.visibility = View.GONE
                }
                eventTitle.text = it.title
                eventDescription.text = it.description

                Glide.with(this)
                    .load(it.image)
                    .placeholder(R.drawable.placeholder_nomoon)
                    .error(R.drawable.placeholder_nomoon)
                    .centerCrop()
                    .into(eventImage)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail_menu, menu)
        if(!deviceCanCall() || eventPhoneNumber.isEmpty()) {
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
                eventDetail.callEventNumber(this, eventPhoneNumber)
                true
            }
            R.id.detail_action_share -> {
                eventDetail.shareEvent(this)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun deviceCanCall(): Boolean {
        return packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)
    }
}

