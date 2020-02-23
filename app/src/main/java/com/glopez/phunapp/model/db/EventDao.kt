package com.glopez.phunapp.model.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.glopez.phunapp.model.StarWarsEvent

@Dao
interface EventDao {
    // Select All
    @Query("SELECT * FROM events")
    fun getAllEvents(): LiveData<List<StarWarsEvent>>

    // Insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(starWarsEvent: StarWarsEvent)

    // Select One By Id
    @Query("SELECT * FROM events WHERE id = :id")
    fun find(id: Int): LiveData<StarWarsEvent>
}