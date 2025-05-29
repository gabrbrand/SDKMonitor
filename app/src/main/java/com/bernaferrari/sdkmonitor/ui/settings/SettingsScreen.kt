package com.bernaferrari.sdkmonitor.ui.settings

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bernaferrari.sdkmonitor.BuildConfig
import com.bernaferrari.sdkmonitor.domain.model.AppFilter
import com.bernaferrari.sdkmonitor.domain.model.ThemeMode
import com.bernaferrari.sdkmonitor.ui.components.SdkDetailDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: ModernSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSyncDialog by remember { mutableStateOf(false) }
    var selectedSdkVersion by remember { mutableIntStateOf(0) }
    var showSdkDialog by remember { mutableStateOf(false) }

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

                    // Beautiful scrollable header
                    ModernSettingsHeader(
                        modifier = Modifier.padding(16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

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

                    // 🎨 ABSOLUTELY STUNNING Theme Selection Section - NEW!
                    SettingsSection(title = "Appearance") {


                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ThemeMode.entries.forEach { theme ->
                                StunningThemeOption(
                                    themeMode = theme,
                                    isSelected = prefs.themeMode == theme,
                                    onClick = { viewModel.updateThemeMode(theme) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // App Filtering & Behavior Section - ULTRA GORGEOUS ENHANCED DESIGN!
                    SettingsSection(title = "App Management") {
                        // STUNNING App Filter Selection with Enhanced Animations
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp), // More rounded for beauty
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer
                            ),
                            elevation = CardDefaults.cardElevation(8.dp) // Enhanced elevation
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp), // More padding for luxury
                                verticalArrangement = Arrangement.spacedBy(20.dp) // More spacing
                            ) {
                                // Beautiful Header with Icon Animation
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    // Animated Icon Container
                                    val iconScale by animateFloatAsState(
                                        targetValue = 1.1f,
                                        animationSpec = infiniteRepeatable(
                                            animation = tween(2000),
                                            repeatMode = RepeatMode.Reverse
                                        ),
                                        label = "icon_pulse"
                                    )

                                    Surface(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .graphicsLayer(scaleX = iconScale, scaleY = iconScale),
                                        shape = RoundedCornerShape(14.dp),
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                        shadowElevation = 4.dp
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.FilterList,
                                                contentDescription = "App Filter",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }

                                    Column {
                                        Text(
                                            text = "App Filter",
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Choose which apps to display and analyze",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                // Clean Filter Chips without elastic animations
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    AppFilter.entries.forEach { filter ->
                                        val isSelected = prefs.appFilter == filter

                                        FilterChip(
                                            onClick = {
                                                viewModel.updateAppFilter(filter)
                                            },
                                            label = {
                                                Text(
                                                    text = filter.displayName,
                                                    style = MaterialTheme.typography.labelLarge.copy(
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                                                    )
                                                )
                                            },
                                            selected = isSelected,
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = when (filter) {
                                                        AppFilter.ALL_APPS -> Icons.Default.Apps
                                                        AppFilter.USER_APPS -> Icons.Default.Person
                                                        AppFilter.SYSTEM_APPS -> Icons.Default.Android
                                                    },
                                                    contentDescription = filter.displayName,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            },
                                            shape = RoundedCornerShape(16.dp),
                                        )
                                    }
                                }

                                // Beautiful description for current selection with updated order
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    color = when (prefs.appFilter) {
                                        AppFilter.ALL_APPS -> MaterialTheme.colorScheme.tertiaryContainer.copy(
                                            alpha = 0.3f
                                        )

                                        AppFilter.USER_APPS -> MaterialTheme.colorScheme.primaryContainer.copy(
                                            alpha = 0.3f
                                        )

                                        AppFilter.SYSTEM_APPS -> MaterialTheme.colorScheme.secondaryContainer.copy(
                                            alpha = 0.3f
                                        )
                                    }
                                ) {
                                    Text(
                                        text = when (prefs.appFilter) {
                                            AppFilter.ALL_APPS -> "🌟 Showing all applications on your device"
                                            AppFilter.USER_APPS -> "📱 Showing apps installed from Play Store and other sources"
                                            AppFilter.SYSTEM_APPS -> "⚙️ Showing pre-installed system applications"
                                        },
                                        modifier = Modifier.padding(16.dp),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Enhanced Sort Setting
                        SettingsItem(
                            title = "Sort Order",
                            subtitle = if (prefs.orderBySdk) "📊 Sorted by target SDK version (highest first)" else "🔤 Sorted alphabetically by name",
                            icon = Icons.AutoMirrored.Filled.Sort,
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

    // SDK Detail Dialog
    if (showSdkDialog) {
        val appsWithSdk = uiState.allAppsForSdk.filter { it.sdkVersion == selectedSdkVersion }
        SdkDetailDialog(
            sdkVersion = selectedSdkVersion,
            apps = appsWithSdk,
            onDismiss = { showSdkDialog = false },
            onAppClick = { packageName ->
                // Navigate to app details
                showSdkDialog = false
            }
        )
    }
}

@Composable
private fun ModernSettingsHeader(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "v${BuildConfig.VERSION_NAME}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
            ) {
                Text(
                    text = "SDK Monitor",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}
