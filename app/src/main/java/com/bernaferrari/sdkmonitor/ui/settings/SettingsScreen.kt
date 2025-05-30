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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.SyncDisabled
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bernaferrari.sdkmonitor.domain.model.AppFilter
import com.bernaferrari.sdkmonitor.domain.model.ThemeMode

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAppDetails: (String) -> Unit, // <-- add this parameter
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSyncDialog by remember { mutableStateOf(false) }
    var selectedSdkVersion by remember { mutableIntStateOf(0) }
    var showSdkDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showAboutDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "About",
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
                            text = "Loading settings...",
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
                                text = "Error loading settings",
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
                                Text("Retry")
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

                    Spacer(modifier = Modifier.height(16.dp))

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

                    // Analytics Section - NEW!
                    if (uiState.sdkDistribution.isNotEmpty()) {
                        SettingsSection(title = "App Analytics") {
                            SdkAnalyticsCard(
                                sdkDistribution = uiState.sdkDistribution,
                                totalApps = uiState.totalApps,
                                onSdkClick = { sdkVersion ->
                                    selectedSdkVersion = sdkVersion
                                    showSdkDialog = true
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }


                    Spacer(modifier = Modifier.height(16.dp))

                    // App Management Section - SIMPLE DROPDOWN!
                    SettingsSection(title = "App Management") {
                        var showFilterMenu by remember { mutableStateOf(false) }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Filter Apps",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            // Filter dropdown button
                            Box {
                                Card(
                                    onClick = { showFilterMenu = true },
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                    elevation = CardDefaults.cardElevation(2.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = when (prefs.appFilter) {
                                                AppFilter.ALL_APPS -> Icons.Default.Apps
                                                AppFilter.USER_APPS -> Icons.Default.Person
                                                AppFilter.SYSTEM_APPS -> Icons.Default.Android
                                            },
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = prefs.appFilter.displayName,
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Medium
                                            ),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Icon(
                                            imageVector = Icons.Default.ExpandMore,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                DropdownMenu(
                                    expanded = showFilterMenu,
                                    onDismissRequest = { showFilterMenu = false },
                                    shape = RoundedCornerShape(12.dp),
                                    containerColor = MaterialTheme.colorScheme.surface
                                ) {
                                    AppFilter.entries.forEach { filter ->
                                        DropdownMenuItem(
                                            text = {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = when (filter) {
                                                            AppFilter.ALL_APPS -> Icons.Default.Apps
                                                            AppFilter.USER_APPS -> Icons.Default.Person
                                                            AppFilter.SYSTEM_APPS -> Icons.Default.Android
                                                        },
                                                        contentDescription = null,
                                                        modifier = Modifier.size(18.dp),
                                                        tint = if (prefs.appFilter == filter) {
                                                            MaterialTheme.colorScheme.primary
                                                        } else {
                                                            MaterialTheme.colorScheme.onSurfaceVariant
                                                        }
                                                    )
                                                    Text(
                                                        text = filter.displayName,
                                                        style = MaterialTheme.typography.bodyMedium.copy(
                                                            fontWeight = if (prefs.appFilter == filter) {
                                                                FontWeight.SemiBold
                                                            } else {
                                                                FontWeight.Normal
                                                            }
                                                        ),
                                                        color = if (prefs.appFilter == filter) {
                                                            MaterialTheme.colorScheme.primary
                                                        } else {
                                                            MaterialTheme.colorScheme.onSurface
                                                        }
                                                    )
                                                }
                                            },
                                            onClick = {
                                                viewModel.updateAppFilter(filter)
                                                showFilterMenu = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Simple description
                        Text(
                            text = when (prefs.appFilter) {
                                AppFilter.ALL_APPS -> "Showing all applications on your device"
                                AppFilter.USER_APPS -> "Showing apps installed from Play Store and other sources"
                                AppFilter.SYSTEM_APPS -> "Showing pre-installed system applications"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                        )
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
}
