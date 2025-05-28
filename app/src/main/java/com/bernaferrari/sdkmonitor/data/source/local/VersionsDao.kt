package com.bernaferrari.sdkmonitor.data.source.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bernaferrari.sdkmonitor.data.Version
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the versions table.
 * Modernized with Flow and Coroutines support.
 */
@Dao
interface VersionsDao {

    @Query("SELECT targetSdk FROM versions WHERE packageName = :packageName ORDER BY version DESC LIMIT 1")
    suspend fun getLastTargetSDK(packageName: String): Int?

    @Query("SELECT * FROM versions WHERE packageName = :packageName ORDER BY version DESC LIMIT 1")
    suspend fun getLastValue(packageName: String): Version?

    @Query("SELECT * FROM versions WHERE packageName = :packageName ORDER BY version DESC")
    suspend fun getAllValues(packageName: String): List<Version>

    @Query("SELECT * FROM versions WHERE packageName = :packageName ORDER BY version DESC")
    fun getAllValuesFlow(packageName: String): Flow<List<Version>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertVersion(version: Version)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertVersions(versions: List<Version>)

    // this will only get versions where there is more than one version for the same package.
    // So, if a package was recently added, there is no reason to be there.
    @Query("SELECT t2.* FROM ( SELECT * FROM versions GROUP BY packageName HAVING COUNT(*) > 1 ) T1 JOIN versions T2 ON T1.packageName = T2.packageName ORDER BY lastUpdateTime DESC")
    fun getVersionsPaged(): PagingSource<Int, Version>

    @Query("SELECT COUNT(*) FROM ( SELECT * FROM versions GROUP BY packageName HAVING COUNT(*) > 1 ) T1 JOIN versions T2 ON T1.packageName = T2.packageName")
    suspend fun countNumberOfChanges(): Int

    @Query("SELECT t2.* FROM ( SELECT * FROM versions GROUP BY packageName HAVING COUNT(*) > 1 ) T1 JOIN versions T2 ON T1.packageName = T2.packageName ORDER BY lastUpdateTime DESC")
    fun getAllChangesFlow(): Flow<List<Version>>

    @Query("DELETE FROM versions WHERE packageName = :packageName")
    suspend fun deleteVersionsForPackage(packageName: String)

    @Query("DELETE FROM versions")
    suspend fun deleteAllVersions()

    @Query("SELECT * FROM versions ORDER BY lastUpdateTime DESC")
    suspend fun getAllVersionsSync(): List<Version>
}
