package com.bernaferrari.sdkmonitor.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bernaferrari.sdkmonitor.domain.model.LogEntry

data class DeveloperBehavior(
    val type: String,
    val description: String,
    val appCount: Int,
    val percentage: Float,
    val insight: String,
    val color: Color
)

@Composable
fun DeveloperBehaviorCard(
    logs: List<LogEntry>,
    modifier: Modifier = Modifier
) {
    val behaviors = remember(logs) { analyzeDeveloperBehaviors(logs) }
    
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
                        color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = "Developer Behavior",
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                    
                    Column {
                        Text(
                            text = "Developer Behavior",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "How your apps adapt to SDK changes",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "Patterns",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }

            if (behaviors.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(behaviors) { behavior ->
                        DeveloperBehaviorCard(behavior = behavior)
                    }
                }
                
                // Main insight
                val mainBehavior = behaviors.maxByOrNull { it.percentage }
                if (mainBehavior != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = mainBehavior.color.copy(alpha = 0.1f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = "Insight",
                                tint = mainBehavior.color,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = mainBehavior.insight,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            } else {
                EmptyBehaviorContent()
            }
        }
    }
}

@Composable
private fun DeveloperBehaviorCard(
    behavior: DeveloperBehavior,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.width(160.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = behavior.color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "${behavior.percentage.toInt()}%",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = behavior.color
            )
            
            Text(
                text = behavior.type,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = behavior.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = behavior.color.copy(alpha = 0.2f)
            ) {
                Text(
                    text = "${behavior.appCount} apps",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = behavior.color
                )
            }
        }
    }
}

@Composable
private fun EmptyBehaviorContent() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Psychology,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Analyzing behavior patterns...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun analyzeDeveloperBehaviors(logs: List<LogEntry>): List<DeveloperBehavior> {
    if (logs.isEmpty()) return emptyList()
    
    val uniqueApps = logs.map { it.packageName }.distinct()
    val totalApps = uniqueApps.size.toFloat()
    
    val behaviors = mutableListOf<DeveloperBehavior>()
    
    // Early adopters (SDK >= 34)
    val earlyAdopters = logs.filter { it.newSdk >= 34 }.map { it.packageName }.distinct()
    if (earlyAdopters.isNotEmpty()) {
        behaviors.add(DeveloperBehavior(
            type = "Early Adopters",
            description = "Quick to adopt latest SDKs",
            appCount = earlyAdopters.size,
            percentage = (earlyAdopters.size / totalApps) * 100f,
            insight = "Your apps are staying current with the latest Android features!",
            color = Color(0xFF4CAF50)
        ))
    }
    
    // Conservative updaters (SDK < 31)
    val conservative = logs.filter { it.newSdk < 31 }.map { it.packageName }.distinct()
    if (conservative.isNotEmpty()) {
        behaviors.add(DeveloperBehavior(
            type = "Conservative",
            description = "Prefer stability over new features",
            appCount = conservative.size,
            percentage = (conservative.size / totalApps) * 100f,
            insight = "Some apps prioritize stability - consider gradual SDK updates.",
            color = Color(0xFFFF9800)
        ))
    }
    
    // Frequent updaters
    val frequentUpdaters = logs.groupBy { it.packageName }
        .filter { it.value.size >= 3 }
        .keys
    if (frequentUpdaters.isNotEmpty()) {
        behaviors.add(DeveloperBehavior(
            type = "Active Maintainers",
            description = "Regular updates and improvements",
            appCount = frequentUpdaters.size,
            percentage = (frequentUpdaters.size / totalApps) * 100f,
            insight = "These apps show excellent maintenance patterns!",
            color = Color(0xFF2196F3)
        ))
    }
    
    return behaviors.sortedByDescending { it.percentage }
}
