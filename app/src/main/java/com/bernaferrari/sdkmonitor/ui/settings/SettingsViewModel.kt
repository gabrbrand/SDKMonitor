package com.bernaferrari.sdkmonitor.ui.settings

import android.content.Context
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
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * Enhanced Settings ViewModel with granular state management
 * Each preference update is handled independently with proper loading states
 */
@HiltViewModel
class SettingsViewModel
    @Inject
    constructor(
        private val preferencesRepository: PreferencesRepository,
        private val appsRepository: AppsRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(SettingsUiState())
        val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

        init {
            observePreferences()
        }

        private fun observePreferences() {
            viewModelScope.launch {
                try {
                    preferencesRepository
                        .getUserPreferences()
                        .catch { e ->
                            _uiState.value =
                                _uiState.value.copy(
                                    isLoading = false,
                                    errorMessage = "Failed to load settings: ${e.message}",
                                )
                        }.collect { userPreferences ->
                            val (interval, timeUnit) = parseSyncInterval(userPreferences.syncInterval)
                            val preferences =
                                SettingsPreferences(
                                    themeMode = userPreferences.themeMode,
                                    appFilter = userPreferences.appFilter,
                                    backgroundSync = userPreferences.backgroundSync,
                                    syncInterval = interval,
                                    syncTimeUnit = timeUnit,
                                )

                            _uiState.value =
                                _uiState.value.copy(
                                    isLoading = false,
                                    preferences = preferences,
                                    errorMessage = null,
                                )

                            // Only reload analytics if this is the first load or filter changed
                            val shouldReloadAnalytics =
                                _uiState.value.sdkDistribution.isEmpty() ||
                                    _uiState.value.preferences.appFilter != userPreferences.appFilter

                            if (shouldReloadAnalytics) {
                                loadAnalytics()
                            }
                        }
                } catch (e: Exception) {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Failed to initialize settings: ${e.message}",
                        )
                }
            }
        }

        private fun loadAnalytics() {
            viewModelScope.launch {
                _uiState.update { it.copy(isAnalyticsLoading = true) }

                try {
                    val appFilter = _uiState.value.preferences.appFilter
                    val allApps = appsRepository.getAllAppsAsAppVersions()

                    // Filter apps based on current app filter preference
                    val filteredApps =
                        when (appFilter) {
                            AppFilter.USER_APPS -> allApps.filter { it.isFromPlayStore }
                            AppFilter.SYSTEM_APPS -> allApps.filter { !it.isFromPlayStore }
                            AppFilter.ALL_APPS -> allApps
                        }

                    val distribution =
                        if (filteredApps.isNotEmpty()) {
                            filteredApps
                                .groupBy { it.sdkVersion }
                                .map { (sdk, appList) ->
                                    SdkDistribution(
                                        sdkVersion = sdk,
                                        appCount = appList.size,
                                        percentage = appList.size.toFloat() / filteredApps.size,
                                    )
                                }.sortedByDescending { it.sdkVersion }
                        } else {
                            emptyList()
                        }

                    _uiState.update { currentState ->
                        currentState.copy(
                            isAnalyticsLoading = false,
                            sdkDistribution = distribution,
                            totalApps = filteredApps.size,
                            allAppsForSdk = filteredApps,
                        )
                    }
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(
                            isAnalyticsLoading = false,
                            errorMessage = "Failed to load analytics: ${e.message}",
                        )
                    }
                }
            }
        }

        fun updateAppFilter(filter: AppFilter) {
            viewModelScope.launch {
                try {
                    preferencesRepository.updateAppFilter(filter)
                    // Analytics will auto-refresh through preference observer
                } catch (e: Exception) {
                    _uiState.value =
                        _uiState.value.copy(
                            errorMessage = "Failed to update app filter: ${e.message}",
                        )
                }
            }
        }

        fun updateThemeMode(themeMode: ThemeMode) {
            viewModelScope.launch {
                try {
                    preferencesRepository.updateThemeMode(themeMode)
                } catch (e: Exception) {
                    _uiState.value =
                        _uiState.value.copy(
//                    hasError = true,
                            errorMessage = "Failed to update theme: ${e.localizedMessage}",
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

        private fun formatSyncInterval(
            interval: String,
            timeUnit: TimeUnit,
        ): String =
            when (timeUnit) {
                TimeUnit.MINUTES -> "${interval}m"
                TimeUnit.HOURS -> "${interval}h"
                TimeUnit.DAYS -> "${interval}d"
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
                    _uiState.value =
                        _uiState.value.copy(
                            errorMessage = "Failed to update background sync: ${e.message}",
                        )
                }
            }
        }

        /**
         * Set sync interval with time unit
         */
        fun setSyncInterval(
            interval: String,
            timeUnit: TimeUnit,
        ) {
            viewModelScope.launch {
                try {
                    val formattedInterval = formatSyncInterval(interval, timeUnit)
                    preferencesRepository.updateSyncInterval(formattedInterval)
                } catch (e: Exception) {
                    _uiState.value =
                        _uiState.value.copy(
                            errorMessage = "Failed to update sync interval: ${e.message}",
                        )
                }
            }
        }

        /**
         * Clear all logs/data
         */
        fun clearAllLogs() {
            viewModelScope.launch {
                try {
                    appsRepository.clearAllLogs()
                } catch (e: Exception) {
                    _uiState.value =
                        _uiState.value.copy(
                            errorMessage = "Failed to clear logs: ${e.message}",
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

        /**
         * Export all app and version data to CSV
         * Returns the created file for the UI to handle sharing
         */
        suspend fun exportDataToCsv(context: Context): File? =
            try {
                val versions = appsRepository.getAllVersions()

                val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.getDefault()).format(Date())
                val fileName = "SDK_Monitor_Export_$timestamp.csv"

                val file = File(context.getExternalFilesDir(null), fileName)

                file.bufferedWriter().use { writer ->
                    // Write header with export info
                    writer.write("# SDK Monitor Data Export\n")
                    writer.write(
                        "# Generated on: ${
                            SimpleDateFormat(
                                "yyyy-MM-dd HH:mm:ss",
                                Locale.getDefault(),
                            ).format(Date())
                        }\n",
                    )
                    writer.write("# Total Versions: ${versions.size}\n")
                    writer.write("#\n")

                    // Write CSV header
                    writer.write("packageName,versionId,version,versionName,lastUpdateTime,targetSdk\n")

                    // Write versions data
                    versions.forEach { version ->
                        writer.write(
                            "${version.packageName},${version.versionId},${version.version},\"${
                                version.versionName.replace(
                                    "\"",
                                    "\"\"",
                                )
                            }\",${version.lastUpdateTime},${version.targetSdk}\n",
                        )
                    }
                }

                file
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        errorMessage = "Failed to export data: ${e.message}",
                    )
                null
            }
    }
