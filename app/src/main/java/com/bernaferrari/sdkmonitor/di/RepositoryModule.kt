package com.bernaferrari.sdkmonitor.di

import com.bernaferrari.sdkmonitor.data.repository.AppsRepositoryImpl
import com.bernaferrari.sdkmonitor.data.repository.PreferencesRepositoryImpl
import com.bernaferrari.sdkmonitor.domain.repository.AppsRepository
import com.bernaferrari.sdkmonitor.domain.repository.PreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAppsRepository(
        appsRepositoryImpl: AppsRepositoryImpl
    ): AppsRepository

    @Binds
    @Singleton
    abstract fun bindPreferencesRepository(
        preferencesRepositoryImpl: PreferencesRepositoryImpl
    ): PreferencesRepository
}
