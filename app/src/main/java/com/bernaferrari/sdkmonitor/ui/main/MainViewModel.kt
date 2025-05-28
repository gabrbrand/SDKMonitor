package com.bernaferrari.sdkmonitor.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bernaferrari.sdkmonitor.core.ModernAppManager
import com.bernaferrari.sdkmonitor.domain.model.AppVersion
import com.bernaferrari.sdkmonitor.domain.repository.AppsRepository
import com.bernaferrari.sdkmonitor.domain.repository.PreferencesRepository
import com.bernaferrari.sdkmonitor.extensions.normalizeString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MainUiState(
    val apps: List<AppVersion> = emptyList(),
    val filteredApps: List<AppVersion> = emptyList(),
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val hasLoaded: Boolean = false,
    val maxListSize: Int = 0
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val appsRepository: AppsRepository,
    private val preferencesRepository: PreferencesRepository,
    private val modernAppManager: ModernAppManager
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _hasLoaded = MutableStateFlow(false)

    val uiState: StateFlow<MainUiState> = combine(
        appsRepository.getAppsWithVersions(),
        _searchQuery,
        _hasLoaded
    ) { apps, query, hasLoaded ->
        val filteredApps = if (query.isBlank()) {
            apps
        } else {
            apps.filter { appVersion ->
                query.normalizeString() in modernAppManager.getAppTitle(appVersion.packageName).normalizeString() 
            }
        }

        MainUiState(
            apps = apps,
            filteredApps = filteredApps,
            isLoading = !hasLoaded && apps.isEmpty(),
            searchQuery = query,
            hasLoaded = hasLoaded,
            maxListSize = apps.size
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainUiState()
    )

    init {
        refreshAppsIfNeeded()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private fun refreshAppsIfNeeded() {
        viewModelScope.launch {
            if (!_hasLoaded.value || uiState.value.apps.isEmpty()) {
                refreshAllApps()
            }
            _hasLoaded.value = true
        }
    }

    private suspend fun refreshAllApps() {
        try {
            val installedApps = modernAppManager.getAllInstalledApps()
            
            // If no apps found (probably emulator), show system apps
            if (installedApps.isEmpty()) {
                preferencesRepository.updateShowSystemApps(true)
            }
            
            // Insert apps into database
            installedApps.forEach { app ->
                appsRepository.insertApp(app)
            }
            
            // Insert current versions
            installedApps.forEach { app ->
                val currentVersion = modernAppManager.getCurrentVersion(app.packageName)
                currentVersion?.let { version ->
                    appsRepository.insertVersion(version)
                }
            }
        } catch (e: Exception) {
            // Handle error - could emit error state
            e.printStackTrace()
        }
    }
}
