package com.glopez.phunapp.model

import timber.log.Timber
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*

fun Event.createEventDateFormatString(): String {
    val inputPattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    val outputPattern = "MMMM dd, yyyy 'at' h:mm a"
    val dateFormat = SimpleDateFormat(inputPattern, Locale.getDefault())

    dateFormat.timeZone = TimeZone.getTimeZone("UTC")

    val formattedEventDate: Date = if (this.date.isNullOrEmpty()) {
        dateFormat.parse(Calendar.getInstance()
            .time.toString())
    } else {
        dateFormat.parse((this.date))
    }

    dateFormat.applyPattern(outputPattern)
    dateFormat.timeZone = TimeZone.getDefault()
    Timber.d("SimpleDateFormat has hashCode: ${dateFormat.hashCode()}")
    return dateFormat.format(formattedEventDate)
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
