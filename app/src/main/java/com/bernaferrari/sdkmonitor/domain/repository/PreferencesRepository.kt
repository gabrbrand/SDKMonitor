package com.bernaferrari.sdkmonitor.domain.repository

import com.bernaferrari.sdkmonitor.domain.model.AppFilter
import com.bernaferrari.sdkmonitor.domain.model.ThemeMode
import com.bernaferrari.sdkmonitor.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

/**
 * Repository for user preferences using DataStore
 */
interface PreferencesRepository {
    /**
     * Get user preferences as a Flow
     */
    fun getUserPreferences(): Flow<UserPreferences>

    /**
     * Update background sync preference
     */
    suspend fun updateBackgroundSync(enabled: Boolean)

    /**
     * Update order by SDK preference
     */
    suspend fun updateOrderBySdk(enabled: Boolean)

    /**
     * Update sync interval preference
     */
    suspend fun updateSyncInterval(interval: String)

    /**
     * Update app filter preference (User/System/All apps)
     */
    suspend fun updateAppFilter(filter: AppFilter)

    /**
     * Update theme mode preference (Material You/Light/Dark/System)
     */
    suspend fun updateThemeMode(themeMode: ThemeMode)
}
