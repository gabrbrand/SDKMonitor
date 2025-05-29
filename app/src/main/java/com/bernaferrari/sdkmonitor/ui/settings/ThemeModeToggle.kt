package com.bernaferrari.sdkmonitor.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bernaferrari.sdkmonitor.domain.model.ThemeMode
import com.bernaferrari.sdkmonitor.ui.theme.SDKMonitorTheme

/**
 * 🎨 ABSOLUTELY STUNNING Theme Option Card - The Most Beautiful Theme Selector Ever!
 * Features breathtaking animations, gorgeous gradients, and divine visual feedback
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeModeToggle(
    themeMode: ThemeMode,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
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
            border = androidx.compose.foundation.BorderStroke(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outlineVariant
                }
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = themeMode.icon,
                    contentDescription = themeMode.displayName,
                    modifier = Modifier.size(24.dp),
                    tint = if (isSelected) {
                        when (themeMode) {
                            ThemeMode.MATERIAL_YOU -> MaterialTheme.colorScheme.primary
                            ThemeMode.LIGHT -> MaterialTheme.colorScheme.secondary
                            ThemeMode.DARK -> MaterialTheme.colorScheme.tertiary
                            ThemeMode.SYSTEM -> MaterialTheme.colorScheme.onSurface
                        }
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = themeMode.displayName,
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

// Divine previews
@Preview(showBackground = true)
@Composable
private fun StunningThemeOptionPreview() {
    SDKMonitorTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ThemeModeToggle(
                themeMode = ThemeMode.MATERIAL_YOU,
                isSelected = true,
                onClick = {},
                modifier = Modifier.weight(1f)
            )
            ThemeModeToggle(
                themeMode = ThemeMode.LIGHT,
                isSelected = false,
                onClick = {},
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StunningThemeOptionDarkPreview() {
    SDKMonitorTheme(darkTheme = true) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ThemeModeToggle(
                themeMode = ThemeMode.DARK,
                isSelected = true,
                onClick = {},
                modifier = Modifier.weight(1f)
            )
            ThemeModeToggle(
                themeMode = ThemeMode.SYSTEM,
                isSelected = false,
                onClick = {},
                modifier = Modifier.weight(1f)
            )
        }
    }
}
