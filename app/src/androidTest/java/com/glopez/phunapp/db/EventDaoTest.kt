package com.glopez.phunapp.db

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.glopez.phunapp.utils.LiveDataTestUtil
import com.glopez.phunapp.utils.TestDataUtil
import com.glopez.phunapp.data.db.EventDao
import com.glopez.phunapp.data.db.EventDatabase
import junit.framework.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class EventDaoTest {

    @Rule
    @JvmField
    var instantTaskExecutor = InstantTaskExecutorRule()

    private lateinit var eventDatabase: EventDatabase
    private lateinit var eventDao: EventDao

    @Before
    fun initializeDb() {
        eventDatabase = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getContext(),
            EventDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        eventDao = eventDatabase.eventDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        eventDatabase.close()
    }

    @Test
    fun getListOfEventsTest() {
        val eventCount = 2
        populateTestEventsList(eventCount)

        val listFromDb = LiveDataTestUtil.getLiveDataValue(eventDao.getAllEvents())

        assertEquals(listFromDb.size, eventCount)
    }

    @Test
    fun insertEventAndFindTest() {
        val event = TestDataUtil.createEvent(1)

        eventDao.insert(event)
        val eventFromDb = LiveDataTestUtil.getLiveDataValue(eventDao.find(1))
        assertEquals(eventFromDb.id, event.id)
        assertEquals(eventFromDb.title, event.title)
    }

    private fun populateTestEventsList(count: Int) {
        val testEventList = TestDataUtil.createListOfEvents(count)
        for(event in testEventList) {
            eventDao.insert(event)
        }
    }
}