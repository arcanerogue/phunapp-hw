package com.glopez.phunapp.view

import com.glopez.phunapp.model.StarWarsEvent
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*

const val inputPattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
const val outputPattern = "MMMM dd, yyyy 'at' h:mm a"
val dateFormat = SimpleDateFormat(inputPattern, Locale.US)

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
    companion object {
        fun mapToUiModel(starWarsEvent: StarWarsEvent) : StarWarsUiEvent {
            val starWarsUiEvent = StarWarsUiEvent(
                id = starWarsEvent.id,
                description = starWarsEvent.description ?: "",
                title = starWarsEvent.title ?: "",
                imageUrl = starWarsEvent.image ?: "",
                phone = starWarsEvent.phone ?: "",
                date = createEventDateFormatString(starWarsEvent.date),
                location1 = starWarsEvent.location1 ?: "",
                location2 = starWarsEvent.location2 ?: ""
            )
            starWarsUiEvent.createShareEventMessage()
            return starWarsUiEvent
        }

        fun mapToUiModelList(starWarsEvents: List<StarWarsEvent>)
                : List<StarWarsUiEvent> {
            val uiEvents = emptyList<StarWarsUiEvent>() as MutableList<StarWarsUiEvent>
            for (event: StarWarsEvent in starWarsEvents) {
                uiEvents.add(mapToUiModel(event))
            }
            return uiEvents
        }

        private fun createEventDateFormatString(date: String?): String {
            if (date.isNullOrBlank()) {
                return date ?: ""
            } else {
                dateFormat.applyPattern(inputPattern)
                dateFormat.timeZone = TimeZone.getTimeZone("UTC")

                val formattedEventDate: Date = if (date.isNullOrBlank()) {
                    dateFormat.parse(
                        Calendar.getInstance()
                            .time.toString()
                    )
                } else {
                    dateFormat.parse((date))
                }

                dateFormat.applyPattern(outputPattern)
                dateFormat.timeZone = TimeZone.getDefault()
                return dateFormat.format(formattedEventDate)
            }
        }
    }

    private fun createShareEventMessage() {
        val shareString = StringBuilder()
        if(!title.isBlank()) {
            shareString.append("${title}\n")
        }
        if(!location1.isBlank()) {
            shareString.append("${location1}, ")
        }
        if(!location2.isBlank()) {
            shareString.append("${location2}\n")
        }
        if(!date.isBlank()) {
            shareString.append("${date}\n")
        }
        if(!description.isBlank()) {
            shareString.append(description)
        }
        this.shareMessage = shareString.toString()
    }
}