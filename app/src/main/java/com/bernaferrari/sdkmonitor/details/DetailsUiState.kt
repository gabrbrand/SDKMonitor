package com.bernaferrari.sdkmonitor.details

import com.bernaferrari.sdkmonitor.data.App
import com.bernaferrari.sdkmonitor.data.Version
import com.bernaferrari.sdkmonitor.domain.model.AppDetails

/**
 * Sealed class representing the UI state for the Details screen
 * Modern approach using sealed classes for type-safe state management
 */
sealed class DetailsUiState {
    object Loading : DetailsUiState()
    
    data class Success(
        val appDetails: AppDetails,
        val versions: List<VersionInfo> = emptyList()
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
 * Extension function to convert Version to VersionInfo
 */
fun Version.toVersionInfo() = VersionInfo(
    versionName = this.versionName,
    versionCode = this.version, // Note: Version entity uses 'version' field for versionCode
    targetSdk = this.targetSdk,
    timestamp = this.lastUpdateTime, // Note: Version entity uses 'lastUpdateTime' not 'timestamp'
    changes = emptyList() // This can be populated based on app changes if available
)
