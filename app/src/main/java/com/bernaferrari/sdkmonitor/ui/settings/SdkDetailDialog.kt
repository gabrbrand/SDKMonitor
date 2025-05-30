package com.bernaferrari.sdkmonitor.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bernaferrari.sdkmonitor.domain.model.AppVersion
import com.bernaferrari.sdkmonitor.extensions.apiToColor
import com.bernaferrari.sdkmonitor.extensions.apiToVersion
import com.bernaferrari.sdkmonitor.ui.main.MainAppCard
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
                            .fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(apps.sortedBy { it.title.lowercase() }) { app ->
                            MainAppCard(
                                modifier = Modifier.padding(horizontal = 0.dp, vertical = 0.dp),
                                appVersion = app,
                                showVersionPill = false,
                                onClick = { onAppClick(app.packageName) }
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
