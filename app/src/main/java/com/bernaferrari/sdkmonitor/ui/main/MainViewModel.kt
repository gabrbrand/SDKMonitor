package com.bernaferrari.sdkmonitor.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bernaferrari.sdkmonitor.core.AppManager
import com.bernaferrari.sdkmonitor.domain.model.AppFilter
import com.bernaferrari.sdkmonitor.domain.model.AppVersion
import com.bernaferrari.sdkmonitor.domain.model.SortOption
import com.bernaferrari.sdkmonitor.domain.repository.AppsRepository
import com.bernaferrari.sdkmonitor.domain.repository.PreferencesRepository
import com.bernaferrari.sdkmonitor.extensions.normalizeString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MainUiState {
    data object Loading : MainUiState()
    data class Success(
        val apps: List<AppVersion>,
        val filteredApps: List<AppVersion>,
        val totalCount: Int
    ) : MainUiState()

    data class Error(val message: String, val throwable: Throwable? = null) : MainUiState()
}

@HiltViewModel
class MainViewModel @Inject constructor(
    appsRepository: AppsRepository,
    private val preferencesRepository: PreferencesRepository,
    private val appManager: AppManager
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _appFilter = MutableStateFlow(AppFilter.ALL_APPS)
    val appFilter: StateFlow<AppFilter> = _appFilter.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.NAME)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    private val _hasLoaded = MutableStateFlow(false)

    val uiState: StateFlow<MainUiState> = combine(
        appsRepository.getAppsWithVersions(),
        preferencesRepository.getUserPreferences(),
        _searchQuery,
        _hasLoaded
    ) { apps, prefs, query, hasLoaded ->
        try {
            // Update filter and sort states from preferences
            _appFilter.value = prefs.appFilter
            _sortOption.value = if (prefs.orderBySdk) SortOption.SDK else SortOption.NAME

            // Apply filtering based on preferences
            val filteredByPrefs = when (prefs.appFilter) {
                AppFilter.ALL_APPS -> apps
                AppFilter.USER_APPS -> apps.filter { it.isFromPlayStore }
                AppFilter.SYSTEM_APPS -> apps.filter { !it.isFromPlayStore }
            }

            // Apply ordering based on preferences  
            val orderedApps = if (prefs.orderBySdk) {
                filteredByPrefs.sortedWith(
                    compareByDescending<AppVersion> { it.sdkVersion }
                        .thenBy { it.title.lowercase() }
                )
            } else {
                filteredByPrefs.sortedBy { it.title.lowercase() }
            }

            // Apply search query
            val searchFiltered = if (query.isBlank()) {
                orderedApps
            } else {
                orderedApps.filter { appVersion ->
                    query.normalizeString() in appVersion.title.normalizeString()
                }
            }

            if (!hasLoaded && orderedApps.isEmpty()) {
                MainUiState.Loading
            } else {
                MainUiState.Success(
                    apps = orderedApps,
                    filteredApps = searchFiltered,
                    totalCount = orderedApps.size
                )
            }
        } catch (e: Exception) {
            MainUiState.Error(
                message = e.localizedMessage ?: "Failed to load apps",
                throwable = e
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainUiState.Loading
    )

    init {
        refreshAppsIfNeeded()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun loadApps() {
        refreshAppsIfNeeded()
    }

    fun updateAppFilter(filter: AppFilter) {
        viewModelScope.launch {
            preferencesRepository.updateAppFilter(filter)
        }
    }

    fun updateSortOption(option: SortOption) {
        viewModelScope.launch {
            preferencesRepository.updateOrderBySdk(option == SortOption.SDK)
        }
    }

    fun retryLoadApps() {
        refreshAppsIfNeeded()
    }

    private fun refreshAppsIfNeeded() {
        viewModelScope.launch {
            if (!_hasLoaded.value || uiState.value is MainUiState.Loading) {
                refreshAllApps()
            }
            _hasLoaded.value = true
        }
    }

    private suspend fun refreshAllApps() {
        try {
            val preferences = preferencesRepository.getUserPreferences().first()
            val installedApps = if (preferences.appFilter === AppFilter.ALL_APPS) {
                appManager.getPackages()
            } else {
                appManager.getPackagesWithUserPrefs()
            }

            // If no apps found (probably emulator), show system apps
            if (installedApps.isEmpty()) {
                preferencesRepository.updateAppFilter(AppFilter.ALL_APPS)
            }

            // Insert apps into database
            installedApps.forEach { packageInfo ->
                appManager.insertNewApp(packageInfo)
                appManager.insertNewVersion(packageInfo)
            }
        } catch (e: Exception) {
            // Handle error - could emit error state
            e.printStackTrace()
        }
    }

    /**
     * Force refresh when settings change
     */
    fun forceRefresh() {
        viewModelScope.launch {
            _hasLoaded.value = false
            refreshAllApps()
            _hasLoaded.value = true
        }
    }
}
