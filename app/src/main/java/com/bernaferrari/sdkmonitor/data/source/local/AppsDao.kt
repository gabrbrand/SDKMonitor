package com.bernaferrari.sdkmonitor.data.source.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bernaferrari.sdkmonitor.data.App
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the apps table.
 * Modernized with Flow and Coroutines support.
 */
@Dao
interface AppsDao {

    @Query("SELECT * FROM apps WHERE (isFromPlayStore = :hasKnownOrigin) ORDER BY title COLLATE NOCASE ASC")
    fun getAppsListFlowFiltered(hasKnownOrigin: Boolean): Flow<List<App>>

    @Query("SELECT * FROM apps ORDER BY title COLLATE NOCASE ASC")
    fun getAppsListFlow(): Flow<List<App>>

    @Query("SELECT * FROM apps ORDER BY title COLLATE NOCASE ASC")
    fun getAppsListPaging(): PagingSource<Int, App>

    @Query("SELECT * FROM apps")
    suspend fun getAppsList(): List<App>

    @Query("SELECT packageName FROM apps WHERE packageName = :packageName LIMIT 1")
    suspend fun getAppString(packageName: String): String?

    @Query("SELECT * FROM apps WHERE packageName = :packageName LIMIT 1")
    suspend fun getApp(packageName: String): App?

    @Query("SELECT * FROM apps WHERE packageName = :packageName LIMIT 1")
    fun getAppFlow(packageName: String): Flow<App?>

    /**
     * Insert an app in the database. If the app already exists, replace it.
     *
     * @param app the app to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApp(app: App)

    /**
     * Insert multiple apps in the database. If any app already exists, replace it.
     *
     * @param apps the list of apps to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApps(apps: List<App>)

    /**
     * Update an app in the database.
     *
     * @param app the app to be updated.
     */
    @Update
    suspend fun updateApp(app: App)

    /**
     * Delete a specific app from the database
     *
     * @param packageName the package name of the app to be deleted.
     */
    @Query("DELETE FROM apps WHERE packageName = :packageName")
    suspend fun deleteApp(packageName: String)

    /**
     * Delete all apps from the database.
     */
    @Query("DELETE FROM apps")
    suspend fun deleteAllApps()

    /**
     * Get the number of apps in the database.
     */
    @Query("SELECT COUNT(*) FROM apps")
    suspend fun getAppsCount(): Int
}
