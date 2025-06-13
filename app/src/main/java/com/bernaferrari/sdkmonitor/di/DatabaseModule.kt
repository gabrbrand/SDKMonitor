package com.bernaferrari.sdkmonitor.di

import android.content.Context
import androidx.room.Room
import com.bernaferrari.sdkmonitor.data.source.local.AppDatabase
import com.bernaferrari.sdkmonitor.data.source.local.AppsDao
import com.bernaferrari.sdkmonitor.data.source.local.VersionsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase =
        Room
            .databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "Apps.db",
            ).fallbackToDestructiveMigration(false)
            .build()

    @Provides
    fun provideAppsDao(database: AppDatabase): AppsDao = database.snapsDao()

    @Provides
    fun provideVersionsDao(database: AppDatabase): VersionsDao = database.versionsDao()
}
