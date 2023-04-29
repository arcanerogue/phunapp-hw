package com.glopez.phunapp.di

import android.content.Context
import com.glopez.phunapp.model.db.EventDao
import com.glopez.phunapp.model.db.EventDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [ContextModule::class])
class DatabaseModule {
    @Provides
    @Singleton
    fun provideDao(context: Context): EventDao = EventDatabase.getDatabase(context).eventDao()
}