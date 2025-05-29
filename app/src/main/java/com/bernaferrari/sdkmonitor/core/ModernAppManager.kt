package com.bernaferrari.sdkmonitor.core

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import com.bernaferrari.sdkmonitor.MainActivity
import com.bernaferrari.sdkmonitor.R
import com.bernaferrari.sdkmonitor.data.App
import com.bernaferrari.sdkmonitor.data.Version
import com.bernaferrari.sdkmonitor.domain.model.AppDetails
import com.bernaferrari.sdkmonitor.domain.model.AppFilter
import com.bernaferrari.sdkmonitor.domain.repository.AppsRepository
import com.bernaferrari.sdkmonitor.domain.repository.PreferencesRepository
import com.bernaferrari.sdkmonitor.extensions.convertTimestampToDate
import com.bernaferrari.sdkmonitor.extensions.darken
import dagger.hilt.android.qualifiers.ApplicationContext
import com.bernaferrari.sdkmonitor.notifications.ModernNotificationManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModernAppManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appsRepository: AppsRepository,
    private val preferencesRepository: PreferencesRepository,
    private val notificationManager: ModernNotificationManager
) {

    private companion object {
        const val PACKAGE_ANDROID_VENDING = "com.android.vending"
        const val OUTSIDE_STORE = "com.google.android.packageinstaller"
    }

    private val packageManager: PackageManager = context.packageManager
    var forceRefresh = true

    private fun isUserApp(ai: ApplicationInfo?): Boolean {
        if (ai == null) return false
        val mask = ApplicationInfo.FLAG_SYSTEM or ApplicationInfo.FLAG_UPDATED_SYSTEM_APP
        return ai.flags and mask == 0
    }

    fun doesAppHasOrigin(packageName: String): Boolean {
        return isUserApp(getApplicationInfo(packageName))
    }

    fun getPackages(): List<PackageInfo> =
        packageManager.getInstalledPackages(PackageManager.GET_META_DATA)

    suspend fun removePackageName(packageName: String) = withContext(Dispatchers.IO) {
        appsRepository.deleteApp(packageName)
    }

    suspend fun insertNewVersion(packageInfo: PackageInfo) {
        if (packageInfo.applicationInfo == null) return

        val versionCode = packageInfo.longVersionCode

        val currentTargetSDK = packageInfo.applicationInfo!!.targetSdkVersion
        val lastVersion = appsRepository.getLastVersion(packageInfo.packageName)?.targetSdk

        if (lastVersion != currentTargetSDK) {
            val version = Version(
                version = versionCode,
                packageName = packageInfo.packageName,
                versionName = packageInfo.versionName ?: "",
                lastUpdateTime = packageInfo.lastUpdateTime,
                targetSdk = currentTargetSDK
            )

            appsRepository.insertVersion(version)

            if (lastVersion != null) {
                showTargetSDKChangeNotification(packageInfo, lastVersion, currentTargetSDK)
            }
        }
    }

    private fun showTargetSDKChangeNotification(
        packageInfo: PackageInfo,
        oldTargetSDK: Int,
        newTargetSDK: Int
    ) {
        val appName = getAppLabel(packageInfo)
        notificationManager.showSdkChangeNotification(appName, oldTargetSDK, newTargetSDK)
    }

    fun getAppLabel(packageInfo: PackageInfo) =
        packageManager.getApplicationLabel(packageInfo.applicationInfo!!).toString().trim()

    suspend fun insertNewApp(packageInfo: PackageInfo) {
        if (appsRepository.getAppsMap()[packageInfo.packageName] != null) return
        if (packageInfo.applicationInfo == null) return

        val icon = packageManager.getApplicationIcon(packageInfo.applicationInfo!!).toBitmap()
        val backgroundColor = getPaletteColor(Palette.from(icon).generate())
        val label = getAppLabel(packageInfo)

        appsRepository.insertApp(
            App(
                packageName = packageInfo.packageName,
                title = label,
                backgroundColor = backgroundColor,
                isFromPlayStore = isUserApp(packageInfo.applicationInfo)
            )
        )
    }

    private fun getPaletteColor(palette: Palette?, defaultColor: Int = 0) = when {
        palette?.darkVibrantSwatch != null -> palette.getDarkVibrantColor(defaultColor)
        palette?.vibrantSwatch != null -> palette.getVibrantColor(defaultColor)
        palette?.mutedSwatch != null -> palette.getMutedColor(defaultColor)
        palette?.darkMutedSwatch != null -> palette.getDarkMutedColor(defaultColor)
        palette?.lightMutedSwatch != null -> palette.getMutedColor(defaultColor).darken
        palette?.lightVibrantSwatch != null -> palette.getLightVibrantColor(defaultColor).darken
        else -> defaultColor
    }

    suspend fun getPackagesWithUserPrefs(): List<PackageInfo> {
        val preferences = preferencesRepository.getUserPreferences().first()
        return if (preferences.appFilter === AppFilter.USER_APPS) {
            getPackages()
        } else {
            getPackagesWithOrigin()
        }
    }

    private fun getPackagesWithOrigin(): List<PackageInfo> {
        return packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
            .filter { isUserApp(it.applicationInfo) }
    }

    fun getPackageInfo(packageName: String): PackageInfo? {
        return try {
            packageManager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    fun getApplicationInfo(packageName: String): ApplicationInfo? {
        return getPackageInfo(packageName)?.applicationInfo
    }

    fun getIconFromId(packageName: String): Drawable? {
        return try {
            packageManager.getApplicationIcon(getApplicationInfo(packageName)!!)
        } catch (e: Exception) {
            null
        }
    }

    fun getAppTitle(packageName: String): String {
        return try {
            val appInfo = getApplicationInfo(packageName) ?: return packageName
            packageManager.getApplicationLabel(appInfo).toString().trim()
        } catch (e: Exception) {
            packageName
        }
    }

    fun getAllInstalledApps(): List<App> {
        return getPackages().mapNotNull { packageInfo ->
            try {
                if (packageInfo.applicationInfo == null) return@mapNotNull null

                val icon =
                    packageManager.getApplicationIcon(packageInfo.applicationInfo!!).toBitmap()
                val backgroundColor = getPaletteColor(Palette.from(icon).generate())
                val label = getAppLabel(packageInfo)

                App(
                    packageName = packageInfo.packageName,
                    title = label,
                    backgroundColor = backgroundColor,
                    isFromPlayStore = isUserApp(packageInfo.applicationInfo)
                )
            } catch (e: Exception) {
                null
            }
        }
    }

    fun getCurrentVersion(packageName: String): Version? {
        return try {
            val packageInfo = getPackageInfo(packageName) ?: return null

            val versionCode = packageInfo.longVersionCode

            Version(
                version = versionCode,
                packageName = packageInfo.packageName,
                versionName = packageInfo.versionName ?: "",
                lastUpdateTime = packageInfo.lastUpdateTime,
                targetSdk = packageInfo.applicationInfo?.targetSdkVersion ?: 0
            )
        } catch (e: Exception) {
            null
        }
    }

    fun getAppDetails(packageName: String): AppDetails {
        val packageInfo = getPackageInfo(packageName)
        val appInfo = packageInfo?.applicationInfo

        return AppDetails(
            packageName = packageName,
            title = getAppTitle(packageName),
            versionName = packageInfo?.versionName ?: "Unknown",
            versionCode = packageInfo?.longVersionCode ?: 0,
            targetSdk = appInfo?.targetSdkVersion ?: 0,
            minSdk = appInfo?.minSdkVersion ?: 0,
            size = try {
                context.packageManager.getApplicationInfo(
                    packageName,
                    0
                ).sourceDir?.let { sourceDir ->
                    java.io.File(sourceDir).length()
                } ?: 0
            } catch (e: Exception) {
                0
            },
            lastUpdateTime = packageInfo?.lastUpdateTime?.convertTimestampToDate() ?: "Unknown",
            isSystemApp = !isUserApp(appInfo)
        )
    }

    /**
     * Get app icon as bitmap for display in Compose
     */
    suspend fun getAppIcon(packageName: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val drawable = packageManager.getApplicationIcon(packageName)
            drawable.toBitmap()
        } catch (e: Exception) {
            null
        }
    }
}
