package com.bernaferrari.sdkmonitor.ui.logs

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bernaferrari.sdkmonitor.ui.theme.SDKMonitorTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class UpdateTrendData(
    val period: String,
    val totalUpdates: Int,
    val appUpdates: Int,
    val sdkUpdates: Int,
    val dailyTrend: List<Int> = emptyList()
)

@Composable
fun UpdateTrendsCard(
    modifier: Modifier = Modifier,
    trends: List<UpdateTrendData>,
) {
    val animationProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(
            durationMillis = 1200,
            easing = FastOutSlowInEasing
        ),
        label = "trends_animation"
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
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timeline,
                                contentDescription = "Trends",
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "Update Trends",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Recent activity overview",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }


            }

            // Trend Cards
            if (trends.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(trends.size) { index ->
                        val trend = trends[index]
                        TrendPeriodCard(
                            trend = trend,
                            animationProgress = animationProgress,
                            isHighlighted = index == 0
                        )
                    }
                }

                // Mini timeline chart
                if (trends.isNotEmpty() && trends[0].dailyTrend.isNotEmpty()) {
                    TrendTimelineChart(
                        data = trends[0].dailyTrend,
                        animationProgress = animationProgress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                    )
                }
            } else {
                EmptyTrendsContent()
            }
        }
    }
}

@Composable
private fun TrendPeriodCard(
    trend: UpdateTrendData,
    animationProgress: Float,
    isHighlighted: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isHighlighted) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainerHighest
    }

    val contentColor = if (isHighlighted) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = modifier.width(140.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isHighlighted) 4.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = trend.period,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = contentColor.copy(alpha = 0.8f)
            )

            Text(
                text = (trend.totalUpdates * animationProgress).toInt().toString(),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = contentColor
            )

            Text(
                text = "total updates",
                style = MaterialTheme.typography.bodySmall,
                color = contentColor.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Beautiful SDK Changes highlight with gradient
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.Transparent
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        )
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = trend.sdkUpdates.toString(),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = Color.White
                    )
                }
            }

            Text(
                text = "SDK Updates",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary
            )

            // Enhanced secondary metrics with beautiful styling
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = "${trend.appUpdates} app updates",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                if (trend.totalUpdates > 0) {
                    val sdkPercentage =
                        ((trend.sdkUpdates.toFloat() / trend.totalUpdates) * 100).toInt()
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = "$sdkPercentage% SDK focused",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UpdateTypeIndicator(
    count: Int,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = color.copy(alpha = 0.1f)
        ) {
            Text(
                text = count.toString(),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                ),
                color = color
            )
        }

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TrendTimelineChart(
    modifier: Modifier = Modifier,
    data: List<Int>,
    animationProgress: Float,
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Chart
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(12.dp)
            ) {
                if (data.isEmpty()) return@Canvas

                val maxValue = data.maxOrNull() ?: 1
                val stepX = size.width / (data.size - 1).coerceAtLeast(1)
                val stepY = size.height

                val path = Path()
                val gradientPath = Path()

                data.forEachIndexed { index, value ->
                    val x = index * stepX
                    val y = size.height - (value.toFloat() / maxValue) * stepY * animationProgress

                    if (index == 0) {
                        path.moveTo(x, y)
                        gradientPath.moveTo(x, size.height)
                        gradientPath.lineTo(x, y)
                    } else {
                        path.lineTo(x, y)
                        gradientPath.lineTo(x, y)
                    }
                }

                gradientPath.lineTo(size.width, size.height)
                gradientPath.close()

                // Draw gradient area
                drawPath(
                    path = gradientPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.3f),
                            primaryColor.copy(alpha = 0.05f)
                        )
                    )
                )

                // Draw line
                drawPath(
                    path = path,
                    color = primaryColor,
                    style = Stroke(
                        width = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                )

                // Draw data points
                data.forEachIndexed { index, value ->
                    val x = index * stepX
                    val y = size.height - (value.toFloat() / maxValue) * stepY * animationProgress

                    drawCircle(
                        color = primaryColor,
                        radius = 4.dp.toPx(),
                        center = Offset(x, y)
                    )

                    drawCircle(
                        color = Color.White,
                        radius = 2.dp.toPx(),
                        center = Offset(x, y)
                    )
                }
            }

            // Date range labels
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatDateLabel(-data.size + 1),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "Today",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

private fun formatDateLabel(daysAgo: Int): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, daysAgo)

    return when {
        daysAgo >= -7 -> "${-daysAgo}d ago"
        daysAgo >= -30 -> "${(-daysAgo) / 7}w ago"
        else -> {
            val format = SimpleDateFormat("MMM dd", Locale.getDefault())
            format.format(calendar.time)
        }
    }
}

@Composable
private fun EmptyTrendsContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Timeline,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "No trend data available",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UpdateTrendsCardPreview() {
    SDKMonitorTheme {
        UpdateTrendsCard(
            trends = listOf(
                UpdateTrendData(
                    period = "30 Days",
                    totalUpdates = 47,
                    appUpdates = 32,
                    sdkUpdates = 15,
                    dailyTrend = listOf(2, 5, 3, 8, 4, 6, 9, 3, 7, 5, 4, 8, 6, 3, 9),
                ),
                UpdateTrendData(
                    period = "90 Days",
                    totalUpdates = 127,
                    appUpdates = 89,
                    sdkUpdates = 38,
                )
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}
