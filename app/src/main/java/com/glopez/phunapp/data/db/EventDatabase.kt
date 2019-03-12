package com.glopez.phunapp.data.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.glopez.phunapp.data.Event

@Database(entities = [Event::class], version = 1)
abstract class EventDatabase: RoomDatabase() {

    abstract fun eventDao(): EventDao

    companion object {
        private var INSTANCE: EventDatabase? = null

        fun getDatabase(context: Context): EventDatabase? {
            return INSTANCE ?: synchronized(this) {
                // Create database
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EventDatabase::class.java,
                    "event_database"
                )
                    .allowMainThreadQueries()
//                        .addCallback(object : RoomDatabase.Callback() {
//                            override fun onCreate(db: SupportSQLiteDatabase) {
//                                super.onCreate(db)
//                                INSTANCE?.let {
//                                    EventDatabase.populateDb(it, )
//                                }
//                            }
//                        })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

//        fun populateDb(database: EventDatabase, eventList: List<Event>) {
//            for(event in eventList) {
//                AsyncTask.execute {database.eventDao().insert(event)}
//            }
//        }


//.addCallback(object: RoomDatabase.Callback() {
//    override fun onCreate(db: SupportSQLiteDatabase) {
//        super.onCreate(db)
//        INSTANCE?.let {
//            PeopleDatabase.prePopulate(it, PeopleInfoProvider.peopleList )
//        }
//        Log.d(LOG_TAG, "Populated database.")
//    }
//})
//.build()
//Log.d(LOG_TAG,"DATABASE CREATED")
//}
//return INSTANCE!!
//}
//}
//
//fun prePopulate(database: PeopleDatabase, peopleList: List<Contacts.People>) {
//    for(people in peopleList) {
//        AsyncTask.execute{ database.peopleDao().insert(people) }
//    }
//}
