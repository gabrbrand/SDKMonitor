package com.bernaferrari.sdkmonitor.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bernaferrari.sdkmonitor.domain.model.AppVersion
import com.bernaferrari.sdkmonitor.ui.theme.SDKMonitorTheme
import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Modern Fast Scroller Component for Compose
 * Beautiful Material Design 3 implementation with smooth animations
 */

@Composable
fun ModernFastScroller(
    listState: LazyListState,
    items: List<AppVersion>,
    modifier: Modifier = Modifier,
    onScrollToPosition: (Int) -> Unit = {}
) {
    var isVisible by remember { mutableStateOf(false) }
    var isScrolling by remember { mutableStateOf(false) }
    var scrollerHeight by remember { mutableFloatStateOf(0f) }
    var scrollerY by remember { mutableFloatStateOf(0f) }

    val density = LocalDensity.current

    // Get unique alphabet letters from app titles
    val alphabetIndices = remember(items) {
        items.mapIndexed { index, appVersion ->
            val firstChar = appVersion.title.firstOrNull()?.uppercaseChar()
            if (firstChar?.isLetter() == true) {
                firstChar to index
            } else {
                '#' to index
            }
        }.distinctBy { it.first }
    }

    // Hide scroller after inactivity
    LaunchedEffect(isScrolling) {
        if (!isScrolling) {
            delay(2000)
            isVisible = false
        }
    }

    // Show scroller when user scrolls
    LaunchedEffect(listState.isScrollInProgress) {
        if (listState.isScrollInProgress) {
            isVisible = true
            isScrolling = true
        } else {
            isScrolling = false
        }
    }

    Box(
        modifier = modifier.fillMaxHeight(),
        contentAlignment = Alignment.CenterEnd
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(spring(stiffness = Spring.StiffnessHigh)),
            exit = fadeOut(spring(stiffness = Spring.StiffnessHigh))
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Current letter indicator
                AnimatedVisibility(
                    visible = isScrolling,
                    enter = fadeIn() + androidx.compose.animation.scaleIn(),
                    exit = fadeOut() + androidx.compose.animation.scaleOut()
                ) {
                    CurrentLetterIndicator(
                        currentIndex = listState.firstVisibleItemIndex,
                        items = items
                    )
                }

                // Alphabet scroller
                AlphabetScroller(
                    alphabetIndices = alphabetIndices,
                    onLetterSelected = { index ->
                        onScrollToPosition(index)
                    },
                    modifier = Modifier
                        .onGloballyPositioned { coordinates ->
                            scrollerHeight = coordinates.size.height.toFloat()
                        }
                )
            }
        }
    }
}

@Composable
private fun AlphabetScroller(
    alphabetIndices: List<Pair<Char, Int>>,
    onLetterSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var dragOffset by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    val animatedElevation by animateDpAsState(
        targetValue = if (isDragging) 12.dp else 4.dp,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "elevation"
    )

    Card(
        modifier = modifier
            .width(48.dp)
            .padding(vertical = 16.dp)
            .shadow(animatedElevation, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .pointerInput(alphabetIndices) {
                    detectDragGestures(
                        onDragStart = {
                            isDragging = true
                        },
                        onDragEnd = {
                            isDragging = false
                            dragOffset = 0f
                        }
                    ) { _, _ ->
                        // Calculate which letter is being touched
                        val itemHeight = size.height / alphabetIndices.size.toFloat()
                        val touchedIndex = (dragOffset / itemHeight).roundToInt()
                            .coerceIn(0, alphabetIndices.size - 1)

                        onLetterSelected(alphabetIndices[touchedIndex].second)
                    }
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            alphabetIndices.forEach { (letter, _) ->
                AlphabetLetter(
                    letter = letter,
                    isHighlighted = isDragging
                )
            }
        }
    }
}

@Composable
private fun AlphabetLetter(
    letter: Char,
    isHighlighted: Boolean,
    modifier: Modifier = Modifier
) {
    val animatedScale by animateDpAsState(
        targetValue = if (isHighlighted) 24.dp else 20.dp,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "scale"
    )

    Box(
        modifier = modifier
            .size(animatedScale)
            .clip(CircleShape)
            .background(
                if (isHighlighted) {
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                } else {
                    Brush.radialGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Transparent
                        )
                    )
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = letter.toString(),
            fontSize = 10.sp,
            fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Medium,
            color = if (isHighlighted) {
                Color.White
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

@Composable
private fun CurrentLetterIndicator(
    currentIndex: Int,
    items: List<AppVersion>,
    modifier: Modifier = Modifier
) {
    val currentLetter = remember(currentIndex, items) {
        if (items.isNotEmpty() && currentIndex < items.size) {
            val firstChar = items[currentIndex].title.firstOrNull()?.uppercaseChar()
            if (firstChar?.isLetter() == true) firstChar.toString() else "#"
        } else {
            ""
        }
    }

    if (currentLetter.isNotEmpty()) {
        Card(
            modifier = modifier,
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currentLetter,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ModernFastScrollerPreview() {
    SDKMonitorTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            ModernFastScroller(
                listState = LazyListState(),
                items = emptyList(),
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CurrentLetterIndicatorPreview() {
    SDKMonitorTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CurrentLetterIndicator(
                currentIndex = 0,
                items = emptyList()
            )
        }
    }
}
