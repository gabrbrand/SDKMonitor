package com.bernaferrari.sdkmonitor.ui.main

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bernaferrari.sdkmonitor.domain.model.AppVersion
import kotlinx.coroutines.launch

@Composable
fun FastScroller(
    apps: List<AppVersion>,
    listState: LazyListState,
    onLetterSelected: (String) -> Unit,
    onScrollFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

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
            val index = apps.indexOfFirst {
                it.title.firstOrNull()?.uppercaseChar()?.toString() == letter
            }
            if (index != -1) {
                mapping[letter] = index
            }
        }
        mapping
    }

    var isDragging by remember { mutableStateOf(false) }
    var currentDragPosition by remember { mutableFloatStateOf(0f) }
    var scrollerSize by remember { mutableStateOf(IntSize.Zero) }


    // Function to handle position and select letter
    fun handlePositionAndSelectLetter(yPosition: Float) {
        val progress = (yPosition / scrollerSize.height).coerceIn(0f, 1f)
        val letterIndex = (progress * (letters.size - 1)).toInt()
            .coerceIn(0, letters.size - 1)

        if (letters.isNotEmpty()) {
            val selectedLetter = letters[letterIndex]
            onLetterSelected(selectedLetter)
            scrollToLetter(selectedLetter)
        }
    }

    // Function to scroll to letter
    fun scrollToLetter(letter: String) {
        letterToIndex[letter]?.let { index ->
            coroutineScope.launch {
                listState.scrollToItem(index.coerceAtMost(apps.size - 1))
            }
        }
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
                    detectTapGestures { offset ->
                        handlePositionAndSelectLetter(offset.y)
                    }
                }
                .pointerInput(letters) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            isDragging = true
                            currentDragPosition = offset.y
                            handlePositionAndSelectLetter(offset.y)
                        },
                        onDrag = { change, offset ->
                            currentDragPosition = (currentDragPosition + offset.y)
                                .coerceIn(0f, scrollerSize.height.toFloat())
                            handlePositionAndSelectLetter(currentDragPosition)
                        },
                        onDragEnd = {
                            isDragging = false
                            onScrollFinished()
                        }
                    )
                }
        ) {
            // Letter indicators
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                letters.forEachIndexed { index, letter ->
                    val letterPosition = if (letters.size > 1) {
                        index.toFloat() / (letters.size - 1)
                    } else {
                        0.5f
                    }
                    val currentPosition = if (scrollerSize.height > 0) {
                        currentDragPosition / scrollerSize.height
                    } else {
                        0f
                    }
                    val distance = kotlin.math.abs(letterPosition - currentPosition)

                    // Scale based on proximity when dragging
                    val scale by animateFloatAsState(
                        targetValue = if (isDragging) {
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
                        targetValue = if (isDragging) {
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
