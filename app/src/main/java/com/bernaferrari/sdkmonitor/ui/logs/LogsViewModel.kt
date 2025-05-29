package com.bernaferrari.sdkmonitor.logs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bernaferrari.sdkmonitor.domain.model.LogEntry
import com.bernaferrari.sdkmonitor.domain.repository.AppsRepository
import com.bernaferrari.sdkmonitor.extensions.convertTimestampToDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LogsUiState {
    object Loading : LogsUiState()
    data class Success(
        val logs: List<LogEntry>,
        val totalCount: Int
    ) : LogsUiState()
    data class Error(val message: String) : LogsUiState()
}

@HiltViewModel
class LogsViewModel @Inject constructor(
    private val appsRepository: AppsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LogsUiState>(LogsUiState.Loading)
    val uiState: StateFlow<LogsUiState> = _uiState.asStateFlow()

    init {
        loadLogs()
    }

    fun loadLogs() {
        viewModelScope.launch {
            try {
                _uiState.value = LogsUiState.Loading
                
                val allVersions = appsRepository.getAllVersions()
                val allApps = appsRepository.getAllApps()
                
                val appMap = allApps.associateBy { it.packageName }
                
                val logEntries = allVersions.mapNotNull { version ->
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

    fun clearAllLogs() {
        viewModelScope.launch {
            try {
                appsRepository.clearAllLogs()
                loadLogs() // Reload to show empty state
            } catch (e: Exception) {
                _uiState.value = LogsUiState.Error(
                    e.message ?: "Failed to clear logs"
                )
            }
        }
    }

    fun refreshLogs() {
        loadLogs()
    }
}
