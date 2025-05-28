package com.bernaferrari.sdkmonitor.settings

/**
 * Modern UI state for Settings screen with granular control
 * Each preference can be updated independently while maintaining overall state consistency
 */
data class SettingsUiState(
    val isLoading: Boolean = true,
    val preferences: SettingsPreferences = SettingsPreferences(),
    val errorMessage: String? = null
) {
    val hasError: Boolean get() = errorMessage != null
    val isFullyLoaded: Boolean get() = !isLoading && !hasError
}

data class SettingsPreferences(
    val lightMode: Boolean = false,
    val showSystemApps: Boolean = false,
    val backgroundSync: Boolean = false,
    val orderBySdk: Boolean = false,
    val syncInterval: String = "30",
    val syncTimeUnit: TimeUnit = TimeUnit.MINUTES
)

enum class SettingType {
    LIGHT_MODE,
    SHOW_SYSTEM_APPS,
    BACKGROUND_SYNC,
    ORDER_BY_SDK,
    SYNC_INTERVAL
}

enum class TimeUnit(val displayName: String, val code: Int) {
    MINUTES("Minutes", 0),
    HOURS("Hours", 1),
    DAYS("Days", 2)
}
