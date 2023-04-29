package com.glopez.phunapp.di

import com.glopez.phunapp.view.ViewModelFactory
import dagger.Component
import javax.inject.Singleton

@Component(
    modules = [ContextModule::class,
        DatabaseModule::class,
        NetworkModule::class,
        DispatcherModule::class,
        RepoModule::class]
)

@Singleton
interface AppComponent {
    fun getViewModelFactory(): ViewModelFactory
}