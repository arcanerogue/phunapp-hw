package com.glopez.phunapp.di

import com.glopez.phunapp.model.db.EventDao
import com.glopez.phunapp.model.network.FeedProvider
import com.glopez.phunapp.model.repository.EventFeedRepository
import com.glopez.phunapp.model.repository.FeedRepository
import com.glopez.phunapp.utils.DispatcherProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [DatabaseModule::class])
class RepoModule {

    @Provides
    @Singleton
    fun provideEventRepo(
        eventApi: FeedProvider,
        eventDao: EventDao,
        dispatchers: DispatcherProvider
    ): FeedRepository = EventFeedRepository(eventApi, eventDao, dispatchers)
}