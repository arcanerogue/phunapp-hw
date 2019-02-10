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
        holder.eventImage
        Picasso.get()
            .load(event.image)
            .placeholder(R.mipmap.ic_launcher_round)
            .error(R.mipmap.ic_launcher_round)
            .resize(72, 72)
            .centerCrop()
            .into(holder.eventImage)
    }

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val eventTitle = itemView?.findViewById<TextView>(R.id.event_title)
        val eventLocation = itemView?.findViewById<TextView>(R.id.event_location)
        val eventDescription = itemView?.findViewById<TextView>(R.id.event_description)
        val eventImage = itemView?.findViewById<ImageView>(R.id.event_image)
    }

}