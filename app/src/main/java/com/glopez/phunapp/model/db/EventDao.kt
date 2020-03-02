package com.glopez.phunapp.model.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.glopez.phunapp.model.StarWarsEvent

@Dao
interface EventDao {
    // Select All
    @Query("SELECT * FROM events")
    suspend fun getAllEvents(): List<StarWarsEvent>

    // Insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(starWarsEvent: StarWarsEvent)

    // Select One By Id
    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun find(id: Int): StarWarsEvent
}