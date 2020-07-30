package com.glopez.phunapp.testutil

import com.glopez.phunapp.model.StarWarsEvent
import com.glopez.phunapp.model.db.EventDao
import java.lang.Exception

class EventDaoMock: EventDao {
    private val fakeEvents: ArrayList<StarWarsEvent> = ArrayList()

    override suspend fun getAllEvents(): List<StarWarsEvent> {
        return fakeEvents
    }

    override suspend fun insert(starWarsEvent: StarWarsEvent) {
        fakeEvents.add(starWarsEvent)
    }

    override suspend fun find(id: Int): StarWarsEvent =
        fakeEvents.find { event -> event.id == id } ?:
        throw NoSuchElementException("No element found with that id")
}