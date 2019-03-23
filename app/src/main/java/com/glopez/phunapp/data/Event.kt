package com.glopez.phunapp.data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "events")
data class Event(
    @PrimaryKey
    @SerializedName("id")
    @Expose
    val id: Int,

    @SerializedName("description")
    @Expose
    val description: String?,

    @SerializedName("title")
    @Expose
    val title: String?,

    @SerializedName("timestamp")
    @Expose
    val timestamp: String?,

    @SerializedName("image")
    @Expose
    val image: String?,

    @SerializedName("phone")
    @Expose
    val phone: String?,

    @SerializedName("date")
    @Expose
    val date: String?,

    @SerializedName("locationline1")
    @Expose
    val location1: String?,

    @SerializedName("locationline2")
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