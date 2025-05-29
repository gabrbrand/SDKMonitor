package com.bernaferrari.sdkmonitor.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bernaferrari.sdkmonitor.ui.theme.SDKMonitorTheme

/**
 * Modern Background Sync Dialog using pure Compose
 * Beautiful Material Design 3 implementation with stunning gradients
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernBackgroundSyncDialog(
    isEnabled: Boolean = false,
    currentInterval: String = "30",
    currentUnit: TimeUnit = TimeUnit.MINUTES,
    onDismiss: () -> Unit = {},
    onSave: (enabled: Boolean, interval: String, unit: TimeUnit) -> Unit = { _, _, _ -> }
) {
    var enabled by remember { mutableStateOf(isEnabled) }
    var interval by remember { mutableStateOf(currentInterval) }
    var selectedUnit by remember { mutableStateOf(currentUnit) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 24.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Header with close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Sync icon with gradient background
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.Transparent,
                        shadowElevation = 8.dp
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.colorScheme.primaryContainer
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Sync",
                                modifier = Modifier.size(28.dp),
                                tint = Color.White
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Title and description
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Background Sync",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Automatically check for app updates in the background",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }

                // Enable/Disable toggle
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Enable Background Sync",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = if (enabled) "Sync is enabled" else "Sync is disabled",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Switch(
                            checked = enabled,
                            onCheckedChange = { enabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }

                // Interval settings (only shown when enabled)
                if (enabled) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Sync Interval",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            // Interval input
                            OutlinedTextField(
                                value = interval,
                                onValueChange = { value ->
                                    if (value.all { it.isDigit() } && value.isNotEmpty()) {
                                        val num = value.toIntOrNull()
                                        if (num != null && num >= 15) {
                                            interval = value
                                        }
                                    } else if (value.isEmpty()) {
                                        interval = ""
                                    }
                                },
                                label = { Text("Interval") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                supportingText = {
                                    Text("Minimum 15 ${selectedUnit.displayName}")
                                }
                            )

                            // Time unit selection
                            Text(
                                text = "Time Unit",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                TimeUnit.values().forEach { unit ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .selectable(
                                                selected = selectedUnit == unit,
                                                onClick = { selectedUnit = unit }
                                            )
                                            .padding(vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = selectedUnit == unit,
                                            onClick = { selectedUnit = unit }
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Text(
                                            text = unit.displayName,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    ElevatedButton (
                        onClick = {
                            val finalInterval = if (interval.isEmpty()) "15" else interval
                            onSave(enabled, finalInterval, selectedUnit)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ModernBackgroundSyncDialogPreview() {
    SDKMonitorTheme {
        ModernBackgroundSyncDialog(
            isEnabled = true,
            currentInterval = "30",
            currentUnit = TimeUnit.MINUTES
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ModernBackgroundSyncDialogDisabledPreview() {
    SDKMonitorTheme {
        ModernBackgroundSyncDialog(
            isEnabled = false
        )
    }
}
