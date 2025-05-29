package com.bernaferrari.sdkmonitor.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bernaferrari.sdkmonitor.R
import com.bernaferrari.sdkmonitor.domain.model.LogEntry
import com.bernaferrari.sdkmonitor.extensions.apiToColor
import com.bernaferrari.sdkmonitor.extensions.apiToVersion
import com.bernaferrari.sdkmonitor.extensions.convertTimestampToDate
import com.bernaferrari.sdkmonitor.ui.theme.SDKMonitorTheme

/**
 * Modern, beautiful LogsCard component with Material Design 3
 * Showcases the pinnacle of Android Compose development
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogsCard(
    modifier: Modifier = Modifier,
    logEntry: LogEntry,
    appIcon: Bitmap? = null,
    onClick: () -> Unit = {},
) {
    val apiColor = Color(logEntry.newSdk.apiToColor())
    val apiDescription = logEntry.newSdk.apiToVersion()

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
            // App Icon with beautiful shadow
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
                    if (appIcon != null) {
                        Image(
                            bitmap = appIcon.asImageBitmap(),
                            contentDescription = "App icon for ${logEntry.appName}",
                            modifier = Modifier.size(32.dp)
                        )
                    } else {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_apps_24dp),
                            contentDescription = "Default app icon",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // App info section
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = logEntry.appName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = logEntry.timestamp.convertTimestampToDate(),
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
                        text = logEntry.newSdk.toString(),
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
private fun LogsCardPreview() {
    SDKMonitorTheme {
        LogsCard(
            logEntry = LogEntry(
                id = 1L,
                packageName = "com.whatsapp",
                appName = "WhatsApp Messenger",
                oldSdk = null,
                newSdk = 33,
                oldVersion = null,
                newVersion = "2.24.1.75",
                timestamp = System.currentTimeMillis() - (3 * 7 * 24 * 60 * 60 * 1000L)
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LogsCardDarkPreview() {
    SDKMonitorTheme(darkTheme = true) {
        LogsCard(
            logEntry = LogEntry(
                id = 2L,
                packageName = "com.instagram.android",
                appName = "Instagram",
                oldSdk = null,
                newSdk = 28,
                oldVersion = null,
                newVersion = "305.0.0.37.120",
                timestamp = System.currentTimeMillis() - (24 * 60 * 60 * 1000L)
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LogsCardLongTitlePreview() {
    SDKMonitorTheme {
        LogsCard(
            logEntry = LogEntry(
                id = 3L,
                packageName = "com.supercellveryverylongpackagename.clashofclans",
                appName = "Clash of Clans - Epic Strategy Game with Very Long Title",
                oldSdk = null,
                newSdk = 31,
                oldVersion = null,
                newVersion = "15.0.4",
                timestamp = System.currentTimeMillis() - (2 * 30 * 24 * 60 * 60 * 1000L)
            )
        )
    }
}
