package com.glopez.phunapp.ui.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v4.content.ContextCompat.startActivity
import android.support.v4.graphics.drawable.RoundedBitmapDrawable
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.glopez.phunapp.R
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.glopez.phunapp.data.Event
import com.glopez.phunapp.ui.activities.EventDetailActivity

class EventRecyclerAdapter (private val context: Context) :
        RecyclerView.Adapter<EventRecyclerAdapter.ViewHolder>() {

    private val LOG_TAG = EventRecyclerAdapter::class.java.simpleName
    var eventList = emptyList<Event>()
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val roundedPlaceholderImage: RoundedBitmapDrawable

    init {
        // Take the placeholder drawable and transform into a circular bitmap.
        // The Glide library will only apply transformations on the requested resource,
        // so the placeholder image must be transformed before using Glide
        val bitmapPlaceholder: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.placeholder_nomoon)
        roundedPlaceholderImage = RoundedBitmapDrawableFactory.create(context.resources, bitmapPlaceholder)
        roundedPlaceholderImage.isCircular = true
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = inflater
            .inflate(R.layout.item_event_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = this.eventList[position]

        holder.eventTitle?.text = event.title
        holder.eventLocation?.text = event.location1
        holder.eventDescription?.text = event.description

        holder.eventImage?.let {
            // This implementation will display the placeholder image as the event
            // image is fetched from the feed url.
            Glide.with(context)
                .load(event.image)
                .placeholder(this.roundedPlaceholderImage)
                .error(this.roundedPlaceholderImage)
                .apply(RequestOptions.circleCropTransform()) // This transformation applies to the requested resource
                .into(it)
        }
    }

    fun setEvents(events: List<Event>) {
        this.eventList = events
        notifyDataSetChanged()
        Log.d(LOG_TAG, "Updating events from adapter")
    }

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        init {
            itemView?.setOnClickListener(this)
        }

        val eventTitle = itemView?.findViewById<TextView>(R.id.event_title)
        val eventLocation = itemView?.findViewById<TextView>(R.id.event_location)
        val eventDescription = itemView?.findViewById<TextView>(R.id.event_description)
        val eventImage = itemView?.findViewById<ImageView>(R.id.event_image)

        override fun onClick(v: View?) {
            val position = adapterPosition
            val event = eventList[position]
            val detailIntent = Intent(context, EventDetailActivity::class.java )
            detailIntent.putExtra("event_id", event.id)
            startActivity(context, detailIntent, null)
        }

    }

}