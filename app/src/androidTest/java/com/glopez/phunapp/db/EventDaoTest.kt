package com.glopez.phunapp.db

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.glopez.phunapp.model.StarWarsEvent
import com.glopez.phunapp.testutil.TestDataUtil
import com.glopez.phunapp.model.db.EventDao
import com.glopez.phunapp.model.db.EventDatabase
import com.glopez.phunapp.testutil.LiveDataTestObserver
import junit.framework.Assert.*
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
    private val singleEventObserver = LiveDataTestObserver<StarWarsEvent>()
    private val eventsListObserver = LiveDataTestObserver<List<StarWarsEvent>>()

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
    fun verify_retrieval_of_events() {
        val eventCount = 2
        populateTestEventsList(eventCount)

        val eventsFromDb = eventDao.getAllEvents()
        eventsFromDb.observeForever(eventsListObserver)
        assertEquals(eventCount, eventsListObserver.getData()?.size)
    }

    @Test
    fun verify_event_can_be_inserted_and_found_by_id() {
        val testId = 1
        val event = TestDataUtil.createEvent(testId)

        eventDao.insert(event)
        val eventDetail = eventDao.find(testId)
        eventDetail.observeForever(singleEventObserver)
        assertEquals(event.id, singleEventObserver.getData()?.id)
        assertEquals(event.title, singleEventObserver.getData()?.title)
        eventDetail.removeObserver(singleEventObserver)
    }

    private fun populateTestEventsList(count: Int) {
        val testEventList = TestDataUtil.createListOfEvents(count)
        for(event in testEventList) {
            eventDao.insert(event)
        }
    }
}