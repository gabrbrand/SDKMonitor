package com.bernaferrari.sdkmonitor.core

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.orhanobut.logger.Logger
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class PackageWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val appManager: AppManager
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val ACTION_ADD_PACKAGE = "ADD_PACKAGE"
        private const val ACTION_UPDATE_PACKAGE = "UPDATE_PACKAGE"
        private const val ACTION_REMOVE_PACKAGE = "REMOVE_PACKAGE"
        private const val KEY_ACTION = "action"
        private const val KEY_PACKAGE_NAME = "package_name"

        fun startActionAddPackage(context: Context, packageName: String) {
            val workRequest = OneTimeWorkRequestBuilder<PackageWorker>()
                .setInputData(
                    Data.Builder()
                        .putString(KEY_ACTION, ACTION_ADD_PACKAGE)
                        .putString(KEY_PACKAGE_NAME, packageName)
                        .build()
                )
                .build()
            WorkManager.getInstance(context).enqueue(workRequest)
        }

        fun startActionFetchUpdate(context: Context, packageName: String) {
            val workRequest = OneTimeWorkRequestBuilder<PackageWorker>()
                .setInputData(
                    Data.Builder()
                        .putString(KEY_ACTION, ACTION_UPDATE_PACKAGE)
                        .putString(KEY_PACKAGE_NAME, packageName)
                        .build()
                )
                .build()
            WorkManager.getInstance(context).enqueue(workRequest)
        }

        fun startActionRemovePackage(context: Context, packageName: String) {
            val workRequest = OneTimeWorkRequestBuilder<PackageWorker>()
                .setInputData(
                    Data.Builder()
                        .putString(KEY_ACTION, ACTION_REMOVE_PACKAGE)
                        .putString(KEY_PACKAGE_NAME, packageName)
                        .build()
                )
                .build()
            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }

    override suspend fun doWork(): Result {
        val action = inputData.getString(KEY_ACTION) ?: return Result.failure()
        val packageName = inputData.getString(KEY_PACKAGE_NAME) ?: return Result.failure()

        return try {
            when (action) {
                ACTION_ADD_PACKAGE -> handleAddPackage(packageName)
                ACTION_UPDATE_PACKAGE -> handleUpdatePackage(packageName)
                ACTION_REMOVE_PACKAGE -> handleRemovePackage(packageName)
                else -> Result.failure()
            }
            Result.success()
        } catch (e: Exception) {
            Logger.e(e, "❌ Failed to handle package action: $action for $packageName")
            Result.failure()
        }
    }

    private suspend fun handleAddPackage(packageName: String) {
        try {
            Logger.d("📦 Adding package: $packageName")
            val packageInfo = appManager.getPackageInfo(packageName)
            if (packageInfo != null) {
                appManager.insertNewApp(packageInfo)
                appManager.insertNewVersion(packageInfo)
                Logger.d("✅ Successfully added package: $packageName")
            } else {
                Logger.w("⚠️ Package not found: $packageName")
            }
        } catch (e: Exception) {
            Logger.e(e, "❌ Failed to add package: $packageName")
            throw e
        }
    }

    private suspend fun handleUpdatePackage(packageName: String) {
        try {
            Logger.d("🔄 Updating package: $packageName")
            val packageInfo = appManager.getPackageInfo(packageName)
            if (packageInfo != null) {
                appManager.insertNewVersion(packageInfo)
                Logger.d("✅ Successfully updated package: $packageName")
            } else {
                Logger.w("⚠️ Package not found for update: $packageName")
                // Package might be uninstalled, remove it
                handleRemovePackage(packageName)
            }
        } catch (e: Exception) {
            Logger.e(e, "❌ Failed to update package: $packageName")
            throw e
        }
    }

    private suspend fun handleRemovePackage(packageName: String) {
        try {
            Logger.d("🗑️ Removing package: $packageName")
            appManager.removePackageName(packageName)
            Logger.d("✅ Successfully removed package: $packageName")
        } catch (e: Exception) {
            Logger.e(e, "❌ Failed to remove package: $packageName")
            throw e
        }
    }
}
