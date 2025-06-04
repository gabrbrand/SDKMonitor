package com.bernaferrari.sdkmonitor.ui.settings

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bernaferrari.sdkmonitor.R
import com.bernaferrari.sdkmonitor.domain.model.AppFilter
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
    val currentAppFilter by remember { mutableStateOf(AppFilter.ALL_APPS) }

    // Filter data based on app type - you'll need to add app type info to SdkDistribution
    // For now, showing all data as placeholder
    val filteredData = sdkDistribution // TODO: Filter by app type when data structure supports it

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
            // Enhanced Header with app type filter buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = stringResource(R.string.analytics),
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.sdk_analytics),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Total apps stat
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                ) {
                                    Text(
                                        text = totalApps.toString(),
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    text = stringResource(R.string.apps_lowercase),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // SDK versions stat
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.tertiaryContainer,
                                ) {
                                    Text(
                                        text = filteredData.size.toString(),
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    text = stringResource(R.string.versions_lowercase),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Chart with filtered data
            if (filteredData.isNotEmpty()) {
                SdkBarChart(
                    data = filteredData,
                    animationProgress = animationProgress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )

                // Legend with filtered data
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredData.sortedByDescending { it.sdkVersion }.take(5)) { sdk ->
                        SdkLegendItem(
                            sdkVersion = sdk.sdkVersion,
                            appCount = sdk.appCount,
                            percentage = sdk.percentage,
                            onClick = { onSdkClick(sdk.sdkVersion) }
                        )
                    }
                }
            } else {
                // Empty state for filtered data
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
                            text = stringResource(
                                R.string.no_filtered_apps_found,
                                when (currentAppFilter) {
                                    AppFilter.ALL_APPS -> stringResource(R.string.all_apps)
                                    AppFilter.USER_APPS -> stringResource(R.string.user_apps)
                                    AppFilter.SYSTEM_APPS -> stringResource(R.string.system_apps)
                                }.lowercase()
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = stringResource(R.string.try_changing_filter),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
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
                        y + 16.dp.toPx(),
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
                text = stringResource(R.string.apps_count, appCount),
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
