package com.glopez.phunapp.ui.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.glopez.phunapp.R
import com.glopez.phunapp.ui.viewmodels.EventViewModel
import com.squareup.picasso.Picasso

class EventDetailActivity : AppCompatActivity() {
    private lateinit var eventViewModel: EventViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)

        val eventImage: ImageView = findViewById(R.id.detail_event_image)
        val eventTitle: TextView = findViewById(R.id.detail_event_title)
        val eventDescription: TextView = findViewById(R.id.detail_event_description)

        val ID: Int = intent.getIntExtra("event_id", 0)

        eventViewModel = ViewModelProviders.of(this).get(EventViewModel::class.java)
        eventViewModel.getEvent(ID).observe(this, Observer { event ->
            event?.let {
                eventTitle.text = event.title
                eventDescription.text = event.description

                // This implementation will display the placeholder image as the event
                // image is fetched from the feed url.
//                Picasso.get()
//                    .load(event.image)
//                    .placeholder(R.drawable.placeholder_nomoon)
//                    .error(R.drawable.placeholder_nomoon)
//                    .into(eventImage)

                Glide.with(this)
                    .load(event.image)
                    .placeholder(R.drawable.placeholder_nomoon)
                    .error(R.drawable.placeholder_nomoon)
                    .centerCrop()
                    .into(eventImage)
            }
        })
    }
}
