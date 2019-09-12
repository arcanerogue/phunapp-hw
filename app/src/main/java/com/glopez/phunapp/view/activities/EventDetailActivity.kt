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
import com.glopez.phunapp.view.viewmodels.ViewModelFactory
import com.glopez.phunapp.constants.DB_MISSING_ID_VALUE
import com.glopez.phunapp.model.StarWarsEvent
import com.glopez.phunapp.model.createEventDateFormatString
import com.glopez.phunapp.model.createShareEventMessage
import com.glopez.phunapp.model.db.Resource
import com.glopez.phunapp.utils.*
import com.glopez.phunapp.view.viewmodels.DetailViewModel
import kotlinx.android.synthetic.main.activity_event_detail.*
import timber.log.Timber
import java.lang.Exception

private const val EVENT_ID: String = "event_id"

class EventDetailActivity : AppCompatActivity() {
    private lateinit var detailViewModel: DetailViewModel
    private var eventPhoneNumber: String = ""
    private var eventShareMessage: String = ""
    private var hideMenuOptions: Boolean = false
    private var canCall: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)
        setSupportActionBar(detail_toolbar)

        canCall = deviceCanCall(this.applicationContext.packageManager)
        val eventDetailId: Int = intent?.extras?.getInt(EVENT_ID) ?: DB_MISSING_ID_VALUE

        // Show the Up button in the action bar and hide the app name.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        detailViewModel = ViewModelProviders.of(this, ViewModelFactory
                .getInstance(application as PhunApp))
            .get(DetailViewModel::class.java)

        observeEventDetail(eventDetailId)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail_menu, menu)
        if(eventPhoneNumber.isEmpty() || !canCall) {
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
//            R.id.detail_action_call -> {
//                createCallIntent(this, eventPhoneNumber)
//                true
//            }
            R.id.detail_action_call -> {
                IntentFactory.create(this, IntentCategory.CALL, eventPhoneNumber)
                true
            }
//            R.id.detail_action_share -> {
//                createShareIntent(this, eventShareMessage)
//                true
//            }
            R.id.detail_action_share -> {
                IntentFactory.create(this, IntentCategory.SHARE, eventShareMessage)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun observeEventDetail(eventId: Int) {
        detailViewModel.getEventDetailResource(eventId).observe(this, Observer { event ->
            event?.let {
                when (event) {
                    is Resource.Error -> handleViewsOnError(event.error)
                    is Resource.Success -> handleViewsOnSuccess(event.data)
                }
            }
        })
    }

    /**
     * Displays an "empty" View when the database cannot locate an StarWarsEvent to display to the user
     */
    private fun handleViewsOnError(error: Throwable?) {
        // Collapses the app bar
        app_bar.setExpanded(false)
        hideMenuOptions = true

        // Recreate the options menu so the toolbar icons will be hidden
        invalidateOptionsMenu()
        nested_scroll_view_group.visibility = View.GONE
        Toast.makeText(this@EventDetailActivity, "Unable to locate StarWarsEvent details.", Toast.LENGTH_LONG)
            .show()
        Timber.e(error)
    }

    private fun handleViewsOnSuccess(starWarsEvent: StarWarsEvent?) {
        if (starWarsEvent != null) {
            eventPhoneNumber = starWarsEvent.phone ?: ""

            if (starWarsEvent.date != null)
                detail_event_date.text = starWarsEvent.createEventDateFormatString()
            else
                detail_event_date.visibility = View.GONE

            eventShareMessage = starWarsEvent.createShareEventMessage()
            detail_event_title.text = starWarsEvent.title
            detail_event_location.text = starWarsEvent.location2
            detail_event_description.text = starWarsEvent.description

            Glide.with(this@EventDetailActivity)
                .load(starWarsEvent.image)
                .onlyRetrieveFromCache(true)
                .error(R.drawable.placeholder_nomoon)
                .centerCrop()
                .into(detail_event_image)
        } else {
            handleViewsOnError(Exception("The database returned a null StarWarsEvent object."))
        }
    }
}

