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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.SyncDisabled
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.res.stringResource
import com.bernaferrari.sdkmonitor.R
import com.bernaferrari.sdkmonitor.domain.model.ThemeMode

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsScreen(
    onNavigateToAppDetails: (String) -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSyncDialog by remember { mutableStateOf(false) }
    var selectedSdkVersion by remember { mutableIntStateOf(0) }
    var showSdkDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showClearLogsDialog by remember { mutableStateOf(false) }

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
                actions = {
                    IconButton(onClick = { showAboutDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = stringResource(R.string.about),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
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
                    modifier = Modifier.fillMaxSize(),
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
                                text = uiState.errorMessage ?: "Unknown error",
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
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                ) {
                    val prefs = uiState.preferences

                    SettingsSection(title = "Appearance") {
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
                    SettingsSection(title = "Background Sync") {
                        SettingsItem(
                            title = "Background Sync",
                            subtitle = if (prefs.backgroundSync) {
                                when {
                                    prefs.syncInterval == "1" && prefs.syncTimeUnit == TimeUnit.DAYS -> "Enabled • Daily updates"
                                    prefs.syncInterval == "7" && prefs.syncTimeUnit == TimeUnit.DAYS -> "Enabled • Weekly updates"
                                    prefs.syncInterval == "30" && prefs.syncTimeUnit == TimeUnit.DAYS -> "Enabled • Monthly updates"
                                    else -> "Enabled • Every ${prefs.syncInterval} ${prefs.syncTimeUnit.displayName.lowercase()}"
                                }
                            } else {
                                "Tap to configure automatic updates"
                            },
                            icon = if (prefs.backgroundSync) Icons.Default.Sync else Icons.Default.SyncDisabled,
                            onClick = { showSyncDialog = true }
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Data Management Section - NEW
                    SettingsSection(title = "Data Management") {
                        SettingsItem(
                            title = stringResource(R.string.clear_all),
                            subtitle = "Delete all app change history and logs",
                            icon = Icons.Default.DeleteSweep,
                            onClick = { showClearLogsDialog = true }
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
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

    // SDK Detail Dialog
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

    // About Dialog
    if (showAboutDialog) {
        AboutDialog(
            onDismiss = { showAboutDialog = false }
        )
    }

    // Clear Logs Dialog - NEW
    if (showClearLogsDialog) {
        AlertDialog(
            onDismissRequest = { showClearLogsDialog = false },
            title = {                    Text(
                        stringResource(R.string.clear_all),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
            },
            text = {
                Text(
                    "Are you sure you want to clear all change logs and app data? This action cannot be undone and will permanently remove your app update history.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        lineHeight = 22.sp
                    )
                )
            },
            confirmButton = {
                FilledTonalButton(
                    onClick = {
                        viewModel.clearAllLogs()
                        showClearLogsDialog = false
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.clear_all), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearLogsDialog = false }) {
                    Text(stringResource(R.string.cancel), fontWeight = FontWeight.Medium)
                }
            },
            shape = RoundedCornerShape(28.dp),
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    }
}
