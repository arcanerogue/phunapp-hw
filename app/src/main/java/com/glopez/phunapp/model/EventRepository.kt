package com.glopez.phunapp.model

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.glopez.phunapp.model.db.EventDao
import com.glopez.phunapp.model.db.EventDatabase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.glopez.phunapp.model.network.ApiResponse
import com.glopez.phunapp.model.network.EventFeedProvider
import timber.log.Timber
import android.os.SystemClock
import com.glopez.phunapp.R
import com.glopez.phunapp.utils.StringsResourceProvider
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class EventRepository(
    eventDatabase: EventDatabase,
    private val eventApi: EventFeedProvider) {

    private val eventDao: EventDao = eventDatabase.eventDao()
    private val minValueSetForIdField: Int = 1
    private val apiResultState = MutableLiveData<ApiResponse<List<Event>>>()
    private val disposables = CompositeDisposable()

    private var refreshTimestamp: Long = 0
    private val timeoutInMinutes = 1
    private val refreshTimeout: Long = TimeUnit.MINUTES.toMillis(timeoutInMinutes.toLong())

    companion object {
        private var INSTANCE: EventRepository? = null

        fun getInstance(eventDb: EventDatabase, eventApi: EventFeedProvider): EventRepository {
            synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE = EventRepository(eventDb, eventApi)
                }
                return INSTANCE as EventRepository
            }
        }
    }

    fun updateEventsFromNetwork() {
        if (shouldRefresh()) {
            refreshTimestamp = SystemClock.uptimeMillis()
            getEventsFromNetwork()
        }
    }

    fun getEventsFromDatabase(): LiveData<List<Event>> {
        return eventDao.getAllEvents()
    }

    private fun getEventsFromNetwork() {
        Timber.d("Retrieving events from network.")
        apiResultState.value = ApiResponse.Loading(emptyList())

        eventApi.getEventFeed(object : Callback<List<Event>> {
            // Network exception occurred talking to the server or an unexpected exception
            // occurred creating the request or processing the response
            override fun onFailure(call: Call<List<Event>>, error: Throwable) {
                apiResultState.value = ApiResponse.onFailure(error.message)
                Timber.e(String.format(
                    "${StringsResourceProvider.getString(R.string.repo_fetch_error)}. ${error.message}"))
            }

            override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                setResponseState(ApiResponse.onResponse(response))
            }
        })
    }

    fun getApiResponseState() : LiveData<ApiResponse<List<Event>>> {
        return this.apiResultState
    }

    private fun setResponseState(apiResponse: ApiResponse<List<Event>>) {
        when (apiResponse) {
            // HTTP Response Code is in the 200-300 range.
            is ApiResponse.Success<List<Event>> -> {
                apiResultState.value = apiResponse
                insertEventsIntoDatabase(apiResponse.body)
                Timber.d(String.format(
                    StringsResourceProvider.getString(R.string.repo_events_fetch_success) +
                            " Received response with count: ${apiResponse.body.size}"))
            }
            // Received Response with empty body.
            is ApiResponse.EmptyBody<List<Event>> -> {
                apiResultState.value = apiResponse
                Timber.d(StringsResourceProvider.getString(R.string.repo_events_fetch_empty_body))
            }
            // HTTP Response Code is in the 300's, 400's, 500's, or application-level failure.
            is ApiResponse.Error -> {
                apiResultState.value = apiResponse
                Timber.e(String.format(
                    "${StringsResourceProvider.getString(R.string.repo_success_http_error)} ${apiResponse.responseCode}." +
                            " ${apiResponse.errorMessage}"))
            }
        }
    }

    private fun insertEventsIntoDatabase(eventList: List<Event>) {
        lateinit var disposableInsertEvent: Disposable
        for (event: Event in eventList) {
            // If the id field was not present in the Response object, the default value of 0 will be set.
            // If this is the case, the Event will not be inserted into the database.
            if (event.id < minValueSetForIdField) {
                Timber.d("Skipping event with invalid Id value.")
            }
            else {
//                AsyncTask.execute { eventDao.insert(event) }
                disposableInsertEvent = Completable.fromAction { eventDao.insert(event) }
                    .doOnError { Timber.e("Error inserting into database: ${it.message}") }
                    .doOnComplete { Timber.d("Inserted Event with id: ${event.id} into database.") }
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe()
                disposables.add(disposableInsertEvent)
            }
        }
    }

    fun getSingleEventFromDatabase(id: Int): LiveData<Event> {
        return eventDao.find(id)
    }

    private fun shouldRefresh(): Boolean {
        val lastRefresh = refreshTimestamp
        val currentTime = SystemClock.uptimeMillis()
        return if (currentTime - lastRefresh > refreshTimeout) {
            refreshTimestamp = currentTime
            true
        } else
            false
    }

    fun clearDisposables() {
        disposables.clear()
    }
}





