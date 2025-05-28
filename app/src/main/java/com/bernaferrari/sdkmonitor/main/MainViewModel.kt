package com.bernaferrari.sdkmonitor.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bernaferrari.sdkmonitor.core.ModernAppManager
import com.bernaferrari.sdkmonitor.domain.model.AppVersion
import com.bernaferrari.sdkmonitor.domain.repository.AppsRepository
import com.bernaferrari.sdkmonitor.extensions.normalizeString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Modern ViewModel showcasing the pinnacle of Android architecture
 * Uses StateFlow and sealed classes for perfect state management
 * Complete elimination of legacy RxJava and MvRx patterns
 */
@OptIn(FlowPreview::class)
@HiltViewModel
class MainViewModel @Inject constructor(
    private val appsRepository: AppsRepository,
    private val modernAppManager: ModernAppManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var allApps: List<AppVersion> = emptyList()
    private var hasLoadedApps = false

    init {
        // Set up search query debouncing
        searchQuery
            .debounce(300)
            .onEach { query ->
                filterApps(query)
            }
            .launchIn(viewModelScope)
    }

    /**
     * Load all apps with modern coroutines and StateFlow
     */
    fun loadApps() {
        viewModelScope.launch {
            try {
                _uiState.value = MainUiState.Loading
                
                // Refresh apps if first load or force refresh needed
                if (!hasLoadedApps || modernAppManager.forceRefresh) {
                    refreshAllApps()
                    modernAppManager.forceRefresh = false
                    hasLoadedApps = true
                }
                
                // Get apps from repository
                allApps = appsRepository.getAllAppsAsAppVersions()
                
                if (allApps.isEmpty()) {
                    // For emulators or fresh installs, enable system apps
                    // and try to get packages from device
                    refreshAllApps()
                    allApps = appsRepository.getAllAppsAsAppVersions()
                }
                
                // Apply current search filter
                filterApps(_searchQuery.value)
                
            } catch (exception: Exception) {
                _uiState.value = MainUiState.Error(
                    message = exception.localizedMessage ?: "Failed to load apps",
                    throwable = exception
                )
            }
        }
    }

    /**
     * Update search query with modern StateFlow
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /**
     * Filter apps based on search query
     */
    private fun filterApps(query: String) {
        val filteredApps = if (query.isBlank()) {
            allApps
        } else {
            allApps.filter { appVersion ->
                query.normalizeString() in appVersion.title.normalizeString()
            }
        }
        
        _uiState.value = MainUiState.Success(
            apps = allApps,
            filteredApps = filteredApps,
            totalCount = allApps.size
        )
    }

    /**
     * Refresh all apps from device packages
     */
    private suspend fun refreshAllApps() {
        try {
            val packages = modernAppManager.getPackagesWithUserPrefs()
            
            // If no packages found (emulator), enable system apps
            if (packages.isEmpty()) {
                // TODO: Update preferences with modern DataStore
                // For now, we'll handle this through the repository
            }
            
            packages.forEach { packageInfo ->
                modernAppManager.insertNewApp(packageInfo)
                modernAppManager.insertNewVersion(packageInfo)
            }
        } catch (exception: Exception) {
            // Log error but don't fail the whole operation
            println("Error refreshing apps: ${exception.message}")
        }
    }

    /**
     * Retry loading apps after an error
     */
    fun retryLoadApps() {
        loadApps()
    }
}
