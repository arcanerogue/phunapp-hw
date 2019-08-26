package com.glopez.phunapp.viewmodel

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import com.glopez.phunapp.model.Event
import com.glopez.phunapp.model.db.Resource
import com.glopez.phunapp.model.network.ApiResponse
import com.glopez.phunapp.model.repository.FeedRepository
import com.glopez.phunapp.testutil.LiveDataTestObserver
import com.glopez.phunapp.testutil.TestDataUtil
import com.glopez.phunapp.view.viewmodels.FeedViewModel
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert.*
import org.hamcrest.core.IsInstanceOf.instanceOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FeedViewModelTest {
    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val feedObserver = LiveDataTestObserver<Resource<List<Event>>>()
    private val mockEventRepository: FeedRepository = mock()
    private lateinit var feedViewModel: FeedViewModel
    private val events = TestDataUtil.createListOfEvents(2)
    private val eventsFromDatabase: MutableLiveData<List<Event>> = MutableLiveData()
    private val apiResponseState: MutableLiveData<ApiResponse<List<Event>>> = MutableLiveData()

    @Before
    fun setUp() {
        whenever(mockEventRepository.getApiResponseState())
            .doReturn(apiResponseState)
        whenever(mockEventRepository.getEventsFromDatabase())
            .doReturn(eventsFromDatabase)

        feedViewModel = FeedViewModel(mockEventRepository)
        feedViewModel.getEventsResource().observeForever(feedObserver)
    }

    @Test
    fun `verify initial fetch results in Success when local cache of feed has data`() {
        eventsFromDatabase.value = events
        assertThat(feedObserver.getData(), instanceOf(Resource.Success::class.java))
        cleanUpAfterTest()
    }

    @Test
    fun `verify initial fetch results in Loading when local cache of feed is empty`() {
        eventsFromDatabase.value = null
        assertThat(feedObserver.getData(), instanceOf(Resource.Loading::class.java))
        cleanUpAfterTest()
    }

    @Test
    fun `verify resource state is updated to success on successful api response`() {
        eventsFromDatabase.value = events
        apiResponseState.value = ApiResponse.Success(events)

        feedViewModel.removeSources()
        feedViewModel.refreshEvents()

        assertThat(feedObserver.getData(), instanceOf(Resource.Success::class.java))
        val resourceData = feedObserver.getData() as Resource.Success<List<Event>>
        assertEquals(resourceData.data, events)
        cleanUpAfterTest()
    }

    @Test
    fun `verify resource state is updated to error on error api response`() {
        eventsFromDatabase.value = null
        apiResponseState.value = ApiResponse.Error("received error response")

        feedViewModel.removeSources()
        feedViewModel.refreshEvents()

        assertThat(feedObserver.getData(), instanceOf(Resource.Error::class.java))
        cleanUpAfterTest()
    }

    private fun cleanUpAfterTest() {
        feedViewModel.getEventsResource().removeObserver(feedObserver)
        feedViewModel.removeSources()
    }
}