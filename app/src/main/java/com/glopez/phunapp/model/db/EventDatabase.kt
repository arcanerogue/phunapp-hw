package com.glopez.phunapp.model.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.glopez.phunapp.R
import com.glopez.phunapp.model.StarWarsEvent
import timber.log.Timber

@Database(entities = [StarWarsEvent::class], version = 1, exportSchema = false)
abstract class EventDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDao

        companion object {
        private var INSTANCE: EventDatabase? = null

        fun getDatabase(context: Context): EventDatabase {
            synchronized(this) {
                if(INSTANCE == null) {
                    // Create database
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        EventDatabase::class.java,
                        context.getString(R.string.database_name)
                    ).build()
                    Timber.d(context.getString(R.string.database_created))
                }
                return INSTANCE as EventDatabase
            }
        }
    }
}