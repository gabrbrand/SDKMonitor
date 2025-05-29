package com.bernaferrari.sdkmonitor.ui.main

import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.bernaferrari.sdkmonitor.R
import com.bernaferrari.sdkmonitor.domain.model.AppVersion
import com.bernaferrari.sdkmonitor.extensions.apiToColor
import com.bernaferrari.sdkmonitor.extensions.apiToVersion
import com.bernaferrari.sdkmonitor.ui.theme.SDKMonitorTheme

/**
 * Ultra-modern App Card showcasing the pinnacle of Material Design 3
 * Features beautiful gradients, shadows, and smooth animations
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernMainAppCard(
    modifier: Modifier = Modifier,
    appVersion: AppVersion,
    onClick: () -> Unit = {},
) {
    val context = LocalContext.current
    val apiColor = Color(appVersion.sdkVersion.apiToColor())
    val apiDescription = appVersion.sdkVersion.apiToVersion()

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 12.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            apiColor.copy(alpha = 0.03f)
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // App Icon with stunning shadow and background
                Card(
                    modifier = Modifier.size(64.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                    ),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(remember(appVersion.packageName) {
                                    try {
                                        context.packageManager.getApplicationIcon(appVersion.packageName)
                                    } catch (e: PackageManager.NameNotFoundException) {
                                        R.drawable.ic_android
                                    }
                                })
                                .crossfade(true)
                                .build(),
                            contentDescription = "App icon for ${appVersion.title}",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(20.dp))

                // App information section
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = appVersion.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_update),
                            contentDescription = "Last update",
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = appVersion.lastUpdateTime,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // API Level display with stunning design
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // API Description Badge
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp)),
                        color = apiColor.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = apiDescription,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.sp
                            ),
                            color = apiColor,
                            maxLines = 1
                        )
                    }

                    // API Level Number with gradient background
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        apiColor,
                                        apiColor.copy(alpha = 0.8f)
                                    )
                                )
                            ),
                        color = Color.Transparent,
                    ) {
                        Text(

                            text = appVersion.sdkVersion.toString(),
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ModernMainAppCardPreview() {
    SDKMonitorTheme {
        ModernMainAppCard(
            appVersion = AppVersion(
                packageName = "com.whatsapp",
                title = "WhatsApp Messenger",
                sdkVersion = 33,
                lastUpdateTime = "2 days ago"
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ModernMainAppCardDarkPreview() {
    SDKMonitorTheme(darkTheme = true) {
        ModernMainAppCard(
            appVersion = AppVersion(
                packageName = "com.instagram.android",
                title = "Instagram",
                sdkVersion = 28,
                lastUpdateTime = "1 week ago"
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ModernMainAppCardLongTitlePreview() {
    SDKMonitorTheme {
        ModernMainAppCard(
            appVersion = AppVersion(
                packageName = "com.supercell.clashofclans",
                title = "Clash of Clans - Epic Strategy Game with Very Long Title That Should Truncate",
                sdkVersion = 31,
                lastUpdateTime = "3 months ago"
            )
        )
    }
}
