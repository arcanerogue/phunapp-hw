package com.glopez.phunapp.view.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import com.glopez.phunapp.model.StarWarsEvent
import com.glopez.phunapp.view.feed.FeedFragment
import timber.log.Timber
import java.lang.Exception

class EventRecyclerAdapter(private val context: Context,
                           private val feedFragmentListener: FeedFragment.FeedFragmentListener
) :
        RecyclerView.Adapter<EventRecyclerAdapter.ViewHolder>() {

    private var starWarsEventList: List<StarWarsEvent> = emptyList()
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = inflater
            .inflate(R.layout.item_event_list, parent, false)
        return ViewHolder(itemView, feedFragmentListener)
    }

    override fun getItemCount(): Int {
        return starWarsEventList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val starWarsEvent: StarWarsEvent = this.starWarsEventList[position]
        holder.setViewData(starWarsEvent)
    }

    fun setEvents(starWarsEvents: List<StarWarsEvent>) {
        this.starWarsEventList = starWarsEvents
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View?, listener: FeedFragment.FeedFragmentListener) : RecyclerView.ViewHolder(itemView) {
        private val roundedPlaceholderImage: RoundedBitmapDrawable
        private val starWarsEventListener = listener
        private val eventTitle = itemView?.findViewById<TextView>(R.id.event_title)
        private val eventLocation = itemView?.findViewById<TextView>(R.id.event_location)
        private val eventDescription = itemView?.findViewById<TextView>(R.id.event_description)
        private val eventImage = itemView?.findViewById<ImageView>(R.id.event_image)
        private val eventShareButton = itemView?.findViewById<Button>(R.id.share_button)
        private val eventCardView = itemView?.findViewById<CardView>(R.id.cardView)

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

        fun setViewData(event: StarWarsEvent) {
            this.eventTitle?.text = event.title
            this.eventLocation?.text = event.location1
            this.eventDescription?.text = event.description

            this.eventImage?.let {
                // This implementation will display the placeholder image as the event
                // image is fetched from the feed url.
                try {
                    Glide.with(context)
                        .load(event.image)
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

            eventShareButton?.setOnClickListener { starWarsEventListener.onShareClicked(event) }
            eventCardView?.setOnClickListener { starWarsEventListener.onFeedEventClicked(event) }
        }
    }
}