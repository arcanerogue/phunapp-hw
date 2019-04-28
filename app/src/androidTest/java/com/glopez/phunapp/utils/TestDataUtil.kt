package com.glopez.phunapp.utils

import com.glopez.phunapp.data.Event

object TestDataUtil {

    fun createEvent(index: Int) = Event(
        id = index,
        title = "TestEvent$index",
        description = "Description$index",
        timestamp = "Timestamp$index",
        image = "ImageUrl$index",
        phone = "Phone$index",
        date = "Date$index",
        location1 = "Location1_$index",
        location2 = "Location2_$index"
    )

    fun createListOfEvents(count: Int): List<Event> {
        val testEventList: MutableList<Event> = mutableListOf<Event>()
        for(i in 1..count) {
            testEventList.add(createEvent(i))
        }
        return testEventList.toList()
    }
}