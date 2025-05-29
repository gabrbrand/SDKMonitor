package com.bernaferrari.sdkmonitor.ui.logs

import android.content.pm.PackageManager
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.bernaferrari.sdkmonitor.R
import com.bernaferrari.sdkmonitor.ui.theme.SDKMonitorTheme

data class TopUpdatingApp(
    val packageName: String,
    val appName: String,
    val updateCount: Int,
    val lastUpdate: String,
    val trend: String, // "increasing", "stable", "decreasing"
    val oldSdk: Int? = null,
    val newSdk: Int? = null,
    val hasRecentSdkChange: Boolean = false
)

@Composable
fun RecentSdkMigrationsCard(
    apps: List<TopUpdatingApp>,
    modifier: Modifier = Modifier
) {
    val animationProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        ),
        label = "migrations_animation"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = "SDK Migrations",
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "Recent SDK Migrations",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Apps that upgraded target SDK",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (apps.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = "${apps.size} migrations",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }

            // Apps List
            if (apps.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(apps) { index, app ->
                        SdkMigrationAppItem(
                            app = app,
                            animationProgress = animationProgress
                        )
                    }
                }
            } else {
                EmptyMigrationsContent()
            }
        }
    }
}

@Composable
private fun SdkMigrationAppItem(
    modifier: Modifier = Modifier,
    app: TopUpdatingApp,
    animationProgress: Float,
) {
    val context = LocalContext.current
    val migrationColor = if (app.newSdk != null && app.oldSdk != null && app.newSdk > app.oldSdk) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.secondary
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // App Icon
            Card(
                modifier = Modifier.size(36.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(remember(app.packageName) {
                                try {
                                    context.packageManager.getApplicationIcon(app.packageName)
                                } catch (e: PackageManager.NameNotFoundException) {
                                    R.drawable.ic_android
                                }
                            })
                            .crossfade(true)
                            .build(),
                        contentDescription = "App icon",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // App Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = app.appName,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = app.lastUpdate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // SDK Migration Display
            if (app.oldSdk != null && app.newSdk != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = app.oldSdk.toString(),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = "Migrated to",
                        modifier = Modifier.size(12.dp),
                        tint = migrationColor
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = migrationColor.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = app.newSdk.toString(),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = migrationColor
                        )
                    }
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "New app",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyMigrationsContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "No recent SDK migrations",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Keep the old component name as an alias for backward compatibility
@Composable
fun TopUpdatingAppsCard(
    apps: List<TopUpdatingApp>,
    modifier: Modifier = Modifier
) {
    RecentSdkMigrationsCard(apps = apps, modifier = modifier)
}

@Preview(showBackground = true)
@Composable
private fun TopUpdatingAppsCardPreview() {
    SDKMonitorTheme {
        TopUpdatingAppsCard(
            apps = listOf(
                TopUpdatingApp(
                    packageName = "com.whatsapp",
                    appName = "WhatsApp",
                    updateCount = 12,
                    lastUpdate = "2 days ago",
                    trend = "increasing"
                ),
                TopUpdatingApp(
                    packageName = "com.instagram.android",
                    appName = "Instagram",
                    updateCount = 8,
                    lastUpdate = "1 week ago",
                    trend = "stable"
                ),
                TopUpdatingApp(
                    packageName = "com.spotify.music",
                    appName = "Spotify",
                    updateCount = 6,
                    lastUpdate = "3 days ago",
                    trend = "decreasing"
                )
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}
