package com.glopez.phunapp.model.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import com.glopez.phunapp.model.db.EventDao
import com.glopez.phunapp.model.db.EventDatabase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.glopez.phunapp.model.network.ApiResponse
import com.glopez.phunapp.model.network.EventFeedProvider
import timber.log.Timber
import android.support.annotation.VisibleForTesting
import com.glopez.phunapp.R
import com.glopez.phunapp.constants.DB_MINIMUM_ID_VALUE
import com.glopez.phunapp.model.StarWarsEvent
import com.glopez.phunapp.model.db.Resource
import com.glopez.phunapp.utils.StringsResourceProvider
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception
import java.util.concurrent.TimeUnit

object EventFeedRepository: FeedRepository {
    private val eventApi = EventFeedProvider()
    private val apiResultState = MutableLiveData<ApiResponse<List<StarWarsEvent>>>()
    private lateinit var eventDao: EventDao

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var disposables = CompositeDisposable()

    private var refreshTimestamp: Long = 0
    private val refreshTimeout: Long = TimeUnit.MINUTES.toMillis(1)

    private val eventsResourceStatus: MediatorLiveData<Resource<List<StarWarsEvent>>> = MediatorLiveData()
    private lateinit var dbSource: LiveData<List<StarWarsEvent>>

    fun init(eventDatabase: EventDatabase) {
        eventDao = eventDatabase.eventDao()
        dbSource = getEventsFromDatabase()

        eventsResourceStatus.addSource(dbSource) { data ->
            if (data.isNullOrEmpty()) {
                eventsResourceStatus.value = Resource.Loading(emptyList())
            } else {
                eventsResourceStatus.value = Resource.Success(data)
            }
        }
    }

    override fun getEvents(): LiveData<Resource<List<StarWarsEvent>>> {
        return eventsResourceStatus
    }

    override fun getEventsFromDatabase(): LiveData<List<StarWarsEvent>> {
        return eventDao.getAllEvents()
    }

    override fun getSingleEventFromDatabase(id: Int): LiveData<StarWarsEvent> {
        return eventDao.find(id)
    }

    override fun getApiResponseState() : LiveData<ApiResponse<List<StarWarsEvent>>> {
        return apiResultState
    }

    private fun shouldRefresh(): Boolean {
        val lastRefresh = refreshTimestamp
        val currentTime = System.currentTimeMillis()
        return currentTime - lastRefresh > refreshTimeout
    }

    override fun updateEventsFromNetwork() {
        if (shouldRefresh()) {
            refreshTimestamp = System.currentTimeMillis()
            getEventsFromNetwork()
            setEventsResourceStatus()
        }
    }

    override fun getEventsFromNetwork() {
        Timber.d("Retrieving events from network.")
        apiResultState.value = ApiResponse.Loading(emptyList())

        eventApi.getEventFeed(object : Callback<List<StarWarsEvent>> {
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

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun setResponseState(apiResponse: ApiResponse<List<StarWarsEvent>>) {
        apiResultState.value = apiResponse
        when (apiResponse) {
            is ApiResponse.Success<List<StarWarsEvent>> -> {
                insertEventsIntoDatabase(apiResponse.body)
            }
        }
    }

    /**
     * Listens for changes to the ApiResponse state and then pulls the latest
     * data from the database. When the database is empty and events can't be fetched from the network,
     * the Resource error state is set to inform the view.
     */
    private fun setEventsResourceStatus() {
        eventsResourceStatus.removeSource(dbSource)
        eventsResourceStatus.removeSource(apiResultState)

        eventsResourceStatus.value = Resource.Loading(emptyList())
        eventsResourceStatus.addSource(apiResultState) {
            when (it) {
                is ApiResponse.Success<List<StarWarsEvent>> -> {
                    eventsResourceStatus.addSource(dbSource) { data ->
                        eventsResourceStatus.value = Resource.Success(data)
                    }
                }
                is ApiResponse.EmptyBody,
                is ApiResponse.Error -> {
                    eventsResourceStatus.addSource(dbSource) { data ->
                        if (data.isNullOrEmpty())
                            eventsResourceStatus.value = Resource.Error(Exception("Unable to populate the database with events."))
                        else
                            eventsResourceStatus.value = Resource.Success(data)
                    }
                }
            }
        }
    }

    override fun insertEventsIntoDatabase(starWarsEvents: List<StarWarsEvent>) {
        for (starWarsEvent: StarWarsEvent in starWarsEvents) {
            // If the id field was not present in the Response object, the default value of 0 will be set.
            // If this is the case, the StarWarsEvent will not be inserted into the database.
            if (starWarsEvent.id < DB_MINIMUM_ID_VALUE) {
                Timber.d("Skipping starWarsEvent with invalid Id value.")
            }
            else {
                val disposableInsertEvent = Completable.fromAction { eventDao.insert(starWarsEvent) }
                    .doOnError { it.printStackTrace() }
                    .doOnComplete { Timber.d("Inserted StarWarsEvent with id: ${starWarsEvent.id} into database.") }
                    .subscribeOn(Schedulers.io())
                    .subscribe()
                disposables.add(disposableInsertEvent)
            }
        }
    }

    override fun clearDisposables() {
        if (!disposables.isDisposed) {
            disposables.dispose()
        }
    }
}





