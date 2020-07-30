package com.glopez.phunapp

import android.app.Application
import android.os.StrictMode
import com.glopez.phunapp.model.repository.EventFeedRepository
import com.glopez.phunapp.model.db.EventDatabase
import com.glopez.phunapp.model.network.EventFeedProvider
import com.glopez.phunapp.utils.StringsResourceProvider
import timber.log.Timber

class PhunApp : Application() {

    override fun onCreate() {
        super.onCreate()
        setTimberLogging()
        Timber.d("Creating PhunApp.")
//        setStrictMode()
        StringsResourceProvider.init(this.resources)
        EventFeedRepository.init(
            EventDatabase.getDatabase(this).eventDao(),
            EventFeedProvider().getService())
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