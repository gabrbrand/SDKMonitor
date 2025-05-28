package com.bernaferrari.sdkmonitor.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bernaferrari.sdkmonitor.core.ModernPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Enhanced Settings ViewModel with granular state management
 * Each preference update is handled independently with proper loading states
 */
@HiltViewModel
class ModernSettingsViewModel @Inject constructor(
    private val preferencesManager: ModernPreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        observePreferences()
    }

    private fun observePreferences() {
        viewModelScope.launch {
            try {
                combine(
                    preferencesManager.isLightMode,
                    preferencesManager.showSystemApps,
                    preferencesManager.backgroundSync,
                    preferencesManager.orderBySdk,
                    preferencesManager.syncInterval
                ) { lightMode, showSystemApps, backgroundSync, orderBySdk, syncInterval ->
                    val (interval, timeUnit) = parseSyncInterval(syncInterval)
                    SettingsPreferences(
                        lightMode = lightMode,
                        showSystemApps = showSystemApps,
                        backgroundSync = backgroundSync,
                        orderBySdk = orderBySdk,
                        syncInterval = interval,
                        syncTimeUnit = timeUnit
                    )
                }.catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Failed to load settings: ${e.message}"
                    )
                }.collect { preferences ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        preferences = preferences,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to initialize settings: ${e.message}"
                )
            }
        }
    }

    private fun parseSyncInterval(syncInterval: String): Pair<String, TimeUnit> {
        // Parse format like "30m", "2h", "1d" or just "30" (defaults to minutes)
        return when {
            syncInterval.endsWith("h") -> {
                val hours = syncInterval.dropLast(1)
                hours to TimeUnit.HOURS
            }

            syncInterval.endsWith("d") -> {
                val days = syncInterval.dropLast(1)
                days to TimeUnit.DAYS
            }

            syncInterval.endsWith("m") -> {
                val minutes = syncInterval.dropLast(1)
                minutes to TimeUnit.MINUTES
            }

            else -> syncInterval to TimeUnit.MINUTES
        }
    }

    private fun formatSyncInterval(interval: String, unit: TimeUnit): String {
        return when (unit) {
            TimeUnit.MINUTES -> "${interval}m"
            TimeUnit.HOURS -> "${interval}h"
            TimeUnit.DAYS -> "${interval}d"
        }
    }

    private suspend fun updateSetting(settingType: SettingType, action: suspend () -> Unit) {
        try {
            // Remove the loading state update since these are local preferences
            action()
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Failed to update ${settingType.name}: ${e.message}"
            )
        }
        // No finally block needed since we're not tracking loading states
    }

    fun toggleLightMode() {
        viewModelScope.launch {
            updateSetting(SettingType.LIGHT_MODE) {
                preferencesManager.setLightMode(!_uiState.value.preferences.lightMode)
            }
        }
    }

    fun toggleShowSystemApps() {
        viewModelScope.launch {
            updateSetting(SettingType.SHOW_SYSTEM_APPS) {
                preferencesManager.setShowSystemApps(!_uiState.value.preferences.showSystemApps)
            }
        }
    }

    fun toggleBackgroundSync() {
        viewModelScope.launch {
            updateSetting(SettingType.BACKGROUND_SYNC) {
                preferencesManager.setBackgroundSync(!_uiState.value.preferences.backgroundSync)
            }
        }
    }

    fun toggleOrderBySdk() {
        viewModelScope.launch {
            updateSetting(SettingType.ORDER_BY_SDK) {
                preferencesManager.setOrderBySdk(!_uiState.value.preferences.orderBySdk)
            }
        }
    }

    fun setSyncInterval(interval: String, unit: TimeUnit) {
        viewModelScope.launch {
            updateSetting(SettingType.SYNC_INTERVAL) {
                val formattedInterval = formatSyncInterval(interval, unit)
                preferencesManager.setSyncInterval(formattedInterval)
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
