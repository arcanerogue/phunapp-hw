package com.glopez.phunapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.glopez.phunapp.model.db.Resource
import com.glopez.phunapp.model.repository.FeedRepository
import com.glopez.phunapp.testutil.CoroutineTestRule
import com.glopez.phunapp.testutil.LiveDataTestObserver
import com.glopez.phunapp.testutil.TestDataUtil
import com.glopez.phunapp.view.StarWarsUiEvent
import com.glopez.phunapp.view.feed.FeedViewModel
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.hamcrest.core.IsInstanceOf.instanceOf
import org.junit.After
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class FeedViewModelTest {
    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutineTestRule = CoroutineTestRule()

    private val testDispatcher = coroutineTestRule.testDispatcher
    private val feedObserver = LiveDataTestObserver<Resource<List<StarWarsUiEvent>>>()
    private val mockEventRepository: FeedRepository = mock()
    private lateinit var feedViewModel: FeedViewModel

    private fun initViewModel() {
        feedViewModel = FeedViewModel(mockEventRepository)
        feedViewModel.eventsFeed.observeForever(feedObserver)
    }

    @Test
    fun `verify initial fetch results in Success when local cache of feed has data`() =
        testDispatcher.runBlockingTest {
            val events = TestDataUtil.createListOfEvents(2)
            whenever(mockEventRepository.getEvents())
                .doReturn(events)

            initViewModel()
            assertThat(feedObserver.getData(), instanceOf(Resource.Success::class.java))
        }

    @Test
    fun `verify initial fetch results in Loading when local cache of feed is empty`() =
        testDispatcher.runBlockingTest {
            whenever(mockEventRepository.getEvents())
                .doReturn(emptyList())

            initViewModel()
            assertThat(feedObserver.getData(), instanceOf(Resource.Loading::class.java))
        }


    @Test
    fun `verify resource state is updated to success on successful api response`() =
        testDispatcher.runBlockingTest {
            val updatedEvents = TestDataUtil.createListOfEvents(3)
            whenever(mockEventRepository.getEvents())
                .doReturn(updatedEvents)
            initViewModel()

            feedViewModel.refreshEvents()
            val updatedUiEvents = StarWarsUiEvent.mapToUiModelList(updatedEvents)

            assertThat(feedObserver.getData(), instanceOf(Resource.Success::class.java))

            val resourceData = feedObserver.getData() as Resource.Success<List<StarWarsUiEvent>>
            assertEquals(resourceData.data?.get(0), updatedUiEvents[0])
            assertEquals(resourceData.data?.get(1), updatedUiEvents[1])
            assertEquals(resourceData.data?.get(2), updatedUiEvents[2])
        }

    @Test
    fun `verify resource state is updated to error on error api response`() =
        testDispatcher.runBlockingTest {
            whenever(mockEventRepository.getEvents())
                .doReturn(emptyList())

            initViewModel()
            feedViewModel.refreshEvents()
            assertThat(feedObserver.getData(), instanceOf(Resource.Error::class.java))
        }

    @Test
    fun `verify resource state is updated to error on api null response`() =
        testDispatcher.runBlockingTest {
            whenever(mockEventRepository.getEvents())
                .thenReturn(null)

            initViewModel()
            feedViewModel.refreshEvents()
            assertThat(feedObserver.getData(), instanceOf(Resource.Error::class.java))
        }

    @After
    fun cleanUp() {
        feedViewModel.eventsFeed.removeObserver(feedObserver)
    }
}