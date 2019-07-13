package com.glopez.phunapp.view.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.glopez.phunapp.PhunApp
import com.glopez.phunapp.R
import com.glopez.phunapp.ViewModelFactory
import com.glopez.phunapp.model.createEventDateFormatString
import com.glopez.phunapp.model.createShareEventMessage
import com.glopez.phunapp.utils.*
import com.glopez.phunapp.view.viewmodels.EventDetailViewModel
import kotlinx.android.synthetic.main.activity_event_detail.*
import timber.log.Timber

private const val EVENT_ID: String = "event_id"

class EventDetailActivity : AppCompatActivity() {
    private lateinit var eventDetailViewModel: EventDetailViewModel
    private var eventPhoneNumber: String = ""
    private var eventShareMessage: String = ""
    private var hideMenuOptions: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)
        setSupportActionBar(detail_toolbar)

        val eventDetailId: Int = intent?.extras?.getInt(EVENT_ID) ?: 0

        // Show the Up button in the action bar and hide the app name.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Get the ViewModel and observe the single event specified by the user click from
        // the list of events in MainActivity
        eventDetailViewModel = ViewModelProviders.of(this, ViewModelFactory
                .getInstance(application as PhunApp))
            .get(EventDetailViewModel::class.java)

        observeEventDetail(eventDetailId)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail_menu, menu)
        val packageManager = this.applicationContext.packageManager
        val canCall: Boolean = deviceCanCall(packageManager)
        if(eventPhoneNumber.isEmpty() || !canCall) {
//        val canCall: Boolean = Utils.deviceCanCall(this.applicationContext)
//        if (eventPhoneNumber.isBlank() || !canCall) {
            val callIcon: MenuItem? = menu?.findItem(R.id.detail_action_call)
            callIcon?.isVisible = false
        }

        if(hideMenuOptions) {
            val shareIcon: MenuItem? = menu?.findItem(R.id.detail_action_share)
            shareIcon?.isVisible = false
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

    private fun observeEventDetail(eventId: Int) {
        eventDetailViewModel.getEvent(eventId).observe(this, Observer { event ->
            event.let {
                if (it != null && it.id > 0) {
                    eventPhoneNumber = it.phone ?: ""

                    if (it.date != null)
                        detail_event_date.text = it.createEventDateFormatString()
                    else
                        detail_event_date.visibility = View.GONE

                    eventShareMessage = it.createShareEventMessage()
                    detail_event_title.text = it.title
                    detail_event_location.text = it.location2
                    detail_event_description.text = it.description

                    Glide.with(this@EventDetailActivity)
                        .load(it.image)
                        .onlyRetrieveFromCache(true)
                        .error(R.drawable.placeholder_nomoon)
                        .centerCrop()
                        .into(detail_event_image)
                } else {
                    handleViewsOnError()
                }
            }
        })
    }

    /**
     * Displays an "empty" View when the database cannot locate an Event to display to the user
     */
    private fun handleViewsOnError() {
        // Collapses the app bar
        app_bar.setExpanded(false)
        hideMenuOptions = true

        // Recreate the options menu so the toolbar icons will be hidden
        invalidateOptionsMenu()
        nested_scroll_view_group.visibility = View.GONE
        Toast.makeText(this@EventDetailActivity, "Unable to locate Event details.", Toast.LENGTH_LONG)
            .show()
        Timber.e("Invalid EVENT_ID passed to EventDetailActivity.")
    }
}

