package com.glopez.phunapp.view.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v4.content.ContextCompat.startActivity
import android.support.v4.graphics.drawable.RoundedBitmapDrawable
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.glopez.phunapp.R
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.glopez.phunapp.model.Event
import com.glopez.phunapp.model.createShareEventMessage
import com.glopez.phunapp.utils.createShareIntent
import com.glopez.phunapp.view.activities.EventDetailActivity
import timber.log.Timber
import java.lang.Exception

private const val EVENT_ID: String = "event_id"

class EventRecyclerAdapter(private val context: Context) :
        RecyclerView.Adapter<EventRecyclerAdapter.ViewHolder>() {

    private var eventList: List<Event> = emptyList()
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val roundedPlaceholderImage: RoundedBitmapDrawable

    init {
        // Take the placeholder drawable and transform into a circular bitmap.
        // The Glide library will only apply transformations on the remotely requested resource,
        // so the placeholder image must be transformed before using Glide.
        val bitmapPlaceholder: Bitmap = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.placeholder_nomoon
        )
        roundedPlaceholderImage = RoundedBitmapDrawableFactory.create(
            context.resources,
            bitmapPlaceholder
        )
        roundedPlaceholderImage.isCircular = true
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = inflater
            .inflate(R.layout.item_event_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event: Event = this.eventList[position]

        holder.eventTitle?.text = event.title
        holder.eventLocation?.text = event.location1
        holder.eventDescription?.text = event.description

        holder.eventImage?.let {
            // This implementation will display the placeholder image as the event
            // image is fetched from the feed url.
            try {
                Glide.with(context)
                    .load(event.image)
//                    .onlyRetrieveFromCache(false)
                    .placeholder(this.roundedPlaceholderImage)
                    .error(this.roundedPlaceholderImage)
                    // This transformation applies to the remotely requested resource
                    .apply(RequestOptions.circleCropTransform())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(it)
            } catch (exception: Exception) {
                Timber.e(exception)
            }
        }

        holder.eventShareButton?.setOnClickListener {
            val shareMessage = event.createShareEventMessage()
            createShareIntent(context, shareMessage)
        }

        holder.eventCardView?.setOnClickListener {
            val detailIntent = Intent(context, EventDetailActivity::class.java)
//            val testId = 52.0
//            detailIntent.putExtra(EVENT_ID, testId)
//            detailIntent.putExtra(EVENT_ID, testId.toInt())
            detailIntent.putExtra(EVENT_ID, event.id)
            startActivity(context, detailIntent, null)
        }
    }

    fun setEvents(events: List<Event>) {
        this.eventList = events
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val eventTitle = itemView?.findViewById<TextView>(R.id.event_title)
        val eventLocation = itemView?.findViewById<TextView>(R.id.event_location)
        val eventDescription = itemView?.findViewById<TextView>(R.id.event_description)
        val eventImage = itemView?.findViewById<ImageView>(R.id.event_image)
        val eventShareButton = itemView?.findViewById<Button>(R.id.share_button)
        val eventCardView = itemView?.findViewById<CardView>(R.id.cardView)
    }
}