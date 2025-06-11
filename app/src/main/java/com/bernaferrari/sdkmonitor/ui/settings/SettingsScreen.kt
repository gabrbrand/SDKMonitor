package com.bernaferrari.sdkmonitor.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.SyncDisabled
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bernaferrari.sdkmonitor.BuildConfig
import com.bernaferrari.sdkmonitor.R
import com.bernaferrari.sdkmonitor.domain.model.ThemeMode

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsScreen(
    onNavigateToAppDetails: (String) -> Unit,
    onNavigateToAbout: (() -> Unit)? = null, // New parameter for about navigation
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSyncDialog by remember { mutableStateOf(false) }
    var selectedSdkVersion by remember { mutableIntStateOf(0) }
    var showSdkDialog by remember { mutableStateOf(false) }

    val singularTimeArray = stringArrayResource(R.array.singularTime)
    val pluralTimeArray = stringArrayResource(R.array.pluralTime)

    // Helper function to get the correct time unit display name
    fun getTimeUnitDisplayName(unit: TimeUnit, value: String): String {
        val intValue = value.toIntOrNull() ?: 1
        return if (intValue == 1) {
            singularTimeArray[unit.code]
        } else {
            pluralTimeArray[unit.code]
        }
    }

    val surface = MaterialTheme.colorScheme.surface

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings_screen_title),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = stringResource(R.string.loading_settings),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            uiState.hasError -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.padding(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.error_loading_settings),
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = uiState.errorMessage
                                    ?: stringResource(R.string.unknown_error),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            FilledTonalButton(onClick = { viewModel.clearError() }) {
                                Text(stringResource(R.string.retry))
                            }
                        }
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues) // Apply padding from this Scaffold
                        .verticalScroll(rememberScrollState())
                ) {
                    val prefs = uiState.preferences

                    SettingsSection(title = stringResource(R.string.appearance)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ThemeMode.entries.forEach { theme ->
                                ThemeModeToggle(
                                    themeMode = theme,
                                    isSelected = prefs.themeMode == theme,
                                    onClick = { viewModel.updateThemeMode(theme) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                    }

                    if (uiState.sdkDistribution.isNotEmpty()) {
                        AnalyticsSection(
                            title = stringResource(R.string.analytics),
                            currentFilter = prefs.appFilter,
                            onFilterChange = { filter -> viewModel.updateAppFilter(filter) }
                        ) {
                            SdkAnalyticsCard(
                                sdkDistribution = uiState.sdkDistribution,
                                totalApps = uiState.totalApps,
                                onSdkClick = { sdkVersion ->
                                    selectedSdkVersion = sdkVersion
                                    showSdkDialog = true
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Background Sync Section - SIMPLIFIED AND ELEGANT
                    SettingsSection(title = stringResource(R.string.background_sync)) {
                        SettingsItem(
                            title = stringResource(R.string.background_sync),
                            subtitle = if (prefs.backgroundSync) {
                                when {
                                    prefs.syncInterval == "1" && prefs.syncTimeUnit == TimeUnit.DAYS -> stringResource(
                                        R.string.enabled_daily_updates
                                    )

                                    prefs.syncInterval == "7" && prefs.syncTimeUnit == TimeUnit.DAYS -> stringResource(
                                        R.string.enabled_weekly_updates
                                    )

                                    prefs.syncInterval == "30" && prefs.syncTimeUnit == TimeUnit.DAYS -> stringResource(
                                        R.string.enabled_monthly_updates
                                    )

                                    else -> stringResource(
                                        R.string.enabled_every,
                                        prefs.syncInterval,
                                        getTimeUnitDisplayName(
                                            prefs.syncTimeUnit,
                                            prefs.syncInterval
                                        ).lowercase()
                                    )
                                }
                            } else {
                                stringResource(R.string.tap_to_configure_automatic_updates)
                            },
                            icon = if (prefs.backgroundSync) Icons.Default.Sync else Icons.Default.SyncDisabled,
                            onClick = { showSyncDialog = true }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // About Section
                    SettingsSection(title = stringResource(R.string.about_section)) {
                        SettingsItem(
                            title = stringResource(
                                R.string.app_version_format,
                                stringResource(R.string.app_name),
                                BuildConfig.VERSION_NAME
                            ),
                            subtitle = stringResource(R.string.learn_more_about_app),
                            icon = Icons.Default.Info,
                            onClick = {
                                onNavigateToAbout?.invoke()
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        // Background Sync Dialog - NOW HANDLES EVERYTHING
        if (showSyncDialog) {
            BackgroundSyncDialog(
                isEnabled = uiState.preferences.backgroundSync,
                currentInterval = uiState.preferences.syncInterval,
                currentUnit = uiState.preferences.syncTimeUnit,
                onDismiss = { showSyncDialog = false },
                onSave = { enabled, interval, unit ->
                    if (enabled != uiState.preferences.backgroundSync) {
                        viewModel.toggleBackgroundSync()
                    }
                    if (enabled) {
                        viewModel.setSyncInterval(interval, unit)
                    }
                }
            )
        }

        // SDK Detail Dialog - Enhanced for better navigation
        if (showSdkDialog) {
            val appsWithSdk = uiState.allAppsForSdk.filter { it.sdkVersion == selectedSdkVersion }
            SdkDetailDialog(
                sdkVersion = selectedSdkVersion,
                apps = appsWithSdk,
                onDismiss = { showSdkDialog = false },
                onAppClick = { packageName ->
                    showSdkDialog = false
                    onNavigateToAppDetails(packageName)
                }
            )
        }
    }
}
