package com.bernaferrari.sdkmonitor.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bernaferrari.sdkmonitor.domain.model.AppVersion
import com.bernaferrari.sdkmonitor.ui.theme.SDKMonitorTheme

/**
 * Modern app card component that displays app information
 * with beautiful Material Design 3 styling
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppCard(
    appVersion: AppVersion,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    appIcon: Bitmap? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(appVersion.backgroundColor))
            ) {
                if (appIcon != null) {
                    Image(
                        bitmap = appIcon.asImageBitmap(),
                        contentDescription = "App icon for ${appVersion.title}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Android,
                        contentDescription = "Default app icon",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // App Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = appVersion.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Target SDK: ${appVersion.sdkVersion}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    text = appVersion.lastUpdateTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // SDK Badge
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = when {
                    appVersion.sdkVersion >= 34 -> Color(0xFF4CAF50) // Green for latest
                    appVersion.sdkVersion >= 30 -> Color(0xFFFF9800) // Orange for moderate
                    else -> Color(0xFFFF5722) // Red for old
                }
            ) {
                Text(
                    text = appVersion.sdkVersion.toString(),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Preview(name = "App Card Light")
@Preview(name = "App Card Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AppCardPreview() {
    val sampleAppVersion = AppVersion(
        packageName = "com.example.app",
        title = "Sample Amazing App",
        sdkVersion = 34,
        lastUpdateTime = "2 days ago",
        versionName = "1.0.0",
        versionCode = 1L,
        backgroundColor = 0xFF2196F3.toInt(),
        isFromPlayStore = true
    )
    
    SDKMonitorTheme {
        Surface(
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            AppCard(
                appVersion = sampleAppVersion,
                onClick = { }
            )
        }
    }
}
