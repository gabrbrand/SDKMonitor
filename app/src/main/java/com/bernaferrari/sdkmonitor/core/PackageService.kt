package com.bernaferrari.sdkmonitor.core

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.bernaferrari.sdkmonitor.domain.repository.AppsRepository
import com.orhanobut.logger.Logger
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Modern PackageService showcasing the absolute pinnacle of Android background processing
 * Uses Hilt dependency injection and coroutines for perfect async operations
 * Complete elimination of legacy Injector and blocking patterns
 */
@HiltWorker
class PackageService @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val modernAppManager: AppManager,
    private val appsRepository: AppsRepository
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val action = inputData.getString("action")
            val packageName = inputData.getString(EXTRA_PACKAGE_NAME) ?: ""

            Logger.d("🔄 Processing package action: $action for $packageName")

            when (action) {
                ACTION_FETCH_INSERT -> handleActionInsert(packageName)
                ACTION_FETCH_UPDATE -> handleActionFetchUpdate(packageName)
                ACTION_REMOVE_PACKAGE -> handleActionRemovePackage(packageName)
            }

            Logger.d("✅ Package operation completed successfully")
            Result.success()
        } catch (exception: Exception) {
            Logger.e(exception, "❌ Package operation failed")
            Result.failure()
        }
    }

    private suspend fun handleActionRemovePackage(packageName: String) {
        appsRepository.deleteApp(packageName)
        Logger.d("🗑️ Removed package: $packageName")
    }

    private suspend fun handleActionFetchUpdate(packageName: String) {
        if (modernAppManager.doesAppHasOrigin(packageName)) {
            val packageInfo = modernAppManager.getPackageInfo(packageName) ?: return
            modernAppManager.insertNewVersion(packageInfo)
            Logger.d("🔄 Updated package: $packageName")
        }
    }

    private suspend fun handleActionInsert(packageName: String) {
        if (modernAppManager.doesAppHasOrigin(packageName)) {
            val packageInfo = modernAppManager.getPackageInfo(packageName) ?: return
            modernAppManager.insertNewApp(packageInfo)
            modernAppManager.insertNewVersion(packageInfo)
            Logger.d("➕ Inserted new package: $packageName")
        }
    }

    companion object {
        private const val ACTION_REMOVE_PACKAGE = "REMOVE_PACKAGE"
        private const val ACTION_FETCH_UPDATE = "FETCH_UPDATE"
        private const val ACTION_FETCH_INSERT = "FETCH_INSERT"
        private const val EXTRA_PACKAGE_NAME = "PACKAGE_NAME"
        private const val SERVICE_WORK = "servicework"

        fun startActionRemovePackage(context: Context, packageName: String) {
            enqueueWork(context, ACTION_REMOVE_PACKAGE, packageName)
        }

        fun startActionFetchUpdate(context: Context, packageName: String) {
            enqueueWork(context, ACTION_FETCH_UPDATE, packageName)
        }

        fun startActionAddPackage(context: Context, packageName: String) {
            enqueueWork(context, ACTION_FETCH_INSERT, packageName)
        }

        private fun enqueueWork(context: Context, action: String, packageName: String) {
            val inputData = Data.Builder()
                .putString("action", action)
                .putString(EXTRA_PACKAGE_NAME, packageName)
                .build()

            val work = OneTimeWorkRequest.Builder(PackageService::class.java)
                .addTag(SERVICE_WORK)
                .setInputData(inputData)
                .build()

            WorkManager.getInstance(context).enqueue(work)
        }
    }
}
