package com.bernaferrari.sdkmonitor.domain.model

import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Domain model representing detailed app information
 */
data class AppDetails(
    val packageName: String,
    val title: String,
    val versionName: String,
    val versionCode: Long,
    val targetSdk: Int,
    val minSdk: Int,
    val size: Long,
    val lastUpdateTime: String,
    val isSystemApp: Boolean = false
)

/**
 * Domain model representing an app with its current SDK version and metadata
 * This is the main AppVersion model used throughout the modern app
 */
data class AppVersion(
    val packageName: String,
    val title: String,
    val sdkVersion: Int,
    val lastUpdateTime: String,
    val versionName: String = "",
    val versionCode: Long = 0L,
    val backgroundColor: Int = 0,
    val isFromPlayStore: Boolean = false
)

enum class AppFilter(val displayName: String) {
    ALL_APPS("All"),
    USER_APPS("User"),
    SYSTEM_APPS("System")
}

/**
 * User preferences for the app - NOW WITH PERSISTENT APP FILTER!
 */
data class UserPreferences(
    val lightMode: Boolean = true,
    val appFilter: AppFilter = AppFilter.ALL_APPS, // Changed default to ALL_APPS
    val backgroundSync: Boolean = false,
    val orderBySdk: Boolean = false,
    val syncInterval: String = "30m", // Fixed default format,
    val themeMode: ThemeMode = ThemeMode.MATERIAL_YOU
)


/**
 * Sort options for the main screen
 */
enum class SortOption(val displayName: String, val icon: ImageVector) {
    NAME("Name", androidx.compose.material.icons.Icons.Default.SortByAlpha),
    SDK("Target SDK", androidx.compose.material.icons.Icons.Default.Android)
}

/**
 * Domain model for app change logs
 */
data class LogEntry(
    val id: Long,
    val packageName: String,
    val appName: String,
    val oldSdk: Int?,
    val newSdk: Int,
    val oldVersion: String?,
    val newVersion: String,
    val timestamp: Long
)
