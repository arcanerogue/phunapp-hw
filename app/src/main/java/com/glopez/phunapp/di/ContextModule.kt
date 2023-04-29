package com.glopez.phunapp.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class ContextModule(private val app: Application) {
    @Provides
    fun provideContext(): Context = app
}
