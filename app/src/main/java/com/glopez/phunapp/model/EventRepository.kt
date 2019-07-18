package com.glopez.phunapp.model

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.glopez.phunapp.model.db.EventDao
import com.glopez.phunapp.model.db.EventDatabase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.os.AsyncTask
import com.glopez.phunapp.model.network.ApiResponse
import com.glopez.phunapp.model.network.EventFeedProvider
import timber.log.Timber
import android.arch.lifecycle.MediatorLiveData
import com.glopez.phunapp.model.db.Resource


class EventRepository(eventDatabase: EventDatabase) {
    private val eventApi = EventFeedProvider()
    private var eventFeedList: List<Event> = emptyList()
    private val eventDao: EventDao
    private val defaultValueSetForMissingIdField: Int = 0
    private val apiResultState = MutableLiveData<ApiResponse<List<Event>>>()
//    private val dataResult = MutableLiveData<Resource<List<Event>>>()
    private val dataResult = MediatorLiveData<Resource<List<Event>>>()

    companion object {
        private var INSTANCE: EventRepository? = null

        fun getInstance(eventDb: EventDatabase): EventRepository {
            synchronized(this) {
                if (INSTANCE == null) {
                    // Create repository
                    INSTANCE = EventRepository(eventDb)
                }
                return INSTANCE as EventRepository
            }
        }
    }

    init {
        // Call web service to fetch events from remote source
//        getEventsFromNetwork()
        eventDao = eventDatabase.eventDao()

        dataResult.value = Resource.Loading(emptyList())
//        val dbSource = eventDao.getAllEvents()
        val dbSource = getEventsFromDatabase()
        dataResult.addSource(dbSource) { dbData ->
            dataResult.removeSource(dbSource)
//            if (dbData == null || dbData.isEmpty()) {
            if (dbData.isNullOrEmpty()) {
                getEventsFromNetwork()
            } else {
                dataResult.value = Resource.Success(dbData)
            }
        }
    }

    fun getEventsFromDatabase(): LiveData<List<Event>> {
//        dataResult.value = Resource.Loading(emptyList())
//        val dbSource = eventDao.getAllEvents()
//        if (dbSource.value == null)
//            getEventsFromNetwork()
//        else
//            dataResult.value = Resource.Success(dbSource.value)
//        return dbSource
        return eventDao.getAllEvents()
    }

//    fun getEventsFromDatabase(): LiveData<Resource<List<Event>>> {
//        dataResult.value = Resource.Loading(emptyList())
//        val dbSource = eventDao.getAllEvents()
//        dataResult.addSource(dbSource) { dbData ->
//            dataResult.removeSource(dbSource)
//            if (dbData == null || dbData.isEmpty()) {
//                getEventsFromNetwork()
//            } else {
//                dataResult.value = Resource.Success(dbData)
//            }
//        }
//        return dataResult
//    }

    fun getEventsFromNetwork() {
        Timber.d("Retrieving events from network.")
        apiResultState.value = ApiResponse.Loading(emptyList())
        dataResult.addSource(apiResultState) {
            eventApi.getEventFeed(object : Callback<List<Event>> {
                // Network exception occurred talking to the server or an unexpected exception
                // occurred creating the request or processing the response
                override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                    apiResultState.value = ApiResponse.onFailure(t)
                    dataResult.removeSource(apiResultState)
                    dataResult.value = Resource.Error(t)
                }

                // Received an HTTP Response
                override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                    val apiResponse = ApiResponse.onResponse(response)
                    when (apiResponse) {
                        is ApiResponse.Success<List<Event>> -> {
                            apiResultState.value = apiResponse
                            insertEventsIntoDatabase(apiResponse.body)
                            Timber.d("Received response with count: ${apiResponse.body.size}")
                            dataResult.removeSource(apiResultState)
//                            dataResult.value = Resource.Success(apiResponse.body)

//                            val dbSource = getEventsFromDatabase()
//                            dataResult.addSource(dbSource) {
                            dataResult.addSource(getEventsFromDatabase()) {
//                                dataResult.removeSource(dbSource)
                                dataResult.value = Resource.Success(it)
                            }

//                        dataResult.addSource(eventDao.getAllEvents()) {
//                            dataResult.value = Resource.Success(it)
//                        }
                        }
//                    is ApiResponse.EmptyBody<List<Event>> -> {
//                        dataResult.addSource(eventDao.getAllEvents()) {
//                            dataResult.value = Resource.Success(it)
//                        }
//                    }
                        else -> {
                            dataResult.removeSource(apiResultState)
                            apiResultState.value = apiResponse
                        }
                    }
                }
            })
        }
    }

