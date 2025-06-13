package com.bernaferrari.sdkmonitor.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Immutable model class for a Version.
 * Inspired from Architecture Components MVVM sample app
 */
@Entity(
    tableName = "versions",
    indices = [(Index(value = ["packageName", "versionId"], unique = true))],
    foreignKeys = [
        (
            ForeignKey(
                entity = App::class,
                parentColumns = arrayOf("packageName"),
                childColumns = arrayOf("packageName"),
                onDelete = ForeignKey.CASCADE,
            )
        ),
    ],
)
data class Version(
    @PrimaryKey
    val versionId: Int,
    val version: Long,
    val packageName: String,
    val versionName: String,
    val lastUpdateTime: Long,
    val targetSdk: Int,
) {
    @Ignore
    constructor(
        version: Long,
        packageName: String,
        versionName: String,
        lastUpdateTime: Long,
        targetSdk: Int,
    ) : this(
        "$packageName $version $versionName $targetSdk".hashCode(),
        version,
        packageName,
        versionName,
        lastUpdateTime,
        targetSdk,
    )
}
