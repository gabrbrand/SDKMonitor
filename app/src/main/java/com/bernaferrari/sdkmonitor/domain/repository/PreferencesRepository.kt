package com.bernaferrari.sdkmonitor.domain.repository

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
     * Update light mode preference
     */
    suspend fun updateLightMode(enabled: Boolean)
    
    /**
     * Update show system apps preference
     */
    suspend fun updateShowSystemApps(enabled: Boolean)
    
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
}