//    fun getEventsFromNetwork() {
////    fun getEventsFromNetwork(dbSource: LiveData<List<Event>>) {
//        Timber.d("Retrieving events from network.")
////        dataResult.addSource(dbSource) {
////            dataResult.value = Resource.Loading(it)
////        }
//
////        dataResult.addSource()
//        apiResultState.value = ApiResponse.Loading(emptyList())
//        eventApi.getEventFeed(object: Callback<List<Event>> {
//            // Network exception occurred talking to the server or an unexpected exception
//            // occurred creating the request or processing the response
//            override fun onFailure(call: Call<List<Event>>, t: Throwable) {
//                apiResultState.value = ApiResponse.onFailure(t)
//            }
//            // Received an HTTP Response
//            override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
////                setApiResponseState(response)
//                val apiResponse = ApiResponse.onResponse(response)
//                when(apiResponse) {
//                    is ApiResponse.Success<List<Event>> -> {
//                        apiResultState.value = apiResponse
//                        insertEventsIntoDatabase(apiResponse.body)
//                        Timber.d("Received response with count: ${apiResponse.body.size}")
//                        dataResult.value = Resource.Success(apiResponse.body)
////                        dataResult.addSource(eventDao.getAllEvents()) {
////                            dataResult.value = Resource.Success(it)
////                        }
//                    }
////                    is ApiResponse.EmptyBody<List<Event>> -> {
////                        dataResult.addSource(eventDao.getAllEvents()) {
////                            dataResult.value = Resource.Success(it)
////                        }
////                    }
//                    else -> {
//                        apiResultState.value = apiResponse
//                    }
//                }
//            }
//        })
//    }

//    private fun setApiResponseState(response: Response<List<Event>>) {
//        val responseBody = response.body()
//        val responseCode:String = response.code().toString()
//        val errorBody = response.errorBody().toString()
//
//        // HTTP Response Code is in the 200-300 range.
//        if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
//            // If the Response body is not empty, populate eventFeedList with list of
//            // events from body. Populate database with events retrieved from remote data source.
//            apiResultState.value = ApiResponse.Success(responseBody)
//            insertEventsIntoDatabase(responseBody)
//            Timber.d("Received response with count: ${responseBody.size}")
//        } else if (response.isSuccessful && responseBody.isNullOrEmpty()) {
//            // Received Response with empty body.
//            apiResultState.value = ApiResponse.EmptyBody(responseCode)
//        } else {
//            // HTTP Response Code is in the 300's, 400's, 500's, or application-level failure.
//            apiResultState.value = ApiResponse.ResponseError(responseCode, errorBody)
//        }
//    }

    fun getApiResponseState() : LiveData<ApiResponse<List<Event>>> {
        return this.apiResultState
    }

    fun getDbResultState() : LiveData<Resource<List<Event>>> {
        return this.dataResult
    }

    private fun insertEventsIntoDatabase(eventList: List<Event>) {
        for (event: Event in eventList) {
            // If the id field was not present in the Response object, the default value of 0 will be set.
            // If this is the case, the Event will not be inserted into the database.
            if (event.id > defaultValueSetForMissingIdField) {
                AsyncTask.execute { eventDao.insert(event) }
            }
        }
    }

    fun getSingleEventFromDatabase(id: Int): LiveData<Event> {
        return eventDao.find(id)
    }
}





