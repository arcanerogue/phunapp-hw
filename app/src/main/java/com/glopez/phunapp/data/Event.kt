package com.glopez.phunapp.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Event(
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