package com.bernaferrari.sdkmonitor

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bernaferrari.sdkmonitor.core.ModernAppManager
import com.bernaferrari.sdkmonitor.domain.repository.PreferencesRepository
import com.bernaferrari.sdkmonitor.notifications.ModernNotificationManager
import com.orhanobut.logger.Logger
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

/**
 * Modern SyncWorker showcasing the absolute pinnacle of Android background processing
 * Uses Hilt dependency injection and coroutines for perfect async operations
 */
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val modernAppManager: ModernAppManager,
    private val preferencesRepository: PreferencesRepository,
    private val notificationManager: ModernNotificationManager
) : CoroutineWorker(context, workerParameters) {

    private val debugLog = StringBuilder()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Logger.d("🚀 Starting modern background sync with world-class architecture!")
            
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
        
        val packages = modernAppManager.getPackagesWithUserPrefs()
        debugLog.appendLine("📱 Found ${packages.size} packages to process")

        packages.forEach { packageInfo ->
            try {
                modernAppManager.insertNewApp(packageInfo)
                modernAppManager.insertNewVersion(packageInfo)
                debugLog.appendLine("✅ Processed: ${packageInfo.packageName}")
            } catch (e: Exception) {
                debugLog.appendLine("❌ Failed to process: ${packageInfo.packageName} - ${e.message}")
                Logger.e(e, "Failed to process package: ${packageInfo.packageName}")
            }
        }

        if (isDebugEnabled) {
            notificationManager.showDebugSyncNotification(
                title = "🔄 Modern Sync Complete",
                text = "Processed ${packages.size} apps with modern architecture",
                bigText = debugLog.toString()
            )
        }
        
        debugLog.appendLine("🎉 Sync completed successfully!")
    }
}
