package com.glopez.phunapp.ui.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.glopez.phunapp.R
import com.glopez.phunapp.data.Event
import com.glopez.phunapp.ui.viewmodels.EventDetailViewModel
import com.glopez.phunapp.ui.viewmodels.EventViewModel
import kotlinx.android.synthetic.main.activity_event_detail.*
import java.text.SimpleDateFormat
import java.util.*

class EventDetailActivity : AppCompatActivity() {
    private lateinit var eventDetailViewModel: EventDetailViewModel
    private val EVENT_ID: String = "event_id"
    private var eventPhoneNumber: String? = ""
    private lateinit var eventDetail: Event

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)
        setSupportActionBar(detail_toolbar)

        // Show the Up button in the action bar and
        // hide the app name.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)


        val eventImage: ImageView = findViewById(R.id.detail_event_image)
        val eventDate: TextView = findViewById(R.id.detail_event_date)
        val eventTitle: TextView = findViewById(R.id.detail_event_title)
        val eventDescription: TextView = findViewById(R.id.detail_event_description)

        val ID: Int = intent.getIntExtra(EVENT_ID, 0)

        eventDetailViewModel = ViewModelProviders.of(this).get(EventDetailViewModel::class.java)
        eventDetailViewModel.getEvent(ID).observe(this, Observer { event ->
            event?.let {
                if (it.date != null) {
                    eventDate.text = it.getEventDate()?.toFormatString()
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

                eventPhoneNumber = it.phone
                eventDetail = it
            }
        })
    }

    private fun Date.toFormatString(): String? {
        val formatter = SimpleDateFormat("MMMM dd, yyyy 'at' h:mm a", Locale.getDefault())
        formatter.timeZone = TimeZone.getDefault()
        return formatter.format(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                navigateUpTo(Intent(this, MainActivity::class.java))
                true
            }
            R.id.detail_action_call -> {
                if (eventPhoneNumber.isNullOrEmpty()) {
                    Toast.makeText(this, "There is no number for this event.", Toast.LENGTH_LONG).show()
                } else {
                    val dialerIntent = Intent(Intent.ACTION_DIAL)
                    dialerIntent.data = Uri.parse("tel:$eventPhoneNumber")
                    if (isIntentSafeToStart(dialerIntent)) {
                        startActivity(dialerIntent)
                    } else {
                        Log.d("EventDetailActivity", "Can't handle phone call!")
                        Toast.makeText(this, "Unable to place a call.", Toast.LENGTH_LONG).show()
                    }
                }
                true
            }
            R.id.detail_action_share -> {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    val shareMessage: String = "${eventDetail.title}\n${eventDetail.location1}, " +
                            "${eventDetail.location2}\n${eventDetail.date}\n${eventDetail.description}"
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    putExtra(Intent.EXTRA_TEXT, shareMessage)
                    type = "text/plain"
                }
                if (isIntentSafeToStart(shareIntent)) {
                    startActivity(Intent.createChooser(shareIntent, "Send with"))
                } else {
                    Toast.makeText(this, "Unable to share this event.", Toast.LENGTH_LONG).show()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }


    private fun isIntentSafeToStart(intent: Intent): Boolean {
        val activities: List<ResolveInfo> = packageManager.queryIntentActivities(intent, 0)
        return activities.isNotEmpty()
    }
}

