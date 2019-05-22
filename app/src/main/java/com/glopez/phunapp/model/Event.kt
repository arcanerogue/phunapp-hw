package com.glopez.phunapp.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

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
    val location2: String?)