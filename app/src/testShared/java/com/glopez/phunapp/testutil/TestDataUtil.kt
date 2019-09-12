package com.glopez.phunapp.testutil

import com.glopez.phunapp.model.StarWarsEvent

object TestDataUtil {

    fun createEvent(index: Int) = StarWarsEvent(
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

    fun createListOfEvents(count: Int): List<StarWarsEvent> {
        val testStarWarsEventList: MutableList<StarWarsEvent> = mutableListOf()
        for(i in 1..count) {
            testStarWarsEventList.add(createEvent(i))
        }
        return testStarWarsEventList.toList()
    }
}