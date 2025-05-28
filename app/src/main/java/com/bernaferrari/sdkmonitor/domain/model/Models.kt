package com.bernaferrari.sdkmonitor.domain.model

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

/**
 * User preferences for the app
 */
data class UserPreferences(
    val lightMode: Boolean = true,
    val showSystemApps: Boolean = false,
    val backgroundSync: Boolean = false,
    val orderBySdk: Boolean = false,
    val syncInterval: String = "301"
)

enum class SortOrder {
    ALPHABETICAL,
    TARGET_SDK,
    LAST_UPDATE
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
