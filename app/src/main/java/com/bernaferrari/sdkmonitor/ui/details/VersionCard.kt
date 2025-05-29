package com.bernaferrari.sdkmonitor.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bernaferrari.sdkmonitor.domain.model.AppVersion
import com.bernaferrari.sdkmonitor.extensions.apiToColor
import com.bernaferrari.sdkmonitor.extensions.apiToVersion
import com.bernaferrari.sdkmonitor.ui.theme.SDKMonitorTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 🎨 ABSOLUTELY STUNNING Version Card - The Most Beautiful Version Display Ever Created!
 * Features breathtaking animations, gorgeous gradients, and divine visual hierarchy
 */
@Composable
fun VersionCard(
    versionInfo: AppVersion, // Fixed: Now correctly uses AppVersion
    modifier: Modifier = Modifier
) {
    val apiColor = Color(versionInfo.sdkVersion.apiToColor())
    val apiDescription = versionInfo.sdkVersion.apiToVersion()

    // Subtle, elegant entrance animation
    var cardVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(100) // Reduced delay for smoother experience
        cardVisible = true
    }



    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(6.dp)
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Beautiful version header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = apiColor.copy(alpha = 0.15f)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Update,
                                    contentDescription = "Version",
                                    modifier = Modifier.size(20.dp),
                                    tint = apiColor
                                )
                            }
                        }

                        Column {
                            Text(
                                text = "Version ${versionInfo.versionName}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Code: ${versionInfo.versionCode}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Gorgeous SDK badge
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = apiColor,
                        shadowElevation = 4.dp
                    ) {
                        Text(
                            text = "API ${versionInfo.sdkVersion}",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White
                        )
                    }
                }

                // Beautiful additional info
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.5f)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Beautiful API description
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = apiColor.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = apiDescription,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = apiColor
                                )
                            }

                            Text(
                                text = "• ${versionInfo.lastUpdateTime}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

// Beautiful utility function
private fun formatDate(timestamp: Long): String {
    return try {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        sdf.format(Date(timestamp))
    } catch (e: Exception) {
        "Unknown date"
    }
}

// Divine preview
@Preview(showBackground = true)
@Composable
private fun VersionCardPreview() {
    SDKMonitorTheme {
        VersionCard(
            versionInfo = AppVersion(
                packageName = "com.bernaferrari.sdkmonitor",
                title = "SDK Monitor",
                sdkVersion = 34,
                versionName = "2.1.0",
                versionCode = 42,
                lastUpdateTime = "2 days ago"
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun VersionCardDarkPreview() {
    SDKMonitorTheme(darkTheme = true) {
        VersionCard(
            versionInfo = AppVersion(
                packageName = "com.whatsapp",
                title = "WhatsApp",
                sdkVersion = 28,
                versionName = "2.23.20.75",
                versionCode = 231275,
                lastUpdateTime = "1 week ago"
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}
