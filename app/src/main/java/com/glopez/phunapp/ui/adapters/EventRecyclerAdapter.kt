package com.glopez.phunapp.ui.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.glopez.phunapp.R
import android.widget.TextView
import com.glopez.phunapp.data.Event
import com.squareup.picasso.Picasso

class EventRecyclerAdapter(private val eventList: List<Event>) :
        RecyclerView.Adapter<EventRecyclerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
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

        // If the image property from the feed provides an image url,
        // the image will be assigned to the ViewHolder.
        if(event.image != null) {
            Picasso.get()
                .load(event.image)
                .error(R.drawable.placeholder_nomoon)
                .resize(72, 72)
                .centerCrop()
                .into(holder.eventImage)
        }

        // Setting the placeholder image here instead of in the Picasso builder
        // statement prevents the user from seeing the placeholder image as the image
        // is fetched from the feed url.
        else {
            holder.eventImage?.setImageResource(R.drawable.placeholder_nomoon)
        }

        // This implementation will display the placeholder image as the event
        // image is fetched from the feed url.
//        Picasso.get()
//            .load(event.image)
//            .placeholder(R.drawable.placeholder_nomoon)
//            .error(R.drawable.placeholder_nomoon)
//            .resize(72, 72)
//            .centerCrop()
//            .into(holder.eventImage)
    }


    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val eventTitle = itemView?.findViewById<TextView>(R.id.event_title)
        val eventLocation = itemView?.findViewById<TextView>(R.id.event_location)
        val eventDescription = itemView?.findViewById<TextView>(R.id.event_description)
        val eventImage = itemView?.findViewById<ImageView>(R.id.event_image)
    }

}