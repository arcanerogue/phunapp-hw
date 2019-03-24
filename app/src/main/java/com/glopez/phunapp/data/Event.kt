package com.glopez.phunapp.data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

const val TABLE_NAME: String = "events"
const val DESCRIPTION: String = "description"
const val ID: String = "id"
const val TITLE: String = "title"
const val TIMESTAMP: String = "timestamp"
const val IMAGE = "image"
const val PHONE = "phone"
const val DATE = "date"
const val LOCATION_LINE_1 = "locationline1"
const val LOCATION_LINE_2 = "locationline2"

@Entity(tableName = TABLE_NAME)
data class Event(
    @PrimaryKey
    @SerializedName(ID)
    @Expose
    val id: Int,

    @SerializedName(DESCRIPTION)
    @Expose
    val description: String?,

    @SerializedName(TITLE)
    @Expose
    val title: String?,

    @SerializedName(TIMESTAMP)
    @Expose
    val timestamp: String?,

    @SerializedName(IMAGE)
    @Expose
    val image: String?,

    @SerializedName(PHONE)
    @Expose
    val phone: String?,

    @SerializedName(DATE)
    @Expose
    val date: String?,

    @SerializedName(LOCATION_LINE_1)
    @Expose
    val location1: String?,

    @SerializedName(LOCATION_LINE_2)
    @Expose
    val location2: String?) {

    fun getEventDateFormatString(): String? {
        if (this.date != null) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            dateFormat.timeZone = TimeZone.getDefault()
            val eventDate: Date = dateFormat.parse((this.date))
            val formatter = SimpleDateFormat("MMMM dd, yyyy 'at' h:mm a", Locale.getDefault())
            return formatter.format(eventDate)
        } else {
           return null
        }
    }
}