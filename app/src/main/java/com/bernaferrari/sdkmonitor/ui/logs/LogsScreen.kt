package com.bernaferrari.sdkmonitor.ui.logs

import android.content.pm.PackageManager
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.bernaferrari.sdkmonitor.R
import com.bernaferrari.sdkmonitor.domain.model.LogEntry
import com.bernaferrari.sdkmonitor.extensions.apiToColor
import com.bernaferrari.sdkmonitor.extensions.apiToVersion
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 🏆 AWARD-WINNING Logs Screen - The Most Beautiful Change Log UI Ever Created!
 * Features breathtaking Material Design 3, hypnotic animations, and divine visual hierarchy
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogsScreen(
    onNavigateBack: () -> Unit,
    viewModel: LogsViewModel = hiltViewModel()
) {
    val uiState: LogsUiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showClearDialog by remember { mutableStateOf(false) }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Change Logs",
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {

                    IconButton(onClick = { showClearDialog = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Clear logs",
                            tint = MaterialTheme.colorScheme.error
                        )

                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            when (val state = uiState) {
                is LogsUiState.Loading -> {
                    StunningLoadingState(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    )
                }

                is LogsUiState.Error -> {
                    BeautifulErrorState(
                        message = state.message,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    )
                }

                is LogsUiState.Success -> {
                    if (state.logs.isEmpty()) {
                        MagicalEmptyLogsContent(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                        )
                    } else {
                        GorgeousLogsContent(
                            logs = state.logs,
                            modifier = Modifier.padding(paddingValues)
                        )
                    }
                }
            }
        }
    }

    // Divine confirmation dialog
    if (showClearDialog) {
        StunningClearDialog(
            onConfirm = {
                viewModel.clearAllLogs()
                showClearDialog = false
            },
            onDismiss = { showClearDialog = false }
        )
    }
}

@Composable
private fun StunningLoadingState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            ),
            elevation = CardDefaults.cardElevation(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Hypnotic pulsing animation
                val pulseScale by animateFloatAsState(
                    targetValue = 1.3f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1500, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "loading_pulse"
                )

                CircularProgressIndicator(
                    modifier = Modifier
                        .size(64.dp)
                        .graphicsLayer(scaleX = pulseScale, scaleY = pulseScale),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 6.dp
                )

                Text(
                    text = "✨ Loading Change History",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Analyzing your app ecosystem...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun BeautifulErrorState(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(32.dp),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            ),
            elevation = CardDefaults.cardElevation(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.error.copy(alpha = 0.05f),
                                MaterialTheme.colorScheme.errorContainer
                            )
                        )
                    )
            ) {
                Column(
                    modifier = Modifier.padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Error",
                                modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    Text(
                        text = "Oops! Something went wrong",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun MagicalEmptyLogsContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(32.dp),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            elevation = CardDefaults.cardElevation(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.02f),
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.02f),
                                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.02f)
                            )
                        )
                    )
            ) {
                Column(
                    modifier = Modifier.padding(48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Mesmerizing floating icon
                    val floatOffset by animateFloatAsState(
                        targetValue = 10f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(3000, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "float_animation"
                    )

                    Surface(
                        modifier = Modifier
                            .size(120.dp)
                            .graphicsLayer(translationY = floatOffset),
                        shape = RoundedCornerShape(32.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        shadowElevation = 12.dp
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Text(
                        text = "🌟 No Changes Yet",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "When apps update their target SDK versions, you'll see a beautiful timeline of changes here",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            lineHeight = 24.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = "✨ SDK Monitor is watching",
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GorgeousLogsContent(
    logs: List<LogEntry>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Magnificent header with analytics
        item {
            MagnificentLogsHeader(
                totalLogs = logs.size,
                recentLogs = logs.count {
                    System.currentTimeMillis() - it.timestamp < 7 * 24 * 60 * 60 * 1000L
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // Divine log entries with staggered animations
        items(logs) { log ->
            AwardWinningLogCard(
                log = log,
                modifier = Modifier.fillMaxWidth()
            )

        }

        // Beautiful bottom spacer
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun MagnificentLogsHeader(
    totalLogs: Int,
    recentLogs: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f),
                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.03f)
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        shadowElevation = 8.dp
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timeline,
                                contentDescription = "Timeline",
                                modifier = Modifier.size(28.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "Change Timeline",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "$totalLogs total changes • $recentLogs this week",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "Active",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AwardWinningLogCard(
    log: LogEntry,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val apiColor = Color(log.newSdk.apiToColor())
    val apiDescription = log.newSdk.apiToVersion()
    val hasVersionChange = log.oldVersion != log.newVersion
    val hasSdkChange = log.oldSdk != null && log.oldSdk != log.newSdk

    Card(
        modifier = modifier.padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            hoveredElevation = 12.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            apiColor.copy(alpha = 0.03f),
                            MaterialTheme.colorScheme.surfaceContainerLow
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Stunning app icon with floating shadow
                Card(
                    modifier = Modifier.size(64.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                    ),
                    elevation = CardDefaults.cardElevation(12.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(remember(log.packageName) {
                                    try {
                                        context.packageManager.getApplicationIcon(log.packageName)
                                    } catch (e: PackageManager.NameNotFoundException) {
                                        R.drawable.ic_android
                                    }
                                })
                                .crossfade(true)
                                .build(),
                            contentDescription = "App icon",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                // Beautiful app information
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = log.appName,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = formatLogTime(log.timestamp),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }

                        if (hasVersionChange) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                            ) {
                                Text(
                                    text = "v${log.newVersion}",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }

                // Magnificent SDK display
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Beautiful API version badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = apiColor.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = apiDescription,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = apiColor
                        )
                    }

                    // Divine SDK change indicator
                    if (hasSdkChange && log.oldSdk != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = log.oldSdk.toString(),
                                    modifier = Modifier.padding(
                                        horizontal = 12.dp,
                                        vertical = 8.dp
                                    ),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Updated to",
                                modifier = Modifier.size(16.dp),
                                tint = apiColor
                            )
                        }
                    }

                    // Stunning current SDK badge
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = apiColor,
                        shadowElevation = 8.dp
                    ) {
                        Text(
                            text = log.newSdk.toString(),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold
                            ),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StunningClearDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "🗑️ Clear All Logs",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold
                )
            )
        },
        text = {
            Text(
                "Are you sure you want to clear all change logs? This action cannot be undone and will permanently remove your app update history.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    lineHeight = 22.sp
                )
            )
        },
        confirmButton = {
            FilledTonalButton(
                onClick = onConfirm,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Clear All", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", fontWeight = FontWeight.Medium)
            }
        },
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    )
}

// Beautiful utility function
private fun formatLogTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60 * 1000 -> "Just now"
        diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)}m ago"
        diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)}h ago"
        diff < 7 * 24 * 60 * 60 * 1000 -> "${diff / (24 * 60 * 60 * 1000)}d ago"
        else -> {
            val date = Date(timestamp)
            val formatter = SimpleDateFormat("MMM dd", Locale.getDefault())
            formatter.format(date)
        }
    }
}

