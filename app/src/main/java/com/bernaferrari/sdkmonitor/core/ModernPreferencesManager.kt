package com.bernaferrari.sdkmonitor.core

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Modern preferences manager using DataStore instead of legacy SharedPreferences/RxPrefs
 * Provides type-safe, coroutine-based preference management with Flow
 */
@Singleton
class ModernPreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")
    
    // Preference Keys
    private object PreferenceKeys {
        val LIGHT_MODE = booleanPreferencesKey("light_mode")
        val SHOW_SYSTEM_APPS = booleanPreferencesKey("show_system_apps")
        val BACKGROUND_SYNC = booleanPreferencesKey("background_sync")
        val ORDER_BY_SDK = booleanPreferencesKey("order_by_sdk")
        val SYNC_INTERVAL = stringPreferencesKey("sync_interval")
    }
    
    // Light Mode (Theme)
    val isLightMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.LIGHT_MODE] ?: true
    }
    
    suspend fun setLightMode(isLight: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.LIGHT_MODE] = isLight
        }
    }
    
    // Show System Apps
    val showSystemApps: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.SHOW_SYSTEM_APPS] ?: false
    }
    
    suspend fun setShowSystemApps(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.SHOW_SYSTEM_APPS] = show
        }
    }
    
    // Background Sync
    val backgroundSync: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.BACKGROUND_SYNC] ?: false
    }
    
    suspend fun setBackgroundSync(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.BACKGROUND_SYNC] = enabled
        }
    }
    
    // Order by SDK
    val orderBySdk: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.ORDER_BY_SDK] ?: false
    }
    
    suspend fun setOrderBySdk(orderBy: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.ORDER_BY_SDK] = orderBy
        }
    }
    
    // Sync Interval
    val syncInterval: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.SYNC_INTERVAL] ?: "301"
    }
    
    suspend fun setSyncInterval(interval: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.SYNC_INTERVAL] = interval
        }
    }
    
    // Additional method for getting sync interval as Flow (for compatibility)
    fun getSyncIntervalFlow(): Flow<String> = syncInterval
}
