package com.bernaferrari.sdkmonitor.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.orhanobut.logger.Logger

/**
 * Modern PackageReceiver showcasing the pinnacle of Android broadcast handling
 * Efficiently processes package install/update/remove events with proper error handling
 * Designed for 10+ years of maintainability and future Android API compatibility
 */
class PackageReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        try {
            Logger.d("📦 Package event received: ${intent.action}")

            val packageName = intent.data?.encodedSchemeSpecificPart
            if (packageName.isNullOrBlank()) {
                Logger.w("⚠️ Package name is null or empty, ignoring event")
                return
            }

            when (intent.action) {
                Intent.ACTION_PACKAGE_ADDED -> {
                    Logger.d("➕ Package installed: $packageName")
                    PackageService.startActionAddPackage(context,packageName)
                }

                Intent.ACTION_PACKAGE_REPLACED -> {
                    Logger.d("🔄 Package updated: $packageName")
                    PackageService.startActionFetchUpdate(context, packageName)
                }

                Intent.ACTION_PACKAGE_FULLY_REMOVED -> {
                    Logger.d("🗑️ Package uninstalled: $packageName")
                    PackageService.startActionRemovePackage(context, packageName)
                }

                else -> {
                    Logger.d("🤷 Unknown package action: ${intent.action}")
                }
            }
        } catch (exception: Exception) {
            Logger.e(exception, "❌ Failed to process package event")
            // Continue gracefully - don't crash the system
        }
    }
}
