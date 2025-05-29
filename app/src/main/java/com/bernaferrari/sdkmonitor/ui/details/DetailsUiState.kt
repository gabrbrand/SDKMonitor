package com.bernaferrari.sdkmonitor.ui.details

import com.bernaferrari.sdkmonitor.data.Version
import com.bernaferrari.sdkmonitor.domain.model.AppDetails
import com.bernaferrari.sdkmonitor.domain.model.AppVersion

/**
 * Sealed class representing the UI state for the Details screen
 * Modern approach using sealed classes for type-safe state management
 */
sealed class DetailsUiState {
    data object Loading : DetailsUiState()

    data class Success(
        val appDetails: AppDetails,
        val versions: List<AppVersion> = emptyList() // Changed from VersionInfo to AppVersion
    ) : DetailsUiState()

    data class Error(val message: String) : DetailsUiState()
}

/**
 * Data class representing version information for the app
 */
data class VersionInfo(
    val versionName: String,
    val versionCode: Long,
    val targetSdk: Int,
    val timestamp: Long,
    val changes: List<String> = emptyList()
)

/**
 * Extension function to convert Version to AppVersion
 */
fun Version.toAppVersion(appDetails: AppDetails) = AppVersion(
    packageName = this.packageName,
    title = appDetails.title,
    sdkVersion = this.targetSdk,
    versionName = this.versionName,
    versionCode = this.version,
    lastUpdateTime = formatTimestamp(this.lastUpdateTime)
)

/**
 * Extension function to convert Version to VersionInfo (kept for compatibility)
 */
fun Version.toVersionInfo() = VersionInfo(
    versionName = this.versionName,
    versionCode = this.version,
    targetSdk = this.targetSdk,
    timestamp = this.lastUpdateTime,
    changes = emptyList()
)

/**
 * Helper function to format timestamp to readable string
 */
private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60 * 1000 -> "Just now"
        diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)} minutes ago"
        diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)} hours ago"
        diff < 7 * 24 * 60 * 60 * 1000 -> "${diff / (24 * 60 * 60 * 1000)} days ago"
        else -> {
            val date = java.util.Date(timestamp)
            val formatter = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
            formatter.format(date)
        }
    }
}
