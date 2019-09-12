package com.glopez.phunapp.repository

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import com.glopez.phunapp.constants.HTTP_BAD_REQUEST
import com.glopez.phunapp.model.StarWarsEvent
import com.glopez.phunapp.model.db.EventDao
import com.glopez.phunapp.model.db.EventDatabase
import com.glopez.phunapp.model.network.ApiResponse
import com.glopez.phunapp.model.network.EventFeedProvider
import com.glopez.phunapp.model.repository.EventFeedRepository
import com.glopez.phunapp.testutil.LiveDataTestObserver
import com.glopez.phunapp.testutil.TestDataUtil
import org.junit.Rule
import com.nhaarman.mockitokotlin2.*
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.hamcrest.core.IsInstanceOf.instanceOf
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import org.junit.Assert.*

class StarWarsEventFeedRepositoryTest {
    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val mockDatabase: EventDatabase = mock()
    private val mockEventDao: EventDao = mock()
    private val apiResponseStateObserver = LiveDataTestObserver<ApiResponse<List<StarWarsEvent>>>()
    private val events = TestDataUtil.createListOfEvents(2)
    private val eventsFromDatabase: MutableLiveData<List<StarWarsEvent>> = MutableLiveData()
    private lateinit var eventFeedRepo: EventFeedRepository

    @Before
    fun setUp() {
       eventFeedRepo = EventFeedRepository.getInstance(mockDatabase, EventFeedProvider())

        whenever(mockEventDao.getAllEvents())
            .doReturn(eventsFromDatabase)

        eventFeedRepo.getApiResponseState().observeForever(apiResponseStateObserver)
    }

    @Test
    fun `verify api success state is set`() {
        val response = Response.success(events)
        val successResponse = ApiResponse.onResponse(response)

        eventFeedRepo.setResponseState(successResponse)
        assertThat(apiResponseStateObserver.getData(), instanceOf(ApiResponse.Success::class.java))
    }

    @Test
    fun `verify api empty body response state is set`() {
        val emptyStarWarsEvents: List<StarWarsEvent>? = null
        val response = Response.success(emptyStarWarsEvents)
        val emptyBodyResponse = ApiResponse.onResponse(response)

        eventFeedRepo.setResponseState(emptyBodyResponse)
        assertThat(apiResponseStateObserver.getData(), instanceOf(ApiResponse.EmptyBody::class.java))
    }

    @Test
    fun `verify api error response state is set`() {
        val errorMessage = "Api returned an error response"
        val response = Response
            .error<List<StarWarsEvent>>(
                HTTP_BAD_REQUEST,
                ResponseBody.create(MediaType.get("application/txt"), errorMessage))
        val errorResponse = ApiResponse.onResponse(response)

        eventFeedRepo.setResponseState(errorResponse)
        assertThat(apiResponseStateObserver.getData(), instanceOf(ApiResponse.Error::class.java))
    }
}