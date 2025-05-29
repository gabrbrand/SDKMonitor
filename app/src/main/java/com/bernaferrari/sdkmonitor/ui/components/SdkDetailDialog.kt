package com.bernaferrari.sdkmonitor.ui.components

import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.bernaferrari.sdkmonitor.R
import com.bernaferrari.sdkmonitor.domain.model.AppVersion
import com.bernaferrari.sdkmonitor.extensions.apiToColor
import com.bernaferrari.sdkmonitor.extensions.apiToVersion
import com.bernaferrari.sdkmonitor.ui.theme.SDKMonitorTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SdkDetailDialog(
    modifier: Modifier = Modifier,
    sdkVersion: Int,
    apps: List<AppVersion>,
    onDismiss: () -> Unit,
    onAppClick: (String) -> Unit = {},
) {
    val apiColor = Color(sdkVersion.apiToColor())
    val apiDescription = sdkVersion.apiToVersion()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 24.dp
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Beautiful Header with gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp) // Increased height to prevent cutoff
                        .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    apiColor,
                                    apiColor.copy(alpha = 0.8f)
                                )
                            )
                        )
                ) {
                    // Close button
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }

                    // Header content with better spacing
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(
                                start = 24.dp,
                                end = 60.dp,
                                bottom = 20.dp,
                                top = 60.dp
                            ), // Added end padding to avoid close button
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Target SDK $sdkVersion",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = apiDescription,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.9f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = "${apps.size} ${if (apps.size == 1) "app" else "apps"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                // Apps List
                if (apps.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No apps found with this SDK version",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        contentPadding = PaddingValues(vertical = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(apps.sortedBy { it.title.lowercase() }) { app ->
                            SdkDialogAppCard(
                                app = app,
                                onClick = { onAppClick(app.packageName) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SdkDialogAppCard(
    app: AppVersion,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // App Icon
            Card(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                ),
                elevation = CardDefaults.cardElevation(4.dp)
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
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // App Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = app.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (app.versionName.isNotEmpty()) {
                    Text(
                        text = "v${app.versionName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = app.lastUpdateTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Play Store indicator
            if (app.isFromPlayStore) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "Play Store",
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

@Preview(showBackground = true)
@Composable
private fun SdkDetailDialogPreview() {
    SDKMonitorTheme {
        SdkDetailDialog(
            sdkVersion = 33,
            apps = listOf(
                AppVersion(
                    packageName = "com.whatsapp",
                    title = "WhatsApp",
                    sdkVersion = 33,
                    lastUpdateTime = "2 days ago",
                    versionName = "2.24.1.75",
                    isFromPlayStore = true
                ),
                AppVersion(
                    packageName = "com.instagram.android",
                    title = "Instagram",
                    sdkVersion = 33,
                    lastUpdateTime = "1 week ago",
                    versionName = "305.0.0.37.120",
                    isFromPlayStore = true
                )
            ),
            onDismiss = {}
        )
    }
}
