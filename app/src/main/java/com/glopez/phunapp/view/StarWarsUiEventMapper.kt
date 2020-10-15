package com.glopez.phunapp.view

import com.glopez.phunapp.model.StarWarsEvent
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*

const val inputPattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
const val outputPattern = "MMMM dd, yyyy 'at' h:mm a"
val dateFormat = SimpleDateFormat(inputPattern, Locale.US)

class StarWarsUiEventMapper {

    fun mapToModel(starWarsEvent: StarWarsEvent): StarWarsUiEvent {
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
        createShareEventMessage(starWarsUiEvent)
        return starWarsUiEvent
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

    private fun createShareEventMessage(uiModel: StarWarsUiEvent) {
        val shareString = StringBuilder()
        if (!uiModel.title.isBlank()) {
            shareString.append("${uiModel.title}\n")
        }
        if (!uiModel.location1.isBlank()) {
            shareString.append("${uiModel.location1}, ")
        }
        if (!uiModel.location2.isBlank()) {
            shareString.append("${uiModel.location2}\n")
        }
        if (!uiModel.date.isBlank()) {
            shareString.append("${uiModel.date}\n")
        }
        if (!uiModel.description.isBlank()) {
            shareString.append(uiModel.description)
        }
        uiModel.shareMessage = shareString.toString()
    }
}