package com.bernaferrari.sdkmonitor.ui.components

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
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bernaferrari.sdkmonitor.R
import com.bernaferrari.sdkmonitor.ui.theme.SDKMonitorTheme

/**
 * Beautiful header component for the logs screen
 * Features gradient background and animated elements
 */
@Composable
fun LogsHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Icon container with beautiful background
                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shadowElevation = 4.dp
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_history_24dp),
                            contentDescription = "Logs icon",
                            modifier = Modifier.size(28.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                // Text content
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
                
                // Decorative element
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_trending_up_24dp),
                            contentDescription = "Trending icon",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LogsHeaderPreview() {
    SDKMonitorTheme {
        LogsHeader(
            title = "TargetSDK Logs",
            subtitle = "127 changes detected"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LogsHeaderDarkPreview() {
    SDKMonitorTheme(darkTheme = true) {
        LogsHeader(
            title = "TargetSDK Logs",
            subtitle = "127 changes detected"
        )
    }
}
