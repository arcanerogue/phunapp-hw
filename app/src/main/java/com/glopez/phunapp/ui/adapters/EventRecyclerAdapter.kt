package com.glopez.phunapp.ui.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.glopez.phunapp.R
import android.widget.TextView
import com.glopez.phunapp.data.Event

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
    }


    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val eventTitle = itemView?.findViewById<TextView>(R.id.event_title)
        val eventLocation = itemView?.findViewById<TextView>(R.id.event_location)
    }

}