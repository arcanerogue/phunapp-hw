package com.glopez.phunapp.data

import android.content.Context
import com.glopez.phunapp.R
import java.text.SimpleDateFormat
import java.util.*

object EventUtils {

    fun getEventDateFormatString(eventDate: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val formattedEventDate: Date

        formattedEventDate = if(eventDate.isEmpty()) {
            dateFormat.parse(Calendar.getInstance()
                .time.toString())
        } else {
            dateFormat.parse((eventDate))
        }

        val formatter = SimpleDateFormat("MMMM dd, yyyy 'at' h:mm a", Locale.getDefault())
        formatter.timeZone = TimeZone.getDefault()
        return formatter.format(formattedEventDate)
    }

    fun createShareEventMessage(context: Context, event: Event): String {
        val eventDate = event.date ?: ""

        return context.getString(
            R.string.share_message, event.title,
            event.location1, event.location2,
            getEventDateFormatString(eventDate),
            event.description)
    }
}