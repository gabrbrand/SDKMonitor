package com.bernaferrari.sdkmonitor.ui.settings

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bernaferrari.sdkmonitor.extensions.apiToColor
import com.bernaferrari.sdkmonitor.extensions.apiToVersion
import com.bernaferrari.sdkmonitor.ui.theme.SDKMonitorTheme

data class SdkDistribution(
    val sdkVersion: Int,
    val appCount: Int,
    val percentage: Float
)

@Composable
fun SdkAnalyticsCard(
    modifier: Modifier = Modifier,
    sdkDistribution: List<SdkDistribution>,
    totalApps: Int,
    onSdkClick: (Int) -> Unit = {},
) {
    val animationProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(
            durationMillis = 1500,
            easing = FastOutSlowInEasing
        ),
        label = "chart_animation"
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
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Analytics,
                                contentDescription = "Analytics",
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "SDK Distribution",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "$totalApps apps analyzed",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${sdkDistribution.size} SDKs",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Chart
            if (sdkDistribution.isNotEmpty()) {
                SdkBarChart(
                    data = sdkDistribution,
                    animationProgress = animationProgress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )

                // Legend with most popular SDKs
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Show top 5 SDK versions by version number (highest first), not by app count
                    items(sdkDistribution.sortedByDescending { it.sdkVersion }.take(5)) { sdk ->
                        SdkLegendItem(
                            sdkVersion = sdk.sdkVersion,
                            appCount = sdk.appCount,
                            percentage = sdk.percentage,
                            onClick = { onSdkClick(sdk.sdkVersion) }
                        )
                    }
                }
            } else {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "No data available",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SdkBarChart(
    modifier: Modifier = Modifier,
    data: List<SdkDistribution>,
    animationProgress: Float,
    onBarClick: (Int) -> Unit = {},
) {
    val maxCount = data.maxOfOrNull { it.appCount } ?: 1

    // Extract theme colors outside of Canvas context
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant

    // Sort data by SDK version (highest to lowest) for proper display
    val sortedData = data.sortedByDescending { it.sdkVersion }

    Canvas(
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures { offset ->
                val barWidth = size.width / (sortedData.size * 1.5f)
                val barSpacing = barWidth * 0.5f

                sortedData.forEachIndexed { index, sdk ->
                    val x = index * (barWidth + barSpacing) + barSpacing
                    if (offset.x >= x && offset.x <= x + barWidth) {
                        onBarClick(sdk.sdkVersion)
                        return@detectTapGestures
                    }
                }
            }
        }
    ) {
        val barWidth = size.width / (sortedData.size * 1.5f)
        val barSpacing = barWidth * 0.5f
        val chartHeight = size.height - 40.dp.toPx()
        val cornerRadius = 8.dp.toPx()

        sortedData.forEachIndexed { index, sdk ->
            val barHeight = (sdk.appCount.toFloat() / maxCount) * chartHeight * animationProgress
            val x = index * (barWidth + barSpacing) + barSpacing
            val y = size.height - barHeight - 20.dp.toPx()

            val apiColor = Color(sdk.sdkVersion.apiToColor())

            // Draw rounded gradient bar
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        apiColor,
                        apiColor.copy(alpha = 0.7f)
                    )
                ),
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                    x = cornerRadius,
                    y = cornerRadius
                )
            )

            // Draw SDK version label
            drawContext.canvas.nativeCanvas.apply {
                val textPaint = android.graphics.Paint().apply {
                    color = if (size.width > 400.dp.toPx()) {
                        onSurfaceColor.toArgb()
                    } else {
                        onSurfaceVariantColor.toArgb()
                    }
                    textSize = if (size.width > 400.dp.toPx()) 28f else 24f
                    textAlign = android.graphics.Paint.Align.CENTER
                    isFakeBoldText = true
                }

                drawText(
                    sdk.sdkVersion.toString(),
                    x + barWidth / 2,
                    size.height - 5.dp.toPx(),
                    textPaint
                )
            }

            // Draw app count on top of bar if there's space
            if (barHeight > 30.dp.toPx()) {
                drawContext.canvas.nativeCanvas.apply {
                    val countPaint = android.graphics.Paint().apply {
                        color = Color.White.toArgb()
                        textSize = 20f
                        textAlign = android.graphics.Paint.Align.CENTER
                        isFakeBoldText = true
                    }

                    drawText(
                        sdk.appCount.toString(),
                        x + barWidth / 2,
                        y + 25.dp.toPx(),
                        countPaint
                    )
                }
            }
        }
    }
}

@Composable
private fun SdkLegendItem(
    modifier: Modifier = Modifier,
    sdkVersion: Int,
    appCount: Int,
    percentage: Float,
    onClick: () -> Unit = {},
) {
    val apiColor = Color(sdkVersion.apiToColor())
    val apiDescription = sdkVersion.apiToVersion()

    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = apiColor.copy(alpha = 0.1f)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = apiColor
            ) {
                Text(
                    text = sdkVersion.toString(),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )
            }

            Text(
                text = apiDescription,
                style = MaterialTheme.typography.labelSmall,
                color = apiColor,
                maxLines = 1,
                textAlign = TextAlign.Center
            )

            Text(
                text = "$appCount apps",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "${(percentage * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SdkAnalyticsCardPreview() {
    SDKMonitorTheme {
        SdkAnalyticsCard(
            sdkDistribution = listOf(
                SdkDistribution(34, 15, 0.3f),
                SdkDistribution(33, 12, 0.24f),
                SdkDistribution(31, 10, 0.2f),
                SdkDistribution(29, 8, 0.16f),
                SdkDistribution(28, 5, 0.1f)
            ),
            totalApps = 50,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SdkAnalyticsCardEmptyPreview() {
    SDKMonitorTheme {
        SdkAnalyticsCard(
            sdkDistribution = emptyList(),
            totalApps = 0,
            modifier = Modifier.padding(16.dp)
        )
    }
}
