package com.bernaferrari.sdkmonitor.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bernaferrari.sdkmonitor.settings.ModernSettingsViewModel
import com.bernaferrari.sdkmonitor.settings.SettingsUiState
import com.bernaferrari.sdkmonitor.settings.SettingType
import com.bernaferrari.sdkmonitor.settings.ModernBackgroundSyncDialog
import com.bernaferrari.sdkmonitor.ui.components.SettingsSection
import com.bernaferrari.sdkmonitor.ui.components.SettingsItem
import com.bernaferrari.sdkmonitor.ui.components.SettingsHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: ModernSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSyncDialog by remember { mutableStateOf(false) }

    // Show error snackbar if needed
    LaunchedEffect(uiState.errorMessage) {
        if (uiState.hasError) {
            // Handle error display here if you have a snackbar host
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
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
                        CircularProgressIndicator()
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
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Error loading settings",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = uiState.errorMessage ?: "Unknown error",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(onClick = { viewModel.clearError() }) {
                            Text("Retry")
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

                    // Beautiful header
                    SettingsHeader(
                        modifier = Modifier.padding(16.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Appearance Section
                    SettingsSection(title = "Appearance") {
                        SettingsItem(
                            title = "Light Mode",
                            subtitle = if (prefs.lightMode) "Using light theme" else "Using dark theme",
                            icon = if (prefs.lightMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            isSwitch = true,
                            switchValue = prefs.lightMode,
                            onSwitchToggle = { viewModel.toggleLightMode() }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // App Filtering Section
                    SettingsSection(title = "App Filtering") {
                        SettingsItem(
                            title = "Show System Apps",
                            subtitle = if (prefs.showSystemApps) "System apps are visible" else "System apps are hidden",
                            icon = Icons.Default.Apps,
                            isSwitch = true,
                            switchValue = prefs.showSystemApps,
                            onSwitchToggle = { viewModel.toggleShowSystemApps() }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        SettingsItem(
                            title = "Order by SDK",
                            subtitle = if (prefs.orderBySdk) "Sorted by target SDK version" else "Sorted alphabetically",
                            icon = Icons.Default.Sort,
                            isSwitch = true,
                            switchValue = prefs.orderBySdk,
                            onSwitchToggle = { viewModel.toggleOrderBySdk() }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Background Sync Section
                    SettingsSection(title = "Background Sync") {
                        SettingsItem(
                            title = "Background Sync",
                            subtitle = if (prefs.backgroundSync) "Automatically checking for updates" else "Manual updates only",
                            icon = Icons.Default.Sync,
                            isSwitch = true,
                            switchValue = prefs.backgroundSync,
                            onSwitchToggle = { viewModel.toggleBackgroundSync() }
                        )

                        if (prefs.backgroundSync) {
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            SettingsItem(
                                title = "Sync Interval",
                                subtitle = "Every ${prefs.syncInterval} ${prefs.syncTimeUnit.displayName.lowercase()}",
                                icon = Icons.Default.Schedule,
                                onClick = { showSyncDialog = true }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }

    // Background Sync Dialog
    if (showSyncDialog) {
        ModernBackgroundSyncDialog(
            isEnabled = uiState.preferences.backgroundSync,
            currentInterval = uiState.preferences.syncInterval,
            currentUnit = uiState.preferences.syncTimeUnit,
            onDismiss = { showSyncDialog = false },
            onSave = { enabled, interval, unit ->
                if (enabled != uiState.preferences.backgroundSync) {
                    viewModel.toggleBackgroundSync()
                }
                viewModel.setSyncInterval(interval, unit)
            }
        )
    }
}
