package com.bernaferrari.sdkmonitor.ui.details

import android.content.Context
import com.bernaferrari.sdkmonitor.R
import com.bernaferrari.sdkmonitor.data.Version
import com.bernaferrari.sdkmonitor.domain.model.AppDetails
import com.bernaferrari.sdkmonitor.domain.model.AppVersion

/**
 * Sealed class representing the UI state for the Details screen
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
fun Version.toAppVersion(appDetails: AppDetails, context: Context) = AppVersion(
    packageName = this.packageName,
    title = appDetails.title,
    sdkVersion = this.targetSdk,
    versionName = this.versionName,
    versionCode = this.version,
    lastUpdateTime = formatTimestamp(this.lastUpdateTime, context)
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
 * Helper function to format timestamp to readable string with multilingual support
 */
private fun formatTimestamp(timestamp: Long, context: Context): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60 * 1000 -> context.getString(R.string.just_now)
        diff < 60 * 60 * 1000 -> {
            val minutes = (diff / (60 * 1000)).toInt()
            context.resources.getQuantityString(R.plurals.minutes_ago, minutes, minutes)
        }
        diff < 24 * 60 * 60 * 1000 -> {
            val hours = (diff / (60 * 60 * 1000)).toInt()
            context.resources.getQuantityString(R.plurals.hours_ago, hours, hours)
        }
        diff < 7 * 24 * 60 * 60 * 1000 -> {
            val days = (diff / (24 * 60 * 60 * 1000)).toInt()
            context.resources.getQuantityString(R.plurals.days_ago, days, days)
        }
        else -> {
            val date = java.util.Date(timestamp)
            val formatter = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
            formatter.format(date)
        }
    }
}
