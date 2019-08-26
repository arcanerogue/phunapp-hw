package com.glopez.phunapp.viewmodel

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import com.glopez.phunapp.constants.DB_MISSING_ID_VALUE
import com.glopez.phunapp.model.Event
import com.glopez.phunapp.model.db.Resource
import com.glopez.phunapp.model.repository.FeedRepository
import com.glopez.phunapp.testutil.LiveDataTestObserver
import com.glopez.phunapp.testutil.TestDataUtil
import com.glopez.phunapp.view.viewmodels.DetailViewModel
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert.*
import org.hamcrest.core.IsInstanceOf.instanceOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DetailViewModelTest {
    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val singleEventObserver = LiveDataTestObserver<Resource<Event>>()
    private val mockEventRepository: FeedRepository = mock()
    private lateinit var detailViewModel: DetailViewModel
    private val eventId: Int = 1
    private val testLiveData: MutableLiveData<Event> = MutableLiveData()

    @Before
    fun setUp() {
        whenever(mockEventRepository.getSingleEventFromDatabase(any()))
            .doReturn(testLiveData)

        detailViewModel = DetailViewModel(mockEventRepository)
        detailViewModel.getEventDetailResource(eventId)
            .observeForever(singleEventObserver)
    }

    @Test
    fun `verify event detail resource results in success when the event is found`() {
        val eventDetail = TestDataUtil.createEvent(eventId)
        testLiveData.value = eventDetail
        assertThat(singleEventObserver.getData(), instanceOf(Resource.Success::class.java))
    }

    @Test
    fun `verify event detail resource results in error when the event is not found`() {
        val eventDetail: Event? = null
        testLiveData.value = eventDetail
        assertThat(singleEventObserver.getData(), instanceOf(Resource.Error::class.java))
    }

    @Test
    fun `verify event detail resource results in error when the event id value is less than 1`() {
        val eventDetail = TestDataUtil.createEvent(DB_MISSING_ID_VALUE)
        testLiveData.value = eventDetail
        assertThat(singleEventObserver.getData(), instanceOf(Resource.Error::class.java))
    }

    @After
    fun cleanUp() {
        detailViewModel.getEventDetailResource(eventId)
            .removeObserver(singleEventObserver)
    }
}