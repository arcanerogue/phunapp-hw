package com.glopez.phunapp.di

import com.glopez.phunapp.PhunApp

class Injector {
    companion object {
        fun get(): AppComponent = PhunApp.get().appComponent
    }
}