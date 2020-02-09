package com.glopez.phunapp.model.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
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