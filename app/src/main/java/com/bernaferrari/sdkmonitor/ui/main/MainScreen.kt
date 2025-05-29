package com.bernaferrari.sdkmonitor.ui.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bernaferrari.sdkmonitor.domain.model.AppFilter
import com.bernaferrari.sdkmonitor.domain.model.SortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToLogs: () -> Unit,
    onNavigateToAppDetails: (String) -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val appFilter by viewModel.appFilter.collectAsStateWithLifecycle()
    val sortOption by viewModel.sortOption.collectAsStateWithLifecycle()

    // Launch effect to load apps when screen first loads
    LaunchedEffect(Unit) {
        viewModel.loadApps()
    }

    // Add state for sort menu
    var showSortMenu by remember { mutableStateOf(false) }

    // Delightful animation for the entire content
    val contentVisible by remember { mutableStateOf(true) }
    val contentAnimation = animateFloatAsState(
        targetValue = if (contentVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "content_animation"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // Animated title with delightful bounce
                    AnimatedContent(
                        targetState = "SDK Monitor",
                        transitionSpec = {
                            slideInVertically { -it } + fadeIn() togetherWith
                                    slideOutVertically { it } + fadeOut()
                        },
                        label = "title_animation"
                    ) { title ->
                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    // Delightful staggered animation for action buttons
                    val buttonsVisible by remember { mutableStateOf(true) }
                    
                    AnimatedVisibility(
                        visible = buttonsVisible,
                        enter = slideInHorizontally { it } + fadeIn(),
                        exit = slideOutHorizontally { it } + fadeOut()
                    ) {
                        Row {
                            IconButton(onClick = onNavigateToLogs) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = "View logs",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = onNavigateToSettings) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Settings",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .graphicsLayer(alpha = contentAnimation.value)
        ) {
            // Ultra-Beautiful Filled Search Field - Material Design 3 Perfection
            TextField(
                value = searchQuery,
                onValueChange = viewModel::updateSearchQuery,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                placeholder = { 
                    Text(
                        "Search apps...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    ) 
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = if (searchQuery.isNotEmpty()) {
                    {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                } else null,
                singleLine = true,
                shape = RoundedCornerShape(28.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                textStyle = MaterialTheme.typography.bodyLarge
            )

            // Clean filter chips without elastic animations
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppFilter.entries.forEachIndexed { index, filter ->
                    val isSelected = appFilter == filter
                    
                    FilterChip(
                        onClick = { 
                            viewModel.updateAppFilter(filter) 
                        },
                        label = {
                            Text(
                                text = filter.displayName,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                                )
                            )
                        },
                        selected = isSelected,
                        leadingIcon = {
                            Icon(
                                imageVector = when (filter) {
                                    AppFilter.ALL_APPS -> Icons.Default.Apps
                                    AppFilter.USER_APPS -> Icons.Default.Person
                                    AppFilter.SYSTEM_APPS -> Icons.Default.Android
                                },
                                contentDescription = filter.displayName,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.height(40.dp)
                    )
                }
            }

            // Perfect Sort & Count Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Elegant Sort Dropdown
                Box {
                    FilterChip(
                        onClick = { showSortMenu = true },
                        label = {
                            Text(
                                text = "Sort: ${sortOption.displayName}",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        },
                        selected = false,
                        leadingIcon = {
                            Icon(
                                imageVector = sortOption.icon,
                                contentDescription = "Sort options",
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Expand sort options",
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.height(36.dp)
                    )

                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false },
                        shape = RoundedCornerShape(16.dp),
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        shadowElevation = 8.dp
                    ) {
                        SortOption.values().forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Icon(
                                            imageVector = option.icon,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp),
                                            tint = if (sortOption == option) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                            }
                                        )
                                        Text(
                                            text = option.displayName,
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontWeight = if (sortOption == option) {
                                                    FontWeight.SemiBold
                                                } else {
                                                    FontWeight.Normal
                                                }
                                            ),
                                            color = if (sortOption == option) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.onSurface
                                            }
                                        )
                                        if (sortOption == option) {
                                            Spacer(modifier = Modifier.weight(1f))
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                modifier = Modifier.size(16.dp),
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    viewModel.updateSortOption(option)
                                    showSortMenu = false
                                },
                                modifier = Modifier.height(48.dp)
                            )
                        }
                    }
                }

                // Beautiful App Count Badge
                when (val state = uiState) {
                    is MainUiState.Success -> {
                        val countScale by animateFloatAsState(
                            targetValue = 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy
                            ),
                            label = "count_scale"
                        )
                        
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shadowElevation = 2.dp,
                            modifier = Modifier.graphicsLayer(
                                scaleX = countScale,
                                scaleY = countScale
                            )
                        ) {
                            Text(
                                text = "${state.filteredApps.size} ${
                                    when (appFilter) {
                                        AppFilter.ALL_APPS -> "apps"
                                        AppFilter.USER_APPS -> "user apps"
                                        AppFilter.SYSTEM_APPS -> "system apps"
                                    }
                                }",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    else -> {}
                }
            }

            // Perfect Content States
            when (val state = uiState) {
                is MainUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Pulsing loading indicator
                            val loadingScale by animateFloatAsState(
                                targetValue = 1.2f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(1000),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "loading_scale"
                            )
                            
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(48.dp)
                                    .graphicsLayer(scaleX = loadingScale, scaleY = loadingScale),
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 4.dp
                            )
                            Text(
                                text = "Loading your beautiful apps...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                is MainUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier.padding(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            shape = RoundedCornerShape(20.dp),
                            elevation = CardDefaults.cardElevation(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = "Error",
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Text(
                                    text = "Oops! Something went wrong",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Text(
                                    text = state.message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f),
                                    textAlign = TextAlign.Center
                                )
                                FilledTonalButton(
                                    onClick = { viewModel.retryLoadApps() },
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = MaterialTheme.colorScheme.onErrorContainer,
                                        contentColor = MaterialTheme.colorScheme.errorContainer
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Retry",
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Retry")
                                }
                            }
                        }
                    }
                }

                is MainUiState.Success -> {
                    when {
                        state.filteredApps.isEmpty() && searchQuery.isNotBlank() -> {
                            // Enhanced empty search state...
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Surface(
                                        modifier = Modifier.size(80.dp),
                                        shape = RoundedCornerShape(24.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.SearchOff,
                                                contentDescription = "No results",
                                                modifier = Modifier.size(32.dp),
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                    Text(
                                        text = "No apps found",
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Try searching for \"$searchQuery\" with different filters",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                        
                        state.filteredApps.isEmpty() -> {
                            // Enhanced empty state...
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Surface(
                                        modifier = Modifier.size(80.dp),
                                        shape = RoundedCornerShape(24.dp),
                                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Apps,
                                                contentDescription = "No apps",
                                                modifier = Modifier.size(32.dp),
                                                tint = MaterialTheme.colorScheme.secondary
                                            )
                                        }
                                    }
                                    Text(
                                        text = "No ${appFilter.displayName.lowercase()} found",
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Try changing your filter or refreshing",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        
                        else -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                itemsIndexed(state.filteredApps) { index, appVersion ->
                                    // Staggered animation for each item
                                    val itemVisible by remember { mutableStateOf(true) }
                                    val itemDelay = (index * 50).coerceAtMost(300)
                                    
                                    AnimatedVisibility(
                                        visible = itemVisible,
                                        enter = slideInVertically(
                                            animationSpec = tween(
                                                durationMillis = 400,
                                                delayMillis = itemDelay,
                                                easing = FastOutSlowInEasing
                                            )
                                        ) { it } + fadeIn(
                                            animationSpec = tween(
                                                durationMillis = 400,
                                                delayMillis = itemDelay
                                            )
                                        )
                                    ) {
                                        MainAppCard(
                                            appVersion = appVersion,
                                            onClick = { onNavigateToAppDetails(appVersion.packageName) }
                                        )
                                    }
                                }
                                
                                // Beautiful bottom spacing
                                item {
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
