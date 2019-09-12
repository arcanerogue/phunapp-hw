package com.glopez.phunapp.model

import timber.log.Timber
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*

const val inputPattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
const val outputPattern = "MMMM dd, yyyy 'at' h:mm a"
val dateFormat = SimpleDateFormat(inputPattern, Locale.US)

fun StarWarsEvent.createEventDateFormatString(): String {
    dateFormat.applyPattern(inputPattern)
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")

    val formattedEventDate: Date = if (this.date.isNullOrBlank()) {
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

fun StarWarsEvent.createShareEventMessage(): String {
    val shareString = StringBuilder()
    if(!this.title.isNullOrBlank()) {
        shareString.append("${this.title}\n")
    }
    if(!this.location1.isNullOrBlank()) {
        shareString.append("${this.location1}, ")
    }
    if(!this.location2.isNullOrBlank()) {
        shareString.append("${this.location2}\n")
    }
    if(!this.date.isNullOrBlank()) {
        shareString.append("${this.createEventDateFormatString()}\n")
    }
    if(!this.description.isNullOrBlank()) {
        shareString.append("${this.description}")
    }
    return shareString.toString()
}
