package com.glopez.phunapp.data.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import android.util.Log
import com.glopez.phunapp.R
import com.glopez.phunapp.data.Event

@Database(entities = [Event::class], version = 1)
abstract class EventDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDao

        companion object {
        private val LOG_TAG = EventDatabase::class.java.simpleName
        private var INSTANCE: EventDatabase? = null

        fun getDatabase(context: Context): EventDatabase {
            synchronized(this) {
                if(INSTANCE == null) {
                    // Create database
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        EventDatabase::class.java,
                        context.getString(R.string.database_name)
                    )
                        .build()
                    Log.d(LOG_TAG, context.getString(R.string.database_created))
                }
                return INSTANCE!!
            }
        }
    }
}