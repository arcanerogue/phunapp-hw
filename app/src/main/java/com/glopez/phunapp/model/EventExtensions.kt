package com.glopez.phunapp.model

import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*


class EventExtensions {
    fun createEventDateFormatString(event: Event) {
        event.createEventDateFormatString()
    }

    fun createShareEventMessage(event: Event) {
        event.createShareEventMessage()
    }
}

fun Event.createEventDateFormatString(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    val formattedEventDate: Date

    formattedEventDate = if(this.date.isNullOrEmpty()) {
        dateFormat.parse(Calendar.getInstance()
            .time.toString())
    } else {
        dateFormat.parse((this.date))
    }

    val formatter = SimpleDateFormat("MMMM dd, yyyy 'at' h:mm a", Locale.getDefault())
    formatter.timeZone = TimeZone.getDefault()
    return formatter.format(formattedEventDate)
}

fun Event.createShareEventMessage(): String {
    val shareString = StringBuilder()
    if(!this.title.isNullOrEmpty()) {
        shareString.append(this.title)
        shareString.appendln()
    }
    if(!this.location1.isNullOrEmpty()) {
        shareString.append("${this.location1}, ")
    }
    if(!this.location2.isNullOrEmpty()) {
        shareString.append(this.location2)
        shareString.appendln()
    }
    if(!this.date.isNullOrEmpty()) {
        shareString.append(this.createEventDateFormatString())
        shareString.appendln()
    }
    if(!this.description.isNullOrEmpty()) {
        shareString.append(this.description)
    }
    return shareString.toString()
}
