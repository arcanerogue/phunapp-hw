package com.glopez.phunapp

import android.app.Application
import android.os.StrictMode
import com.glopez.phunapp.model.EventRepository
import com.glopez.phunapp.model.db.EventDatabase
import com.glopez.phunapp.utils.StringsResourceProvider
import timber.log.Timber

class PhunApp : Application() {
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

    fun getRepository(): EventRepository {
        return EventRepository.getInstance(getDatabase())
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
                    .detectAll()
                    .penaltyLog()
                    .build())

            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build())
        }
    }
}