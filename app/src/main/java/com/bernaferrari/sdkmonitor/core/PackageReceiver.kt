package com.bernaferrari.sdkmonitor.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.github.aakira.napier.Napier

class PackageReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        try {
            Napier.d("📦 Package event received: ${intent.action}")

            val packageName = intent.data?.encodedSchemeSpecificPart
            if (packageName.isNullOrBlank()) {
                Napier.w("⚠️ Package name is null or empty, ignoring event")
                return
            }

            when (intent.action) {
                Intent.ACTION_PACKAGE_ADDED -> {
                    Napier.d("➕ Package installed: $packageName")
                    PackageWorker.startActionAddPackage(context, packageName)
                }

                Intent.ACTION_PACKAGE_REPLACED -> {
                    Napier.d("🔄 Package updated: $packageName")
                    PackageWorker.startActionFetchUpdate(context, packageName)
                }

                Intent.ACTION_PACKAGE_FULLY_REMOVED -> {
                    Napier.d("🗑️ Package uninstalled: $packageName")
                    PackageWorker.startActionRemovePackage(context, packageName)
                }

                else -> {
                    Napier.d("🤷 Unknown package action: ${intent.action}")
                }
            }
        } catch (exception: Exception) {
            Napier.e("❌ Failed to process package event", exception)
        }
    }
}
