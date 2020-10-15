package com.glopez.phunapp.view


data class StarWarsUiEvent(
    val id: Int,
    val description: String,
    val title: String,
    val imageUrl: String,
    val phone: String,
    val date: String,
    val location1: String,
    val location2: String,
    var shareMessage: String = ""
) {

    override fun equals(other: Any?): Boolean {
        if (this.javaClass != other?.javaClass) {
            return false
        }

        other as StarWarsUiEvent
        return this.id == other.id
                && this.description == other.description
                && this.imageUrl == other.imageUrl
                && this.phone == other.phone
                && this.date == other.date
                && this.location1 == other.location1
                && this.location2 == other.location2
                && this.shareMessage == other.shareMessage
    }

    override fun hashCode(): Int {
        return this.id
    }
}