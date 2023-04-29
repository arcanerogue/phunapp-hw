package com.glopez.phunapp

import android.app.Application
import android.os.StrictMode
import com.glopez.phunapp.di.AppComponent
import com.glopez.phunapp.di.ContextModule
import com.glopez.phunapp.di.DaggerAppComponent
import com.glopez.phunapp.utils.StringsResourceProvider
import timber.log.Timber

class PhunApp : Application() {
    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        setTimberLogging()
        Timber.d("Creating PhunApp.")
//        setStrictMode()
        StringsResourceProvider.init(this.resources)

        appComponent = DaggerAppComponent
            .builder()
            .contextModule(ContextModule(this))
            .build()
    }

    private fun setTimberLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun setStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .penaltyLog()
                    .build()
            )

            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectActivityLeaks()
                    .detectLeakedClosableObjects()
                    .detectLeakedSqlLiteObjects()
                    .penaltyLog()
                    .build()
            )
        }
    }

    companion object {
        private var INSTANCE: PhunApp? = null

        @JvmStatic
        fun get(): PhunApp = INSTANCE!!
    }
}