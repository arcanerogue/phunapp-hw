package com.glopez.phunapp

import android.app.Application
import android.os.StrictMode
import com.glopez.phunapp.model.repository.EventFeedRepository
import com.glopez.phunapp.model.db.EventDatabase
import com.glopez.phunapp.model.network.EventFeedProvider
import com.glopez.phunapp.utils.StringsResourceProvider
import timber.log.Timber

class PhunApp : Application() {
    private val eventApi = EventFeedProvider()

    override fun onCreate() {
        setTimberLogging()
        Timber.d("Creating PhunApp.")
        super.onCreate()
        setStrictMode()
        StringsResourceProvider.init(this.applicationContext.resources)
    }
    private fun getDatabase(): EventDatabase {
        return EventDatabase.getDatabase(this)
    }

    fun getRepository(): EventFeedRepository {
        return EventFeedRepository.getInstance(getDatabase(), eventApi)
    }

    private fun setTimberLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun setStrictMode() {
        if(BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .penaltyLog()
                    .build())

            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectActivityLeaks()
                    .detectLeakedClosableObjects()
                    .detectLeakedSqlLiteObjects()
                    .penaltyLog()
                    .build())
        }
    }
}