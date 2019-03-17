package com.glopez.phunapp.ui.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.glopez.phunapp.R
import com.glopez.phunapp.ui.viewmodels.EventViewModel
import java.text.SimpleDateFormat
import java.util.*

class EventDetailActivity : AppCompatActivity() {
    private lateinit var eventViewModel: EventViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)

        val eventImage: ImageView = findViewById(R.id.detail_event_image)
        val eventDate: TextView = findViewById(R.id.detail_event_date)
        val eventTitle: TextView = findViewById(R.id.detail_event_title)
        val eventDescription: TextView = findViewById(R.id.detail_event_description)

        val ID: Int = intent.getIntExtra("event_id", 0)

        eventViewModel = ViewModelProviders.of(this).get(EventViewModel::class.java)
        eventViewModel.getEvent(ID).observe(this, Observer { event ->
            event?.let {
                if (it.date != null)
                {
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
            }
        })
    }

    private fun Date.toFormatString() : String? {
        val formatter = SimpleDateFormat("MMMM dd, yyyy 'at' h:mm a", Locale.getDefault())
        formatter.timeZone = TimeZone.getDefault()
        return formatter.format(this)
    }
}
