package com.bernaferrari.sdkmonitor.ui.logs

import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.bernaferrari.sdkmonitor.R
import com.bernaferrari.sdkmonitor.domain.model.LogEntry
import com.bernaferrari.sdkmonitor.extensions.apiToColor
import com.bernaferrari.sdkmonitor.extensions.apiToVersion
import com.bernaferrari.sdkmonitor.ui.theme.SDKMonitorTheme

@Composable
fun LogCard(
    modifier: Modifier = Modifier,
    log: LogEntry,
    onClick: () -> Unit = {},
) {
    val context = LocalContext.current
    val apiColor = Color(log.newSdk.apiToColor())
    val apiDescription = log.newSdk.apiToVersion()
    val hasVersionChange = log.oldVersion != log.newVersion
    val hasSdkChange = log.oldSdk != null && log.oldSdk != log.newSdk

    Card(
        onClick = onClick,
        modifier = modifier.padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 8.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surfaceContainer,
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(remember(log.packageName) {
                                try {
                                    context.packageManager.getApplicationIcon(log.packageName)
                                } catch (e: PackageManager.NameNotFoundException) {
                                    R.drawable.ic_android
                                }
                            })
                            .crossfade(true)
                            .build(),
                        contentDescription = "App icon",
                        modifier = Modifier.size(56.dp)
                    )
                }

                // App information
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = log.appName,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = formatLogTime(log.timestamp, context),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }

                        if (hasVersionChange) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                            ) {
                                Text(
                                    text = "v${log.newVersion}",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.secondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                // SDK display
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // API version badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = apiColor.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = apiDescription,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = apiColor
                        )
                    }

                    // SDK display with compact transition
                    if (hasSdkChange && log.oldSdk != null) {
                        // Compact SDK transition
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = log.oldSdk.toString(),
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )

                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Updated to",
                                    modifier = Modifier.size(12.dp),
                                    tint = apiColor.copy(alpha = 0.8f)
                                )

                                Text(
                                    text = log.newSdk.toString(),
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = apiColor
                                )
                            }
                        }
                    } else {
                        // Current SDK badge (when no change)
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = apiColor,
                            shadowElevation = 6.dp
                        ) {
                            Text(
                                text = log.newSdk.toString(),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold
                                ),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LogCardPreview() {
    SDKMonitorTheme {
        LogCard(
            log = LogEntry(
                id = 1,
                packageName = "com.whatsapp",
                appName = "WhatsApp Messenger",
                oldSdk = 31,
                newSdk = 34,
                oldVersion = "2.24.1.74",
                newVersion = "2.24.1.75",
                timestamp = System.currentTimeMillis() - 3600000 // 1 hour ago
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LogCardDarkPreview() {
    SDKMonitorTheme(darkTheme = true) {
        LogCard(
            log = LogEntry(
                id = 2,
                packageName = "com.instagram.android",
                appName = "Instagram",
                oldSdk = 28,
                newSdk = 33,
                oldVersion = "305.0.0.36.120",
                newVersion = "305.0.0.37.120",
                timestamp = System.currentTimeMillis() - 86400000 // 1 day ago
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LogCardLongVersionPreview() {
    SDKMonitorTheme {
        LogCard(
            log = LogEntry(
                id = 3,
                packageName = "com.supercell.clashofclans",
                appName = "Clash of Clans - Epic Strategy Game",
                oldSdk = null,
                newSdk = 34,
                oldVersion = "15.0.3.build.4.release.candidate.final",
                newVersion = "15.0.4.build.1.release.candidate.final.new",
                timestamp = System.currentTimeMillis() - 172800000 // 2 days ago
            )
        )
    }
}
