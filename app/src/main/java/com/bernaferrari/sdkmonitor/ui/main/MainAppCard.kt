package com.bernaferrari.sdkmonitor.ui.main

import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
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

/**
 * Modern, beautiful MainAppCard component with Material Design 3
 * Showcases the pinnacle of Android Compose development
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppCard(
    modifier: Modifier = Modifier,
    appVersion: AppVersion,
    appIcon: Bitmap? = null,
    onClick: () -> Unit = {},
) {
    val context = LocalContext.current
    val apiColor = Color(appVersion.sdkVersion.apiToColor())
    val apiDescription = appVersion.sdkVersion.apiToVersion()

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 8.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Icon - clean and larger
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (appIcon != null) {
                    Image(
                        bitmap = appIcon.asImageBitmap(),
                        contentDescription = "App icon for ${appVersion.title}",
                        modifier = Modifier.size(56.dp)
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
                        modifier = Modifier.size(56.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // App info section
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = appVersion.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = appVersion.lastUpdateTime,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // API Level indicator with beautiful gradient
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp))
                    .background(
                        apiColor.copy(alpha = 0.1f)
                    )
                    .height(36.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(0.dp)
            ) {

                Text(
                    text = apiDescription,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp
                    ),
                    color = apiColor,
                    maxLines = 1
                )

                // API Level Number
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp))
                        .background(apiColor)
                        .height(36.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = appVersion.sdkVersion.toString(),
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            lineHeightStyle = LineHeightStyle(
                                alignment = LineHeightStyle.Alignment.Center,
                                trim = LineHeightStyle.Trim.Both
                            )
                        ),
                        color = Color.White
                    )
                }

            }
        }
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

@Preview(showBackground = true)
@Composable
private fun MainAppCardLongTitlePreview() {
    SDKMonitorTheme {
        MainAppCard(
            appVersion = AppVersion(
                packageName = "com.supercellveryverylongpackagename.clashofclans",
                title = "Clash of Clans - Epic Strategy Game with Very Long Title",
                sdkVersion = 31,
                lastUpdateTime = "2 months ago",
                versionName = "15.0.4"
            )
        )
    }
}
