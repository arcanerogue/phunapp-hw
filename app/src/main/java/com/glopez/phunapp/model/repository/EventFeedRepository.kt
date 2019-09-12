package com.glopez.phunapp.model.repository

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
import android.support.annotation.VisibleForTesting
import com.glopez.phunapp.R
import com.glopez.phunapp.constants.DB_MINIMUM_ID_VALUE
import com.glopez.phunapp.model.StarWarsEvent
import com.glopez.phunapp.utils.StringsResourceProvider
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class EventFeedRepository(
    eventDatabase: EventDatabase,
    private val eventApi: EventFeedProvider): FeedRepository {

    private val eventDao: EventDao = eventDatabase.eventDao()
    private val apiResultState = MutableLiveData<ApiResponse<List<StarWarsEvent>>>()

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var disposables = CompositeDisposable()

    private var refreshTimestamp: Long = 0
    private val timeoutInMinutes = 1
    private val refreshTimeout: Long = TimeUnit.MINUTES.toMillis(timeoutInMinutes.toLong())

    companion object {
        private var INSTANCE: EventFeedRepository? = null

        fun getInstance(eventDb: EventDatabase, eventApi: EventFeedProvider): EventFeedRepository {
            synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE =
                        EventFeedRepository(eventDb, eventApi)
                }
                return INSTANCE as EventFeedRepository
            }
        }
    }

    override fun updateEventsFromNetwork() {
        if (shouldRefresh()) {
            refreshTimestamp = SystemClock.uptimeMillis()
            getEventsFromNetwork()
        }
    }

    override fun getEventsFromDatabase(): LiveData<List<StarWarsEvent>> {
        return eventDao.getAllEvents()
    }

    override fun getEventsFromNetwork() {
        Timber.d("Retrieving events from network.")
        apiResultState.value = ApiResponse.Loading(emptyList())

        eventApi.getEventFeed(object : Callback<List<StarWarsEvent>> {
            // Network exception occurred talking to the server or an unexpected exception
            // occurred creating the request or processing the response
            override fun onFailure(call: Call<List<StarWarsEvent>>, error: Throwable) {
                apiResultState.value = ApiResponse.onFailure(error.message)
                Timber.e(String.format(
                    "${StringsResourceProvider.getString(R.string.repo_fetch_error)}. ${error.message}"))
            }

            override fun onResponse(call: Call<List<StarWarsEvent>>, response: Response<List<StarWarsEvent>>) {
                setResponseState(ApiResponse.onResponse(response))
            }
        })
    }

    override fun getApiResponseState() : LiveData<ApiResponse<List<StarWarsEvent>>> {
        return this.apiResultState
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun setResponseState(apiResponse: ApiResponse<List<StarWarsEvent>>) {
        when (apiResponse) {
            // HTTP Response Code is in the 200-300 range.
            is ApiResponse.Success<List<StarWarsEvent>> -> {
                apiResultState.value = apiResponse
                insertEventsIntoDatabase(apiResponse.body)
                Timber.d("Received response with count: ${apiResponse.body.size}")
            }
            // Received Response with empty body.
            is ApiResponse.EmptyBody<List<StarWarsEvent>> -> {
                apiResultState.value = apiResponse
                Timber.d("Received Response with empty body")
            }
            // HTTP Response Code is in the 300's, 400's, 500's, or application-level failure.
            is ApiResponse.Error -> {
                apiResultState.value = apiResponse
                Timber.e(String.format("Failed to retrieve events. Response code:" +
                        "${apiResponse.responseCode},Response body: ${apiResponse.message}"))
            }
        }
    }

    override fun insertEventsIntoDatabase(starWarsEvents: List<StarWarsEvent>) {
        lateinit var disposableInsertEvent: Disposable
        if (disposables.isDisposed) {
            disposables = CompositeDisposable()
        }
        for (starWarsEvent: StarWarsEvent in starWarsEvents) {
            // If the id field was not present in the Response object, the default value of 0 will be set.
            // If this is the case, the StarWarsEvent will not be inserted into the database.
            if (starWarsEvent.id < DB_MINIMUM_ID_VALUE) {
                Timber.d("Skipping starWarsEvent with invalid Id value.")
            }
            else {
                disposableInsertEvent = Completable.fromAction { eventDao.insert(starWarsEvent) }
//                    .doOnError { Timber.e("Error inserting into database: ${it.message}") }
                    .doOnError { it.printStackTrace() }
                    .doOnComplete { Timber.d("Inserted StarWarsEvent with id: ${starWarsEvent.id} into database.") }
                    .subscribeOn(Schedulers.io())
                    .subscribe()
                disposables.add(disposableInsertEvent)
            }
        }
    }

    override fun getSingleEventFromDatabase(id: Int): LiveData<StarWarsEvent> {
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

    override fun clearDisposables() {
        if (!disposables.isDisposed) {
            disposables.dispose()
        }
    }
}





