package com.glopez.phunapp.data.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.glopez.phunapp.data.Event

@Dao
interface EventDao {
    // Select All
    @Query("SELECT * FROM events")
    fun getAllEvents(): List<Event>

    // Insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(event: Event)
}