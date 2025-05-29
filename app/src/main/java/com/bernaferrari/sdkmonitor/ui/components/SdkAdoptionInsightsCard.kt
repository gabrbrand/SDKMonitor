package com.bernaferrari.sdkmonitor.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingFlat
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bernaferrari.sdkmonitor.domain.model.LogEntry
import com.bernaferrari.sdkmonitor.extensions.apiToColor
import com.bernaferrari.sdkmonitor.extensions.apiToVersion

data class SdkAdoptionInsight(
    val sdkVersion: Int,
    val adoptionTrend: String, // "rising", "stable", "declining"
    val recentAdoptions: Int,
    val timeToAdopt: String, // "Early adopter", "Fast follower", "Late adopter"
    val trendPercentage: Float
)

@Composable
fun SdkAdoptionInsightsCard(
    logs: List<LogEntry>, modifier: Modifier = Modifier
) {
    val insights = remember(logs) { generateSdkAdoptionInsights(logs) }

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
                            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Insights,
                                contentDescription = "SDK Insights",
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "SDK Adoption Insights",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "How developers adopt new SDKs",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "Trends",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            if (insights.isNotEmpty()) {
                // Top insight - most active SDK
                val topInsight = insights.first()

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(topInsight.sdkVersion.apiToColor()).copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(topInsight.sdkVersion.apiToColor())
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = topInsight.sdkVersion.toString(),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color.White
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Most Active SDK",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = Color(topInsight.sdkVersion.apiToColor())
                            )
                            Text(
                                text = topInsight.sdkVersion.apiToVersion(),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${topInsight.recentAdoptions} recent adoptions • ${topInsight.timeToAdopt}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Trend indicator
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when (topInsight.adoptionTrend) {
                                "rising" -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                                "stable" -> Color(0xFFFF9800).copy(alpha = 0.1f)
                                else -> Color(0xFFFF5722).copy(alpha = 0.1f)
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = when (topInsight.adoptionTrend) {
                                        "rising" -> Icons.AutoMirrored.Filled.TrendingUp
                                        "stable" -> Icons.AutoMirrored.Filled.TrendingFlat
                                        else -> Icons.AutoMirrored.Filled.TrendingDown
                                    },
                                    contentDescription = topInsight.adoptionTrend,
                                    modifier = Modifier.size(12.dp),
                                    tint = when (topInsight.adoptionTrend) {
                                        "rising" -> Color(0xFF4CAF50)
                                        "stable" -> Color(0xFFFF9800)
                                        else -> Color(0xFFFF5722)
                                    }
                                )
                                Text(
                                    text = "${topInsight.trendPercentage.toInt()}%",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = when (topInsight.adoptionTrend) {
                                        "rising" -> Color(0xFF4CAF50)
                                        "stable" -> Color(0xFFFF9800)
                                        else -> Color(0xFFFF5722)
                                    }
                                )
                            }
                        }
                    }
                }

                // Other insights
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(insights.drop(1).take(4)) { insight ->
                        SdkInsightMiniCard(insight = insight)
                    }
                }
            } else {
                EmptyInsightsContent()
            }
        }
    }
}

@Composable
private fun SdkInsightMiniCard(
    insight: SdkAdoptionInsight, modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.width(140.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp), color = Color(insight.sdkVersion.apiToColor())
            ) {
                Text(
                    text = insight.sdkVersion.toString(),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )
            }

            Text(
                text = "${insight.recentAdoptions} apps",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Text(
                text = insight.timeToAdopt,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun EmptyInsightsContent() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp), contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Insights,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Gathering insights...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun generateSdkAdoptionInsights(logs: List<LogEntry>): List<SdkAdoptionInsight> {
    val now = System.currentTimeMillis()
    val thirtyDaysAgo = now - (30L * 24 * 60 * 60 * 1000)
    val ninetyDaysAgo = now - (90L * 24 * 60 * 60 * 1000)

    return logs.filter { it.oldSdk != it.newSdk && it.oldSdk != null }.groupBy { it.newSdk }
        .mapNotNull { (sdkVersion, migrations) ->
            val recent30 = migrations.count { it.timestamp > thirtyDaysAgo }
            val recent90 = migrations.count { it.timestamp > ninetyDaysAgo }

            if (recent30 == 0) return@mapNotNull null

            val trend = when {
                recent30 > recent90 * 0.7f -> "rising"
                recent30 > recent90 * 0.3f -> "stable"
                else -> "declining"
            }

            val timeToAdopt = when {
                sdkVersion >= 34 -> "Early adopter"
                sdkVersion >= 31 -> "Fast follower"
                else -> "Late adopter"
            }

            val trendPercentage = if (recent90 > 0) {
                ((recent30.toFloat() / (recent90.toFloat() / 3f)) - 1f) * 100f
            } else 100f

            SdkAdoptionInsight(
                sdkVersion = sdkVersion,
                adoptionTrend = trend,
                recentAdoptions = recent30,
                timeToAdopt = timeToAdopt,
                trendPercentage = trendPercentage.coerceIn(-100f, 500f)
            )
        }.sortedByDescending { it.recentAdoptions }
}
