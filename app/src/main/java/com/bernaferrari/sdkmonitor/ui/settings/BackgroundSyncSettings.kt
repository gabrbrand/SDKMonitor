package com.bernaferrari.sdkmonitor.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PowerOff
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
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
import com.bernaferrari.sdkmonitor.ui.theme.SDKMonitorTheme

enum class BackgroundSyncOption(
    val displayName: String,
    val shortName: String,
    val description: String,
    val icon: ImageVector,
    val intervalValue: String,
    val timeUnit: TimeUnit,
    val isEnabled: Boolean
) {
    DISABLED("Disabled", "Off", "Manual only", Icons.Default.PowerOff, "", TimeUnit.HOURS, false),
    DAILY("Daily", "Daily", "Every day", Icons.Default.CalendarToday, "1", TimeUnit.DAYS, true),
    WEEKLY("Weekly", "Weekly", "Every week", Icons.Default.DateRange, "7", TimeUnit.DAYS, true),
    MONTHLY("Monthly", "Monthly", "Every month", Icons.Default.CalendarMonth, "30", TimeUnit.DAYS, true),
    CUSTOM("Custom", "Custom", "Set interval", Icons.Default.Tune, "", TimeUnit.HOURS, true)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackgroundSyncSettings(
    isEnabled: Boolean,
    currentInterval: String,
    currentUnit: TimeUnit,
    onSyncSettingsChange: (enabled: Boolean, interval: String, unit: TimeUnit) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedOption by remember { 
        mutableStateOf(
            when {
                !isEnabled -> BackgroundSyncOption.DISABLED
                currentInterval == "1" && currentUnit == TimeUnit.DAYS -> BackgroundSyncOption.DAILY
                currentInterval == "7" && currentUnit == TimeUnit.DAYS -> BackgroundSyncOption.WEEKLY
                currentInterval == "30" && currentUnit == TimeUnit.DAYS -> BackgroundSyncOption.MONTHLY
                else -> BackgroundSyncOption.CUSTOM
            }
        )
    }
    var customInterval by remember { mutableStateOf(if (selectedOption == BackgroundSyncOption.CUSTOM) currentInterval else "1") }
    var customUnit by remember { mutableStateOf(if (selectedOption == BackgroundSyncOption.CUSTOM) currentUnit else TimeUnit.HOURS) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Grid layout exactly like ThemeModeToggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BackgroundSyncOption.entries.forEach { option ->
                BackgroundSyncToggle(
                    option = option,
                    isSelected = selectedOption == option,
                    onClick = { 
                        selectedOption = option
                        when (option) {
                            BackgroundSyncOption.DISABLED -> onSyncSettingsChange(false, "0", TimeUnit.HOURS)
                            BackgroundSyncOption.CUSTOM -> {} // Wait for custom input
                            else -> onSyncSettingsChange(true, option.intervalValue, option.timeUnit)
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Custom interval input (animated)
        AnimatedVisibility(
            visible = selectedOption == BackgroundSyncOption.CUSTOM,
            enter = expandVertically(animationSpec = spring()) + fadeIn(),
            exit = shrinkVertically(animationSpec = spring()) + fadeOut()
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Custom Interval",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        OutlinedTextField(
                            value = customInterval,
                            onValueChange = { value ->
                                if (value.all { it.isDigit() } && value.length <= 2) {
                                    customInterval = value
                                    val validInterval = value.toIntOrNull()
                                    if (validInterval != null && validInterval > 0) {
                                        onSyncSettingsChange(true, value, customUnit)
                                    }
                                }
                            },
                            label = { Text("Every") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            supportingText = {
                                if (customInterval.isEmpty() || customInterval.toIntOrNull() == null) {
                                    Text("Required", color = MaterialTheme.colorScheme.error)
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
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
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
                                            val validInterval = customInterval.toIntOrNull()
                                            if (validInterval != null && validInterval > 0) {
                                                onSyncSettingsChange(true, customInterval, unit)
                                            }
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

@Composable
private fun BackgroundSyncToggle(
    option: BackgroundSyncOption,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Inspired by ThemeModeToggle design
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedCard(
            onClick = onClick,
            modifier = Modifier.height(64.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.outlinedCardColors(
                containerColor = when {
                    isSelected && option == BackgroundSyncOption.DISABLED -> MaterialTheme.colorScheme.errorContainer
                    isSelected -> MaterialTheme.colorScheme.primaryContainer
                    else -> MaterialTheme.colorScheme.surface
                }
            ),
            border = BorderStroke(
                width = if (isSelected) 2.dp else 1.dp,
                color = when {
                    isSelected && option == BackgroundSyncOption.DISABLED -> MaterialTheme.colorScheme.error
                    isSelected -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.outlineVariant
                }
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = option.icon,
                    contentDescription = option.displayName,
                    modifier = Modifier.size(24.dp),
                    tint = when {
                        isSelected && option == BackgroundSyncOption.DISABLED -> MaterialTheme.colorScheme.error
                        isSelected -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = option.shortName,
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BackgroundSyncSettingsPreview() {
    SDKMonitorTheme {
        BackgroundSyncSettings(
            isEnabled = true,
            currentInterval = "7",
            currentUnit = TimeUnit.DAYS,
            onSyncSettingsChange = { _, _, _ -> },
            modifier = Modifier.padding(16.dp)
        )
    }
}
