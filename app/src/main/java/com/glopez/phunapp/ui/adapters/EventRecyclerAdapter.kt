package com.glopez.phunapp.ui.adapters

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.glopez.phunapp.R
import android.widget.TextView
import com.bumptech.glide.Glide
import com.glopez.phunapp.data.Event
import com.glopez.phunapp.ui.activities.EventDetailActivity
import com.squareup.picasso.Picasso

class EventRecyclerAdapter (private val context: Context) :
        RecyclerView.Adapter<EventRecyclerAdapter.ViewHolder>() {

    private val LOG_TAG = EventRecyclerAdapter::class.java.simpleName
    var eventList = emptyList<Event>()
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
//        Picasso.get()
//            .load(event.image)
//            .placeholder(R.drawable.placeholder_nomoon)
//            .error(R.drawable.placeholder_nomoon)
//            .resize(72, 72)
//            .centerCrop()
//            .into(holder.eventImage)


        Glide.with(context)
            .load(event.image)
            .placeholder(R.drawable.placeholder_nomoon)
            .error(R.drawable.placeholder_nomoon)
            .override(72,72)
            .centerCrop()
            .into(holder.eventImage)
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