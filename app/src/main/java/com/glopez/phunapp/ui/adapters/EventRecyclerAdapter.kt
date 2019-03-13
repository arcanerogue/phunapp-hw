package com.glopez.phunapp.ui.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.glopez.phunapp.R
import android.widget.TextView
import com.glopez.phunapp.data.Event
import com.squareup.picasso.Picasso

class EventRecyclerAdapter (context: Context) :
        RecyclerView.Adapter<EventRecyclerAdapter.ViewHolder>() {

    private val LOG_TAG = EventRecyclerAdapter::class.java.simpleName
    private var eventList = emptyList<Event>()
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = inflater
            .inflate(R.layout.item_event_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = eventList[position]

        holder.eventTitle?.text = event.title
        holder.eventLocation?.text = event.location1
        holder.eventDescription?.text = event.description

        // This implementation will display the placeholder image as the event
        // image is fetched from the feed url.
        Picasso.get()
            .load(event.image)
            .placeholder(R.drawable.placeholder_nomoon)
            .error(R.drawable.placeholder_nomoon)
            .resize(72, 72)
            .centerCrop()
            .into(holder.eventImage)
    }

    fun setEvents(events: List<Event>) {
        this.eventList = events
        notifyDataSetChanged()
        Log.d(LOG_TAG, "Updating events from adapter")
    }


    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val eventTitle = itemView?.findViewById<TextView>(R.id.event_title)
        val eventLocation = itemView?.findViewById<TextView>(R.id.event_location)
        val eventDescription = itemView?.findViewById<TextView>(R.id.event_description)
        val eventImage = itemView?.findViewById<ImageView>(R.id.event_image)
    }

}