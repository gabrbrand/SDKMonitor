package com.bernaferrari.sdkmonitor.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bernaferrari.sdkmonitor.domain.model.AppFilter
import com.bernaferrari.sdkmonitor.domain.model.ThemeMode
import com.bernaferrari.sdkmonitor.domain.repository.AppsRepository
import com.bernaferrari.sdkmonitor.domain.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Enhanced Settings ViewModel with granular state management
 * Each preference update is handled independently with proper loading states
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val appsRepository: AppsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        observePreferences()
        loadAnalytics()
    }

    private fun observePreferences() {
        viewModelScope.launch {
            try {
                preferencesRepository.getUserPreferences()
                    .catch { e ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Failed to load settings: ${e.message}"
                        )
                    }
                    .collect { userPreferences ->
                        val (interval, timeUnit) = parseSyncInterval(userPreferences.syncInterval)
                        val preferences = SettingsPreferences(
                            themeMode = userPreferences.themeMode,
                            appFilter = userPreferences.appFilter,
                            backgroundSync = userPreferences.backgroundSync,
                            orderBySdk = userPreferences.orderBySdk,
                            syncInterval = interval,
                            syncTimeUnit = timeUnit
                        )

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            preferences = preferences,
                            errorMessage = null
                        )

                        // Refresh analytics when preferences change
                        loadAnalytics()
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to initialize settings: ${e.message}"
                )
            }
        }
    }

    private fun loadAnalytics() {
        viewModelScope.launch {
            try {
                val appFilter = _uiState.value.preferences.appFilter
                val allApps = appsRepository.getAllAppsAsAppVersions()

                // Filter apps based on current app filter preference - FIXED LOGIC
                val filteredApps = when (appFilter) {
                    AppFilter.USER_APPS -> allApps.filter { it.isFromPlayStore } // User apps
                    AppFilter.SYSTEM_APPS -> allApps.filter { !it.isFromPlayStore } // System apps  
                    AppFilter.ALL_APPS -> allApps // All apps
                }

                val distribution = filteredApps
                    .groupBy { it.sdkVersion }
                    .map { (sdk, appList) ->
                        SdkDistribution(
                            sdkVersion = sdk,
                            appCount = appList.size,
                            percentage = appList.size.toFloat() / filteredApps.size.coerceAtLeast(1)
                        )
                    }
                    .sortedByDescending { it.sdkVersion }

                _uiState.update { currentState ->
                    currentState.copy(
                        sdkDistribution = distribution,
                        totalApps = filteredApps.size,
                        allAppsForSdk = filteredApps
                    )
                }
            } catch (e: Exception) {
                // Handle error silently for analytics, it's not critical
            }
        }
    }

    /**
     * Update app filter preference - BEAUTIFUL PERSISTENT SETTING!
     */
    fun updateAppFilter(filter: AppFilter) {
        viewModelScope.launch {
            try {
                preferencesRepository.updateAppFilter(filter)
                // Analytics will auto-refresh through preference observer
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to update app filter: ${e.message}"
                )
            }
        }
    }

    /**
     * 🎨 Update theme mode with beautiful animations
     */
    fun updateThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            try {
                preferencesRepository.updateThemeMode(themeMode)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
//                    hasError = true,
                    errorMessage = "Failed to update theme: ${e.localizedMessage}"
                )
            }
        }
    }

    private fun parseSyncInterval(interval: String): Pair<String, TimeUnit> {
        // Parse interval like "30m", "1h", "2d", "7d", "30d" into number and unit
        return try {
            when {
                interval.endsWith("m") -> Pair(interval.dropLast(1), TimeUnit.MINUTES)
                interval.endsWith("h") -> Pair(interval.dropLast(1), TimeUnit.HOURS)
                interval.endsWith("d") -> Pair(interval.dropLast(1), TimeUnit.DAYS)
                // Handle legacy formats without unit suffix
                interval.toIntOrNull() != null -> {
                    val value = interval.toInt()
                    when {
                        value <= 24 -> Pair(interval, TimeUnit.HOURS)
                        value <= 168 -> Pair((value / 24).toString(), TimeUnit.DAYS)
                        else -> Pair("7", TimeUnit.DAYS)
                    }
                }

                else -> Pair("7", TimeUnit.DAYS) // Default to weekly
            }
        } catch (e: Exception) {
            Pair("7", TimeUnit.DAYS) // Default to weekly on any error
        }
    }

    private fun formatSyncInterval(interval: String, timeUnit: TimeUnit): String {
        return when (timeUnit) {
            TimeUnit.MINUTES -> "${interval}m"
            TimeUnit.HOURS -> "${interval}h"
            TimeUnit.DAYS -> "${interval}d"
        }
    }


    /**
     * Toggle order by SDK preference
     */
    fun toggleOrderBySdk() {
        viewModelScope.launch {
            try {
                val current = _uiState.value.preferences.orderBySdk
                preferencesRepository.updateOrderBySdk(!current)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to update order by SDK: ${e.message}"
                )
            }
        }
    }

    /**
     * Toggle background sync preference
     */
    fun toggleBackgroundSync() {
        viewModelScope.launch {
            try {
                val current = _uiState.value.preferences.backgroundSync
                preferencesRepository.updateBackgroundSync(!current)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to update background sync: ${e.message}"
                )
            }
        }
    }

    /**
     * Set sync interval with time unit
     */
    fun setSyncInterval(interval: String, timeUnit: TimeUnit) {
        viewModelScope.launch {
            try {
                val formattedInterval = formatSyncInterval(interval, timeUnit)
                preferencesRepository.updateSyncInterval(formattedInterval)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to update sync interval: ${e.message}"
                )
            }
        }
    }

    /**
     * Clear error state
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
