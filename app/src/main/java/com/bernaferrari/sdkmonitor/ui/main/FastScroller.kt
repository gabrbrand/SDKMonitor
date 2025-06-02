package com.bernaferrari.sdkmonitor.ui.main

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bernaferrari.sdkmonitor.domain.model.AppVersion
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun FastScroller(
    modifier: Modifier = Modifier,
    apps: List<AppVersion>,
    listState: LazyListState,
    onLetterSelected: (String) -> Unit,
    onScrollFinished: () -> Unit,
    onInteractionStart: () -> Unit = {} // Add callback for when interaction starts
) {
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    // Get unique first letters from apps (sorted)
    val letters = remember(apps) {
        apps
            .mapNotNull { it.title.firstOrNull()?.uppercaseChar()?.toString() }
            .filter { it.first().isLetter() }
            .distinct()
            .sorted()
    }

    // Create letter to index mapping for fast scrolling
    val letterToIndex = remember(apps, letters) {
        val mapping = mutableMapOf<String, Int>()
        letters.forEach { letter ->
            val index = apps.indexOfFirst { app ->
                app.title.firstOrNull()?.uppercaseChar()?.toString() == letter
            }
            if (index >= 0 && index < apps.size) {
                mapping[letter] = index
            }
        }
        mapping
    }

    var isInteracting by remember { mutableStateOf(false) }
    var currentDragPosition by remember { mutableFloatStateOf(0f) }
    var scrollerSize by remember { mutableStateOf(IntSize.Zero) }

    // Auto-reset when apps list changes
    LaunchedEffect(apps) {
        isInteracting = false
        currentDragPosition = 0f
    }

    // Function to scroll to letter
    fun scrollToLetter(letter: String) {
        letterToIndex[letter]?.let { index ->
            coroutineScope.launch {
                val targetIndex = index.coerceIn(0, (apps.size - 1).coerceAtLeast(0))
                listState.scrollToItem(targetIndex)
            }
        }
    }

    // Function to handle position and select letter - FIXED calculation with bounds checking
    fun handlePositionAndSelectLetter(yPosition: Float) {
        if (scrollerSize.height <= 0 || letters.isEmpty()) return

        // Account for the Column's vertical padding (12.dp on top and bottom)
        val verticalPaddingPx = with(density) { 12.dp.toPx() }
        val usableHeight = (scrollerSize.height - (verticalPaddingPx * 2)).coerceAtLeast(1f)
        val adjustedY = (yPosition - verticalPaddingPx).coerceIn(0f, usableHeight)

        val progress = (adjustedY / usableHeight).coerceIn(0f, 1f)
        val letterIndex = (progress * (letters.size - 1)).toInt()
            .coerceIn(0, letters.size - 1)

        val selectedLetter = letters[letterIndex]

        onLetterSelected(selectedLetter)
        scrollToLetter(selectedLetter)
    }

    Surface(
        modifier = modifier
            .fillMaxHeight()
            .wrapContentWidth(Alignment.CenterHorizontally)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 12.dp)
                .onGloballyPositioned { scrollerSize = it.size }
                .pointerInput(letters) {
                    awaitPointerEventScope {
                        while (true) {
                            // Wait for initial touch
                            val down = awaitFirstDown()
                            onInteractionStart() // Call when interaction starts
                            isInteracting = true
                            currentDragPosition = down.position.y
                            handlePositionAndSelectLetter(down.position.y)

                            // Check if this becomes a drag or stays a tap
                            val change = awaitTouchSlopOrCancellation(down.id) { change, _ ->
                                // Consume the change to indicate we accept this as a drag
                                change.consume()
                            }

                            if (change != null) {
                                // It's a drag - handle drag events
                                drag(change.id) { dragChange ->
                                    currentDragPosition = dragChange.position.y
                                        .coerceIn(0f, scrollerSize.height.toFloat())
                                    handlePositionAndSelectLetter(currentDragPosition)
                                }
                            }
                            // If change is null, it was just a tap (no drag threshold exceeded)

                            // Reset state when gesture ends (both tap and drag end here)
                            isInteracting = false
                            onScrollFinished()
                        }
                    }
                }
        ) {
            // Letter indicators
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Calculate current position once, outside the loop
                val verticalPaddingPx = with(density) { 12.dp.toPx() }
                val usableHeight = (scrollerSize.height - (verticalPaddingPx * 2)).coerceAtLeast(1f)
                val adjustedDragPosition =
                    (currentDragPosition - verticalPaddingPx).coerceIn(0f, usableHeight)
                val currentPosition = if (usableHeight > 0) {
                    (adjustedDragPosition / usableHeight).coerceIn(0f, 1f)
                } else {
                    0f
                }

                letters.forEachIndexed { index, letter ->
                    val letterPosition = if (letters.size > 1) {
                        index.toFloat() / (letters.size - 1)
                    } else {
                        0.5f
                    }

                    val distance = abs(letterPosition - currentPosition)

                    // Scale based on proximity when dragging
                    val scale by animateFloatAsState(
                        targetValue = if (isInteracting) {
                            when {
                                distance < 0.05f -> 1.6f  // Very close
                                distance < 0.1f -> 1.3f   // Close
                                distance < 0.15f -> 1.1f  // Medium
                                else -> 0.9f              // Far
                            }
                        } else 1.0f,
                        animationSpec = spring(dampingRatio = 0.8f),
                        label = "letter_scale_$index"
                    )

                    val alpha by animateFloatAsState(
                        targetValue = if (isInteracting) {
                            when {
                                distance < 0.05f -> 1.0f
                                distance < 0.1f -> 0.9f
                                distance < 0.15f -> 0.7f
                                else -> 0.5f
                            }
                        } else 0.7f,
                        animationSpec = spring(),
                        label = "letter_alpha_$index"
                    )

                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = letter,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .graphicsLayer(
                                    scaleX = scale,
                                    scaleY = scale,
                                    alpha = alpha
                                )
                        )
                    }
                }
            }
        }
    }
}
