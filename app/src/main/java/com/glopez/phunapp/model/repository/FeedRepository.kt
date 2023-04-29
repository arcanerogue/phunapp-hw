package com.glopez.phunapp.model.repository

import com.glopez.phunapp.model.StarWarsEvent

interface FeedRepository {
    suspend fun getEvents(): List<StarWarsEvent>
    suspend fun getEvent(id: Int): StarWarsEvent?
    suspend fun updateEvents()
    suspend fun saveEvents(starWarsEvents: List<StarWarsEvent>)
}