package com.glopez.phunapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.glopez.phunapp.constants.DB_MISSING_ID_VALUE
import com.glopez.phunapp.model.StarWarsEvent
import com.glopez.phunapp.model.db.Resource
import com.glopez.phunapp.model.repository.FeedRepository
import com.glopez.phunapp.testutil.CoroutineTestRule
import com.glopez.phunapp.testutil.LiveDataTestObserver
import com.glopez.phunapp.testutil.TestDataUtil
import com.glopez.phunapp.view.StarWarsUiEvent
import com.glopez.phunapp.view.detail.DetailViewModel
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.hamcrest.core.IsInstanceOf.instanceOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class DetailViewModelTest {
    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutineTestRule = CoroutineTestRule()

    private val testDispatcher = coroutineTestRule.testDispatcher
    private val singleEventObserver = LiveDataTestObserver<Resource<StarWarsUiEvent>>()
    private val mockEventRepository: FeedRepository = mock()
    private lateinit var detailViewModel: DetailViewModel

    @Before
    fun setUp() {
        detailViewModel = DetailViewModel(mockEventRepository)
        detailViewModel.eventDetail.observeForever(singleEventObserver)
    }

    @Test
    fun `verify event detail resource results in success when the event is found`() =
        testDispatcher.runBlockingTest {
            val eventId = 1
            val eventDetail = TestDataUtil.createEvent(eventId)

            whenever(mockEventRepository.getEvent(any()))
                .doReturn(eventDetail)

            detailViewModel.getEventDetail(eventId)
            assertThat(singleEventObserver.getData(), instanceOf(Resource.Success::class.java))
        }

    @Test
    fun `verify event detail resource results in error when the event is not found`() =
        testDispatcher.runBlockingTest {
            val starWarsEventDetail: StarWarsEvent? = null

            whenever(mockEventRepository.getEvent(any()))
                .doReturn(starWarsEventDetail)

            detailViewModel.getEventDetail(any())
            assertThat(singleEventObserver.getData(), instanceOf(Resource.Error::class.java))
        }

    @Test
    fun `verify event detail resource results in error when the event id value is less than 1`() =
        testDispatcher.runBlockingTest {
            val eventDetail = TestDataUtil.createEvent(DB_MISSING_ID_VALUE)

            whenever(mockEventRepository.getEvent(any()))
                .doReturn(eventDetail)

            detailViewModel.getEventDetail(any())
            assertThat(singleEventObserver.getData(), instanceOf(Resource.Error::class.java))
        }

    @After
    fun cleanUp() {
        detailViewModel.eventDetail.removeObserver(singleEventObserver)
    }
}