package com.bernaferrari.sdkmonitor.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bernaferrari.sdkmonitor.ui.theme.SDKMonitorTheme

enum class SyncPreset(
    val displayName: String,
    val shortName: String,
    val description: String,
    val icon: ImageVector,
    val intervalValue: String,
    val timeUnit: TimeUnit,
) {
    DAILY("Daily", "Daily", "Once every day", Icons.Default.CalendarToday, "1", TimeUnit.DAYS),
    WEEKLY("Weekly", "Weekly", "Once every week", Icons.Default.DateRange, "7", TimeUnit.DAYS),
    MONTHLY(
        "Monthly",
        "Monthly",
        "Once every month",
        Icons.Default.CalendarMonth,
        "30",
        TimeUnit.DAYS
    ),
    CUSTOM("Custom", "Custom", "Set your own interval", Icons.Default.Tune, "", TimeUnit.HOURS)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackgroundSyncDialog(
    isEnabled: Boolean = false,
    currentInterval: String = "30",
    currentUnit: TimeUnit = TimeUnit.MINUTES,
    onDismiss: () -> Unit = {},
    onSave: (enabled: Boolean, interval: String, unit: TimeUnit) -> Unit = { _, _, _ -> }
) {
    var enabled by remember { mutableStateOf(isEnabled) }
    var selectedPreset by remember {
        mutableStateOf(
            when {
                currentInterval == "1" && currentUnit == TimeUnit.DAYS -> SyncPreset.DAILY
                currentInterval == "7" && currentUnit == TimeUnit.DAYS -> SyncPreset.WEEKLY
                currentInterval == "30" && currentUnit == TimeUnit.DAYS -> SyncPreset.MONTHLY
                else -> SyncPreset.CUSTOM
            }
        )
    }
    var customInterval by remember { mutableStateOf(if (selectedPreset == SyncPreset.CUSTOM) currentInterval else "1") }
    var customUnit by remember { mutableStateOf(if (selectedPreset == SyncPreset.CUSTOM) currentUnit else TimeUnit.HOURS) }

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
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
            

                // Fully Clickable Enable/Disable Card
                Card(
                    onClick = { enabled = !enabled },
                    colors = CardDefaults.cardColors(
                        containerColor = if (enabled) {
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        }
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Background Sync",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (enabled) "Apps will update automatically" else "Tap to enable automatic updates",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Switch(
                            checked = enabled,
                            onCheckedChange = null // Disable switch click since card handles it
                        )
                    }
                }

                // Sync Frequency Selection (only when enabled)
                AnimatedVisibility(
                    visible = enabled,
                    enter = expandVertically(animationSpec = spring()) + fadeIn(),
                    exit = shrinkVertically(animationSpec = spring()) + fadeOut()
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Sync Frequency",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        // Beautiful Grid Layout with External Descriptions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                SyncPreset.DAILY,
                                SyncPreset.WEEKLY,
                                SyncPreset.MONTHLY,
                                SyncPreset.CUSTOM
                            ).forEach { preset ->
                                ElegantSyncToggleWithDescription(
                                    preset = preset,
                                    isSelected = selectedPreset == preset,
                                    onClick = { selectedPreset = preset },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // Custom Interval Input
                        AnimatedVisibility(
                            visible = selectedPreset == SyncPreset.CUSTOM,
                            enter = expandVertically(animationSpec = spring()) + fadeIn(),
                            exit = shrinkVertically(animationSpec = spring()) + fadeOut()
                        ) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(
                                        alpha = 0.3f
                                    )
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Tune,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "Set Custom Interval",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            color = MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                    }


                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        OutlinedTextField(
                                            value = customInterval,
                                            onValueChange = { value ->
                                                if (value.all { it.isDigit() } && value.length <= 2) {
                                                    customInterval = value
                                                }
                                            },
                                            label = { Text("Every") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier.weight(1f),
                                            singleLine = true,
                                            supportingText = {
                                                if (customInterval.isEmpty() || customInterval.toIntOrNull() == null) {
                                                    Text(
                                                        "Required",
                                                        color = MaterialTheme.colorScheme.error
                                                    )
                                                }
                                            },
                                            isError = customInterval.isEmpty() || customInterval.toIntOrNull() == null
                                        )

                                        var expanded by remember { mutableStateOf(false) }

                                        ExposedDropdownMenuBox(
                                            expanded = expanded,
                                            onExpandedChange = { expanded = it },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            OutlinedTextField(
                                                value = customUnit.displayName,
                                                onValueChange = {},
                                                readOnly = true,
                                                label = { Text("Unit") },
                                                trailingIcon = {
                                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                                        expanded = expanded
                                                    )
                                                },
                                                modifier = Modifier.menuAnchor()
                                            )

                                            ExposedDropdownMenu(
                                                expanded = expanded,
                                                onDismissRequest = { expanded = false }
                                            ) {
                                                TimeUnit.entries.forEach { unit ->
                                                    DropdownMenuItem(
                                                        text = { Text(unit.displayName) },
                                                        onClick = {
                                                            customUnit = unit
                                                            expanded = false
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Action Buttons
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

                    FilledTonalButton(
                        onClick = {
                            val (interval, unit) = if (enabled) {
                                when (selectedPreset) {
                                    SyncPreset.CUSTOM -> {
                                        val validInterval = customInterval.toIntOrNull()
                                        if (validInterval != null && validInterval > 0) {
                                            Pair(customInterval, customUnit)
                                        } else {
                                            Pair("1", TimeUnit.HOURS)
                                        }
                                    }

                                    else -> Pair(
                                        selectedPreset.intervalValue,
                                        selectedPreset.timeUnit
                                    )
                                }
                            } else {
                                Pair("0", TimeUnit.HOURS)
                            }
                            onSave(enabled, interval, unit)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        enabled = if (!enabled) {
                            true
                        } else if (selectedPreset == SyncPreset.CUSTOM) {
                            customInterval.isNotEmpty() && customInterval.toIntOrNull() != null
                        } else {
                            true
                        }
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Composable
private fun ElegantSyncToggleWithDescription(
    preset: SyncPreset,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedCard(
            onClick = onClick,
            modifier = Modifier.height(64.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.outlinedCardColors(
                containerColor = if (isSelected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surface
                }
            ),
            border = BorderStroke(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.inversePrimary
                } else {
                    MaterialTheme.colorScheme.outlineVariant
                }
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = preset.icon,
                    contentDescription = preset.displayName,
                    modifier = Modifier.size(20.dp),
                    tint = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }

        // Description outside the card
        Text(
            text = preset.shortName,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BackgroundSyncDialogPreview() {
    SDKMonitorTheme {
        BackgroundSyncDialog(
            isEnabled = true,
            currentInterval = "7",
            currentUnit = TimeUnit.DAYS
        )
    }
}
