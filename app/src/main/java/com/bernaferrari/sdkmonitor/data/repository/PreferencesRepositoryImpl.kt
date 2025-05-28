package com.bernaferrari.sdkmonitor.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.bernaferrari.sdkmonitor.domain.model.UserPreferences
import com.bernaferrari.sdkmonitor.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : PreferencesRepository {

    companion object {
        private val LIGHT_MODE_KEY = booleanPreferencesKey("light_mode")
        private val SHOW_SYSTEM_APPS_KEY = booleanPreferencesKey("show_system_apps")
        private val BACKGROUND_SYNC_KEY = booleanPreferencesKey("background_sync")
        private val ORDER_BY_SDK_KEY = booleanPreferencesKey("order_by_sdk")
        private val SYNC_INTERVAL_KEY = stringPreferencesKey("sync_interval")
    }

    override fun getUserPreferences(): Flow<UserPreferences> {
        return dataStore.data.map { preferences ->
            UserPreferences(
                lightMode = preferences[LIGHT_MODE_KEY] ?: true,
                showSystemApps = preferences[SHOW_SYSTEM_APPS_KEY] ?: false,
                backgroundSync = preferences[BACKGROUND_SYNC_KEY] ?: false,
                orderBySdk = preferences[ORDER_BY_SDK_KEY] ?: false,
                syncInterval = preferences[SYNC_INTERVAL_KEY] ?: "301"
            )
        }
    }

    override suspend fun updateLightMode(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[LIGHT_MODE_KEY] = enabled
        }
    }

    override suspend fun updateShowSystemApps(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[SHOW_SYSTEM_APPS_KEY] = enabled
        }
    }

    override suspend fun updateBackgroundSync(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[BACKGROUND_SYNC_KEY] = enabled
        }
    }

    override suspend fun updateOrderBySdk(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[ORDER_BY_SDK_KEY] = enabled
        }
    }

    override suspend fun updateSyncInterval(interval: String) {
        dataStore.edit { preferences ->
            preferences[SYNC_INTERVAL_KEY] = interval
        }
    }
}
