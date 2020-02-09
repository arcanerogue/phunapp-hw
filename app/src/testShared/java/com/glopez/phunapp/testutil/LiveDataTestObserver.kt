package com.glopez.phunapp.testutil

import android.arch.lifecycle.Observer

class LiveDataTestObserver<T> : Observer<T> {
    private var data: T? = null

    override fun onChanged(t: T?) {
        data = t
    }

    fun getData(): T? {
        return data
    }

}