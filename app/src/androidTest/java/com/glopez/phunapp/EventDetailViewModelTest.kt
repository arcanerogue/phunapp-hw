package com.glopez.phunapp

import android.app.Application
import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import com.glopez.phunapp.data.Event
import com.glopez.phunapp.data.EventRepository
import com.glopez.phunapp.ui.viewmodels.EventDetailViewModel
import com.glopez.phunapp.utils.LiveDataTestUtil
import com.glopez.phunapp.utils.TestDataUtil
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EventDetailViewModelTest {

    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val singleEventObserver: Observer<Event> = mock()
    private val mockEventRepository: EventRepository = mock()
    private lateinit var eventDetailViewModel: EventDetailViewModel
    private var eventId: Int = 1

    @Before
    fun setUp() {
        eventDetailViewModel.getEvent(any()).observeForever(singleEventObserver)
    }
}