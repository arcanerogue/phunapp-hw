package com.glopez.phunapp.di

import com.glopez.phunapp.utils.DefaultDispatcherProvider
import com.glopez.phunapp.utils.DispatcherProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DispatcherModule {

    @Provides
    @Singleton
    fun provideDispatcher(): DispatcherProvider = DefaultDispatcherProvider()
}