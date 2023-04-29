package com.glopez.phunapp.db

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.glopez.phunapp.testutil.TestDataUtil
import com.glopez.phunapp.model.db.EventDao
import com.glopez.phunapp.model.db.EventDatabase
import com.glopez.phunapp.testutil.CoroutineTestRule
import org.junit.Assert.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class EventDaoTest {
    @get:Rule
    var coroutineTestRule = CoroutineTestRule()

    private val testDispatcher = coroutineTestRule.testDispatcher
    private lateinit var eventDatabase: EventDatabase
    private lateinit var eventDao: EventDao

    @Before
    fun initializeDb() {
        eventDatabase = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            EventDatabase::class.java
        )
            .setTransactionExecutor(testDispatcher.asExecutor())
            .setQueryExecutor(testDispatcher.asExecutor())
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
    fun verify_retrieval_of_events() = testDispatcher.runBlockingTest {
        val eventCount = 2
        populateTestEventsList(eventCount)
        val eventsFromDb = eventDao.getAllEvents()
        assertEquals(eventCount, eventsFromDb.size)
    }

    @Test
    fun verify_retrieval_of_events_when_database_is_empty() =
        testDispatcher.runBlockingTest {
            val eventsFromDb = eventDao.getAllEvents()
            assertEquals(0, eventsFromDb.size)
        }

    @Test
    fun verify_event_can_be_inserted_and_found_by_id() {
        val testId = 10
        val event = TestDataUtil.createEvent(testId)

        testDispatcher.runBlockingTest {
            eventDao.insert(event)
            val eventDetail = eventDao.find(testId)
            assertEquals(event.id, eventDetail.id)
            assertEquals(event.title, eventDetail.title)
        }
    }

    @Test
    fun verify_event_cannot_be_find_with_provided_id() {
        val testId = 12
        val event = TestDataUtil.createEvent(testId)

        testDispatcher.runBlockingTest {
            eventDao.insert(event)
            val eventDetail = eventDao.find(15)
            assertEquals(null, eventDetail)
        }
    }

    private suspend fun populateTestEventsList(count: Int) {
        val testEventList = TestDataUtil.createListOfEvents(count)
        testDispatcher.runBlockingTest {
            for (event in testEventList) {
                eventDao.insert(event)
            }
        }
    }
}