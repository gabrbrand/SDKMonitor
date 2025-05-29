package com.bernaferrari.sdkmonitor.settings

import com.bernaferrari.sdkmonitor.domain.model.AppFilter
import com.bernaferrari.sdkmonitor.domain.model.AppVersion
import com.bernaferrari.sdkmonitor.ui.components.SdkDistribution

/**
 * Modern UI state for Settings screen with granular control
 * Each preference can be updated independently while maintaining overall state consistency
 */
data class SettingsUiState(
    val isLoading: Boolean = true,
    val preferences: SettingsPreferences = SettingsPreferences(),
    val errorMessage: String? = null,
    val sdkDistribution: List<SdkDistribution> = emptyList(),
    val totalApps: Int = 0,
    val allAppsForSdk: List<AppVersion> = emptyList(),
) {
    val hasError: Boolean get() = errorMessage != null
    val isFullyLoaded: Boolean get() = !isLoading && !hasError
}

data class SettingsPreferences(
    val lightMode: Boolean = false,
    val appFilter: AppFilter = AppFilter.ALL_APPS, // Changed default to ALL_APPS
    val backgroundSync: Boolean = false,
    val orderBySdk: Boolean = false,
    val syncInterval: String = "30",
    val syncTimeUnit: TimeUnit = TimeUnit.MINUTES
)

enum class SettingType {
    LIGHT_MODE,
    APP_FILTER, // Use APP_FILTER instead of SHOW_SYSTEM_APPS
    BACKGROUND_SYNC,
    ORDER_BY_SDK,
    SYNC_INTERVAL
}

enum class TimeUnit(val displayName: String, val code: Int) {
    MINUTES("Minutes", 0),
    HOURS("Hours", 1),
    DAYS("Days", 2)
}