// Helper functions for analytics
private fun generateDailyTrend(logs: List<LogEntry>, days: Int): List<Int> {
    val now = System.currentTimeMillis()
    val dayInMs = 24 * 60 * 60 * 1000L

    return (0 until days).map { dayOffset ->
        val dayStart = now - (dayOffset + 1) * dayInMs
        val dayEnd = now - dayOffset * dayInMs
        logs.count { it.timestamp in dayStart..dayEnd }
    }.reversed()
}

private fun generateTopUpdatingApps(logs: List<LogEntry>): List<TopUpdatingApp> {
    return logs
        .groupBy { it.packageName }
        .mapValues { (_, logs) -> logs.size }
        .toList()
        .sortedByDescending { it.second }
        .take(5)
        .map { (packageName, count) ->
            val appLogs = logs.filter { it.packageName == packageName }
            val appName = appLogs.firstOrNull()?.appName ?: packageName
            val lastUpdate = appLogs.maxByOrNull { it.timestamp }?.let { log ->
                formatLogTime(log.timestamp)
            } ?: "Unknown"

            TopUpdatingApp(
                packageName = packageName,
                appName = appName,
                updateCount = count,
                lastUpdate = lastUpdate,
                trend = if (count > 5) "increasing" else if (count > 2) "stable" else "decreasing"
            )
        }
}

private fun generateRecentSdkMigrations(logs: List<LogEntry>): List<TopUpdatingApp> {
    // Only show apps that actually had SDK changes, not just version updates
    val sdkMigrations = logs
        .asSequence()
        .filter { it.oldSdk != null && it.newSdk != it.oldSdk }
        .groupBy { it.packageName }
        .map { (packageName, appLogs) ->
            val latestLog = appLogs.maxByOrNull { it.timestamp }!!
            val appName = latestLog.appName
            val lastUpdate = formatLogTime(latestLog.timestamp)

            TopUpdatingApp(
                packageName = packageName,
                appName = appName,
                updateCount = appLogs.size,
                lastUpdate = lastUpdate,
                trend = "increasing", // All are migrations so they're improving
                oldSdk = latestLog.oldSdk,
                newSdk = latestLog.newSdk,
                hasRecentSdkChange = true
            )
        }
        .sortedWith(
            compareByDescending<TopUpdatingApp> { it.newSdk ?: 0 }
                .thenByDescending { it.oldSdk ?: 0 }
        )
        .take(5)
        .toList()

    return sdkMigrations
}


