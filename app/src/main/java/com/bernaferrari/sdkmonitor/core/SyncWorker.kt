package com.bernaferrari.sdkmonitor.core

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bernaferrari.sdkmonitor.domain.repository.PreferencesRepository
import com.bernaferrari.sdkmonitor.notifications.NotificationManager
import com.orhanobut.logger.Logger
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val appManager: AppManager,
    private val preferencesRepository: PreferencesRepository,
    private val notificationManager: NotificationManager
) : CoroutineWorker(context, workerParameters) {

    private val debugLog = StringBuilder()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Logger.d("🚀 Starting background sync")
            
            performHeavyWork()
            
            Logger.d("✨ Background sync completed successfully!")
            Result.success()
        } catch (exception: Exception) {
            Logger.e(exception, "❌ Background sync failed")
            Result.failure()
        }
    }

    private suspend fun performHeavyWork() {
        debugLog.setLength(0)
        debugLog.appendLine("🔄 Starting app synchronization...")

        val preferences = preferencesRepository.getUserPreferences().first()
        val isDebugEnabled = preferences.backgroundSync // Use background sync preference for debug logging
        
        val packages = appManager.getPackagesWithUserPrefs()
        debugLog.appendLine("📱 Found ${packages.size} apps to process")

        packages.forEach { packageInfo ->
            try {
                appManager.insertNewApp(packageInfo)
                appManager.insertNewVersion(packageInfo)
                debugLog.appendLine("✅ Processed: ${packageInfo.packageName}")
            } catch (e: Exception) {
                debugLog.appendLine("❌ Failed to process: ${packageInfo.packageName} - ${e.message}")
                Logger.e(e, "Failed to process package: ${packageInfo.packageName}")
            }
        }

        if (isDebugEnabled) {
            notificationManager.showDebugSyncNotification(
                title = "🔄 Sync Complete",
                text = "Processed ${packages.size} apps with",
                bigText = debugLog.toString()
            )
        }
        
        debugLog.appendLine("🎉 Sync completed successfully!")
    }
}
