package com.glopez.phunapp.data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import android.support.v4.content.ContextCompat.startActivity
import android.text.TextUtils.isEmpty
import android.util.Log
import android.widget.Toast
import com.glopez.phunapp.R
import com.glopez.phunapp.ui.activities.EventDetailActivity
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
class Event(
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
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            val eventDate: Date = dateFormat.parse((this.date))
            val formatter = SimpleDateFormat("MMMM dd, yyyy 'at' h:mm a", Locale.getDefault())
            formatter.timeZone = TimeZone.getDefault()
            return formatter.format(eventDate)
        } else {
           return null
        }
    }

    private fun isIntentSafeToStart(context: Context,  intent: Intent): Boolean {
        val activities: List<ResolveInfo> = context.applicationContext
            .packageManager.queryIntentActivities(intent, 0)
        return activities.isNotEmpty()
    }

    fun shareEvent(context: Context) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            val message = context.getString(
                R.string.share_message, this@Event.title,
                this@Event.location1, this@Event.location2,
                this@Event.getEventDateFormatString(),
                this@Event.description)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }

        if (isIntentSafeToStart(context, shareIntent)) {
            startActivity(context, Intent.createChooser(shareIntent,
                context.getString(R.string.share_intent_title)), null)
        } else {
            Toast.makeText(context, context.getString(R.string.share_failed),
                Toast.LENGTH_LONG).show()
        }
    }

    fun callEventNumber(context: Context, phoneNumber: String) {
        if (phoneNumber.isEmpty()) {
            Toast.makeText(context, context.getString(R.string.event_detail_no_number),
                Toast.LENGTH_LONG).show()
        } else {
            val dialerIntent = Intent(Intent.ACTION_DIAL)
            dialerIntent.data = Uri.parse("tel:$phoneNumber")
            if (isIntentSafeToStart(context, dialerIntent)) {
                startActivity(context, dialerIntent, null)
            } else {
                Toast.makeText(context, context.getString(R.string.event_detail_unable_call),
                    Toast.LENGTH_LONG).show()
            }
        }
    }
}