package com.bernaferrari.sdkmonitor.domain.repository

import androidx.paging.PagingData
import com.bernaferrari.sdkmonitor.data.App
import com.bernaferrari.sdkmonitor.data.Version
import com.bernaferrari.sdkmonitor.domain.model.AppVersion
import com.bernaferrari.sdkmonitor.domain.model.LogEntry
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface using Flow and coroutines
 */
interface AppsRepository {
    
    /**
     * Get all apps as a Flow
     */
    fun getAppsFlow(): Flow<List<App>>
    
    /**
     * Get all apps as AppVersion objects
     */
    fun getAppsWithVersions(): Flow<List<AppVersion>>
    
    /**
     * Get detailed information for a specific app
     * Note: This now only returns database info, not live system info
     */
    fun getAppFromDatabase(packageName: String): Flow<App?>
    
    /**
     * Get version history for a specific app
     */
    fun getAppVersionHistory(packageName: String): Flow<List<AppVersion>>
    
    /**
     * Get change logs
     */
    fun getAppChangeLogs(): Flow<List<LogEntry>>
    
    /**
     * Clear all change logs
     */
    suspend fun clearAllLogs()
    
    /**
     * Get versions for a specific package
     */
    fun getVersionsForPackage(packageName: String): Flow<List<Version>>
    
    /**
     * Get paginated versions
     */
    fun getVersionsPaged(): Flow<PagingData<Version>>
    
    /**
     * Insert or update an app
     */
    suspend fun insertApp(app: App)
    
    /**
     * Insert a new version
     */
    suspend fun insertVersion(version: Version)
    
    /**
     * Delete an app by package name
     */
    suspend fun deleteApp(packageName: String)
    
    /**
     * Get the last version for a package
     */
    suspend fun getLastVersion(packageName: String): Version?
    
    /**
     * Count of version changes
     */
    suspend fun getVersionChangesCount(): Int
    
    /**
     * Get all apps as a map (packageName -> App)
     */
    suspend fun getAppsMap(): Map<String, App>

    /**
     * Get all versions synchronously
     */
    suspend fun getAllVersions(): List<Version>
    
    /**
     * Get all apps synchronously  
     */
    suspend fun getAllApps(): List<App>
    
    /**
     * Get all apps as AppVersion domain objects with complete information
     */
    suspend fun getAllAppsAsAppVersions(): List<AppVersion>
    
    /**
     * Get a specific app by package name from database only
     */
    suspend fun getApp(packageName: String): App?
    
    /**
     * Get all versions for a specific app by package name
     */
    suspend fun getAppVersions(packageName: String): List<Version>
}
