package com.bernaferrari.sdkmonitor.ui.details

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bernaferrari.sdkmonitor.domain.model.AppDetails
import com.bernaferrari.sdkmonitor.extensions.apiToColor
import com.bernaferrari.sdkmonitor.extensions.apiToVersion
import com.bernaferrari.sdkmonitor.ui.theme.SDKMonitorTheme

@Composable
fun AppDetailsCard(
    appDetails: AppDetails,
    modifier: Modifier = Modifier,
    onAppInfoClick: () -> Unit = {},
    onPlayStoreClick: () -> Unit = {}
) {
    val sdkColor = Color(appDetails.targetSdk.apiToColor())
    val sdkDescription = appDetails.targetSdk.apiToVersion()

    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 16.dp,
            hoveredElevation = 24.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            sdkColor.copy(alpha = 0.02f),
                            MaterialTheme.colorScheme.surfaceContainerLow
                        ),
                        radius = 800f
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                AppDetailsHeader(
                    sdkColor = sdkColor,
                    sdkDescription = sdkDescription
                )
                BeautifulSdkSection(
                    appDetails = appDetails,
                    sdkColor = sdkColor
                )
                ActionButtonsSection(
                    onAppInfoClick = onAppInfoClick,
                    onPlayStoreClick = onPlayStoreClick
                )
                AppInformationGrid(appDetails = appDetails)
                AdditionalDetailsSection(appDetails = appDetails)
            }
        }
    }
}

@Composable
private fun AppDetailsHeader(
    sdkColor: Color,
    sdkDescription: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title section
        Column {
            Text(
                text = "App Details",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 28.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun BeautifulSdkSection(
    appDetails: AppDetails,
    sdkColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Target SDK - Primary badge
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            color = sdkColor.copy(alpha = 0.15f),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = appDetails.targetSdk.toString(),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = sdkColor
                )
                Text(
                    text = "Target SDK",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = sdkColor
                )
                Text(
                    text = appDetails.targetSdk.apiToVersion(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }

        // Min SDK - Secondary badge
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = appDetails.minSdk.toString(),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "Min SDK",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = appDetails.minSdk.apiToVersion(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun ActionButtonsSection(
    onAppInfoClick: () -> Unit,
    onPlayStoreClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onAppInfoClick,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "App Info",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }

        OutlinedButton(
            onClick = onPlayStoreClick,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Play Store",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Composable
private fun AppInformationGrid(appDetails: AppDetails) {
    // Single elegant surface instead of nested cards
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Simple section header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "App Info",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = "Application Information",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Clean info rows without excessive nesting
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CleanDetailRow(
                    icon = Icons.Default.Code,
                    title = "Package Name",
                    value = appDetails.packageName,
                    color = MaterialTheme.colorScheme.primary
                )

                CleanDetailRow(
                    icon = Icons.Default.Tag,
                    title = "Version",
                    value = "${appDetails.versionName} (${appDetails.versionCode})",
                    color = MaterialTheme.colorScheme.secondary
                )

                CleanDetailRow(
                    icon = Icons.Default.Update,
                    title = "Last Update",
                    value = appDetails.lastUpdateTime,
                    color = MaterialTheme.colorScheme.tertiary
                )

                CleanDetailRow(
                    icon = Icons.Default.Storage,
                    title = "App Size",
                    value = formatSize(appDetails.size),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun CleanDetailRow(
    icon: ImageVector,
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    // Simple row design without excessive surfaces
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Simple icon container
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(12.dp),
            color = color.copy(alpha = 0.12f)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(20.dp),
                    tint = color
                )
            }
        }

        // Clean text content
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun AdditionalDetailsSection(appDetails: AppDetails) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Simple header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Install Info",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Installation Details",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Simple install information
            CleanDetailRow(
                icon = Icons.Default.Schedule,
                title = "Install Date",
                value = appDetails.lastUpdateTime,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// Beautiful utility functions
private fun formatSize(sizeInBytes: Long): String {
    val kb = 1024
    val mb = kb * 1024
    val gb = mb * 1024

    return when {
        sizeInBytes < kb -> "$sizeInBytes B"
        sizeInBytes < mb -> String.format("%.1f KB", sizeInBytes.toFloat() / kb)
        sizeInBytes < gb -> String.format("%.1f MB", sizeInBytes.toFloat() / mb)
        else -> String.format("%.1f GB", sizeInBytes.toFloat() / gb)
    }
}

// Divine preview
@Preview(showBackground = true)
@Composable
private fun AppDetailsCardPreview() {
    SDKMonitorTheme {
        AppDetailsCard(
            appDetails = AppDetails(
                packageName = "com.bernaferrari.sdkmonitor",
                title = "SDK Monitor",
                targetSdk = 34,
                minSdk = 26,
                versionName = "2.1.0",
                versionCode = 42,
                lastUpdateTime = "2 days ago",
                size = 25 * 1024 * 1024, // 25 MB
            ),
            modifier = Modifier.padding(16.dp),
            onAppInfoClick = { /* Handle app info click */ },
            onPlayStoreClick = { /* Handle play store click */ }
        )
    }
}
