package com.glopez.phunapp.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.glopez.phunapp.model.StarWarsEvent
import com.glopez.phunapp.model.db.EventDao
import com.glopez.phunapp.model.network.FeedProvider
import com.glopez.phunapp.model.repository.EventFeedRepository
import com.glopez.phunapp.testutil.*
import org.junit.Rule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class StarWarsEventFeedRepositoryTest {
    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutineTestRule = CoroutineTestRule()

    private val testDispatcher = coroutineTestRule.testDispatcher
    private val events = TestDataUtil.createListOfEvents(2)
    private val mockEventDao: EventDao = EventDaoMock()
    private val mockEventApi: FeedProvider = FeedProviderMock(events)
    private lateinit var eventFeedRepo: EventFeedRepository

    @Before
    fun setUp() {
        EventFeedRepository
            .init(mockEventDao, mockEventApi, coroutineTestRule.testDispatcherProvider)
        eventFeedRepo = EventFeedRepository
    }

    @Test
    fun `verify events are retrieved from database`() =
        testDispatcher.runBlockingTest {
            for (event: StarWarsEvent in events) {
                mockEventDao.insert(event)
            }
            val events = eventFeedRepo.getEvents()
            assertEquals(events.size, events.size)
        }

    @Test
    fun `verify empty list is retrieved when no events in database`() =
        testDispatcher.runBlockingTest {

            assertEquals(0, eventFeedRepo.getEvents().size)
        }

    @Test
    fun `verify event can be retrieved by id`() =
        testDispatcher.runBlockingTest {

            mockEventDao.insert(TestDataUtil.createEvent(10))

            val event = eventFeedRepo.getEvent(10)
            assertEquals(event?.id, 10)
        }
}