package com.glopez.phunapp.testutil

import com.glopez.phunapp.model.StarWarsEvent
import com.glopez.phunapp.model.repository.FeedRepository

class FeedRepositoryMock : FeedRepository {
    private val fakeEvents: ArrayList<StarWarsEvent> = ArrayList()

    override suspend fun getEvents(): List<StarWarsEvent> {
        return fakeEvents
    }

    override suspend fun getEventById(id: Int): StarWarsEvent =
        fakeEvents.find { event -> event.id  == id }
            ?: throw NoSuchElementException("No element found with that id")

    override suspend fun updateEvents() {
        return // no-op
    }

    override suspend fun insertEventsIntoDatabase(starWarsEvents: List<StarWarsEvent>) {
        fakeEvents.addAll(starWarsEvents)
    }
}