package com.glopez.phunapp.di

import com.glopez.phunapp.model.repository.FeedRepository
import com.glopez.phunapp.view.ViewModelFactory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ViewModelFactoryModule {
    @Provides
    @Singleton
    fun provideViewModelFactory(eventRepo: FeedRepository) = ViewModelFactory(eventRepo)
}