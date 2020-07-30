@file:Suppress("UNCHECKED_CAST")

package com.glopez.phunapp.testutil

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

object LiveDataTestUtil {

    /**
     * Get the value from a LiveData object by waiting 2 seconds for the LiveData
     * to emit. Once a notification is received via onChanged, observing is stopped.
     */
    fun <T> getLiveDataValue(liveData: LiveData<T>): T {
        val data = arrayOfNulls<Any>(1)
        val emitTimer = CountDownLatch(1)

        val observer = object : Observer<T> {
            override fun onChanged(t: T?) {
                data[0] = t
                emitTimer.countDown()
                liveData.removeObserver(this)
            }
        }

        liveData.observeForever(observer)
        emitTimer.await(2, TimeUnit.SECONDS)
        return data[0] as T
    }
}