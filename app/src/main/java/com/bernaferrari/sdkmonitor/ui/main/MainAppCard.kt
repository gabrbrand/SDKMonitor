package com.bernaferrari.sdkmonitor.ui.main

import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush.Companion.verticalGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.bernaferrari.sdkmonitor.domain.model.AppVersion
import com.bernaferrari.sdkmonitor.extensions.apiToColor
import com.bernaferrari.sdkmonitor.extensions.apiToVersion
import com.bernaferrari.sdkmonitor.ui.theme.SDKMonitorTheme

@Composable
fun MainAppCard(
    modifier: Modifier = Modifier,
    appVersion: AppVersion,
    appIcon: Bitmap? = null,
    showVersionPill: Boolean = true,
    isLast: Boolean = false, // Add parameter to detect last item
    onClick: () -> Unit = {},
) {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current
    val apiColor = Color(appVersion.sdkVersion.apiToColor())
    val apiDescription = appVersion.sdkVersion.apiToVersion()

    Column {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clickable { onClick() } // Move clickable to outer area for larger tap target
                .padding(
                    horizontal = 16.dp,
                    vertical = 16.dp
                ), // Increased vertical padding for larger tap area
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App Icon - clean and modern
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (appIcon != null) {
                    Image(
                        bitmap = appIcon.asImageBitmap(),
                        contentDescription = "App icon for ${appVersion.title}",
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                } else if (isPreview) {
                    Icon(
                        imageVector = Icons.Outlined.Apps,
                        contentDescription = "App icon for ${appVersion.title}",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                } else {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(remember(appVersion.packageName) {
                                try {
                                    context.packageManager.getApplicationIcon(appVersion.packageName)
                                } catch (e: PackageManager.NameNotFoundException) {
                                    Icons.Outlined.Apps
                                }
                            })
                            .crossfade(true)
                            .build(),
                        contentDescription = "App icon for ${appVersion.title}",
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp)),
                    )
                }
            }

            // Content section - takes up remaining space
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // App title
                Text(
                    text = appVersion.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Bottom row with date and API pill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Date - subtle and clean
                    Text(
                        text = appVersion.lastUpdateTime,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                }
            }

            if (showVersionPill) {
                // Refined compact pill with beautiful styling
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            verticalGradient(
                                colors = listOf(
                                    apiColor.copy(alpha = 0.18f),
                                    apiColor.copy(alpha = 0.08f)
                                )
                            )
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    // Compact SDK number with refined typography
                    Text(
                        text = appVersion.sdkVersion.toString(),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.8).dp.value.sp
                        ),
                        color = apiColor
                    )

                    // Compact API description with subtle background
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 1.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = apiDescription,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.2.dp.value.sp
                            ),
                            color = apiColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

        }

        // Subtle divider line - only show if not last item
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 88.dp, end = 16.dp)
                .height(0.5.dp)
                .background(if (!isLast) MaterialTheme.colorScheme.outlineVariant else Color.Transparent)
        )

    }
}

@Preview(showBackground = true)
@Composable
private fun MainAppCardPreview() {
    SDKMonitorTheme {
        MainAppCard(
            appVersion = AppVersion(
                packageName = "com.whatsapp",
                title = "WhatsApp Messenger",
                sdkVersion = 33,
                lastUpdateTime = "3 weeks ago",
                versionName = "2.24.1.75"
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MainAppCardDarkPreview() {
    SDKMonitorTheme(darkTheme = true) {
        MainAppCard(
            appVersion = AppVersion(
                packageName = "com.instagram.android",
                title = "Instagram",
                sdkVersion = 28,
                lastUpdateTime = "1 day ago",
                versionName = "305.0.0.37.120"
            )
        )
    }
}
