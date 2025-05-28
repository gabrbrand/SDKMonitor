package com.bernaferrari.sdkmonitor.main

import com.bernaferrari.sdkmonitor.domain.model.AppVersion

/**
 * Modern UI state for the Main screen using sealed classes
 * Represents all possible states with type safety and composability
 */
sealed class MainUiState {
    data object Loading : MainUiState()
    
    data class Success(
        val apps: List<AppVersion>,
        val filteredApps: List<AppVersion> = apps,
        val totalCount: Int = apps.size
    ) : MainUiState()
    
    data class Error(
        val message: String,
        val throwable: Throwable? = null
    ) : MainUiState()
}
