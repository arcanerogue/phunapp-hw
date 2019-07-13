package com.glopez.phunapp.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true)
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
    val location2: String?)