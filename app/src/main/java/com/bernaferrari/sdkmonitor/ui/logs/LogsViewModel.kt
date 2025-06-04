package com.bernaferrari.sdkmonitor.ui.logs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bernaferrari.sdkmonitor.domain.model.AppFilter
import com.bernaferrari.sdkmonitor.domain.model.LogEntry
import com.bernaferrari.sdkmonitor.domain.repository.AppsRepository
import com.bernaferrari.sdkmonitor.domain.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

sealed class LogsUiState {
    data object Loading : LogsUiState()
    data class Success(
        val logs: List<LogEntry>,
        val totalCount: Int
    ) : LogsUiState()

    data class Error(val message: String) : LogsUiState()
}

@HiltViewModel
class LogsViewModel @Inject constructor(
    private val appsRepository: AppsRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LogsUiState>(LogsUiState.Loading)
    val uiState: StateFlow<LogsUiState> = _uiState.asStateFlow()

    init {
        // React to preference changes automatically
        viewModelScope.launch {
            preferencesRepository.getUserPreferences().collect { preferences ->
                loadLogsWithFilter(preferences.appFilter)
            }
        }
    }

    private fun loadLogsWithFilter(appFilter: AppFilter) {
        viewModelScope.launch {
            try {
                _uiState.value = LogsUiState.Loading

                val allVersions = appsRepository.getAllVersions()
                val allApps = appsRepository.getAllApps()

                val appMap = allApps.associateBy { it.packageName }

                // Filter apps based on the provided filter
                val filteredApps = when (appFilter) {
                    AppFilter.ALL_APPS -> allApps
                    AppFilter.USER_APPS -> allApps.filter { it.isFromPlayStore }
                    AppFilter.SYSTEM_APPS -> allApps.filter { !it.isFromPlayStore }
                }

                val filteredPackageNames = filteredApps.map { it.packageName }.toSet()

                val logEntries = allVersions
                    .filter { version -> version.packageName in filteredPackageNames }
                    .mapNotNull { version ->
                        val app = appMap[version.packageName] ?: return@mapNotNull null
                        LogEntry(
                            id = version.versionId.toLong(),
                            packageName = version.packageName,
                            appName = app.title,
                            oldSdk = null, // Previous SDK version would need to be tracked separately
                            newSdk = version.targetSdk,
                            oldVersion = null, // Previous version would need to be tracked separately
                            newVersion = version.versionName,
                            timestamp = version.lastUpdateTime
                        )
                    }.sortedByDescending { it.timestamp } // Most recent first

                _uiState.value = LogsUiState.Success(
                    logs = logEntries,
                    totalCount = logEntries.size
                )
            } catch (e: Exception) {
                _uiState.value = LogsUiState.Error(
                    e.message ?: "Failed to load logs"
                )
            }
        }
    }

    fun loadLogs() {
        viewModelScope.launch {
            val preferences = preferencesRepository.getUserPreferences().first()
            loadLogsWithFilter(preferences.appFilter)
        }
    }

    fun refreshLogs() {
        loadLogs()
    }

    fun getCurrentFilter(): AppFilter {
        return try {
            runBlocking {
                val preferences = preferencesRepository.getUserPreferences().first()
                preferences.appFilter
            }
        } catch (e: Exception) {
            AppFilter.ALL_APPS
        }
    }
}
