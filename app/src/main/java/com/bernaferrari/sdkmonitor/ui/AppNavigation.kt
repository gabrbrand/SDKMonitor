@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.bernaferrari.sdkmonitor.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowWidthSizeClass
import com.bernaferrari.sdkmonitor.R
import com.bernaferrari.sdkmonitor.ui.details.DetailsScreen
import com.bernaferrari.sdkmonitor.ui.logs.LogsScreen
import com.bernaferrari.sdkmonitor.ui.main.MainScreen
import com.bernaferrari.sdkmonitor.ui.settings.AboutScreen
import com.bernaferrari.sdkmonitor.ui.settings.SettingsScreen
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    initialPackageName: String? = null,
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavItems =
        listOf(
            BottomNavItem.Main,
            BottomNavItem.Logs,
            BottomNavItem.Settings,
        )

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            bottomNavItems.forEach { item ->
                item(
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                        )
                    },
                    label = { Text(stringResource(item.label)) },
                    selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
        modifier = modifier,
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.Main.route,
            modifier =
                Modifier
                    .fillMaxSize(),
        ) {
            composable(Screen.Main.route) {
                MainScreenWithListDetail(
                    navController = navController,
                    appStartupPackageName = initialPackageName,
                    screenRoute = Screen.Main.route,
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreenWithListDetail()
            }

            composable(Screen.Logs.route) {
                LogsScreenWithListDetail()
            }
        }
    }
}

@Composable
private fun MainScreenWithListDetail(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    appStartupPackageName: String?,
    screenRoute: String,
) {
    val listDetailNavigator = rememberListDetailPaneScaffoldNavigator<String>()
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    var startupDeepLinkApplied by remember { mutableStateOf(false) }

    LaunchedEffect(listDetailNavigator, appStartupPackageName, navBackStackEntry) {
        val isActive = navBackStackEntry?.destination?.route == screenRoute
        if (isActive) {
            // Only apply the startup deep link if appStartupPackageName is present
            // and it hasn't been applied for the current active state of this screen.
            if (!appStartupPackageName.isNullOrEmpty() && !startupDeepLinkApplied) {
                val currentDestination = listDetailNavigator.currentDestination
                // Navigate only if not already on the correct detail item and pane.
                if (currentDestination?.contentKey != appStartupPackageName ||
                    currentDestination.pane != ListDetailPaneScaffoldRole.Detail
                ) {
                    listDetailNavigator.navigateTo(
                        ListDetailPaneScaffoldRole.Detail,
                        appStartupPackageName,
                    )
                }
                startupDeepLinkApplied = true
            }
            // Removed the 'else' block that previously navigated to ListDetailPaneScaffoldRole.List.
            // The NavHost's saveState/restoreState mechanism will handle preserving
            // the listDetailNavigator's state (e.g., if a detail item was already selected).
        } else {
            // Reset startupDeepLinkApplied when the screen is no longer active.
            // This allows the deep link to be re-processed if the user navigates away
            // and then returns to this screen, and appStartupPackageName is still relevant.
            startupDeepLinkApplied = false
        }
    }

    NavigableListDetailPaneScaffold(
        navigator = listDetailNavigator,
        listPane = {
            AnimatedPane {
                MainScreen(
                    onNavigateToAppDetails = { packageName ->
                        scope.launch {
                            listDetailNavigator.navigateTo(
                                ListDetailPaneScaffoldRole.Detail,
                                packageName,
                            )
                        }
                    },
                    selectedPackageName = listDetailNavigator.currentDestination?.contentKey,
                )
            }
        },
        detailPane = {
            AnimatedPane {
                listDetailNavigator.currentDestination?.contentKey?.let { packageName ->
                    DetailsScreen(
                        packageName = packageName,
                        onNavigateBack = {
                            scope.launch {
                                listDetailNavigator.navigateBack()
                            }
                        },
                        isTabletSize = isTablet(),
                    )
                } ?: EmptyDetailState()
            }
        },
        modifier = modifier,
    )
}

@Composable
private fun LogsScreenWithListDetail(modifier: Modifier = Modifier) {
    val listDetailNavigator = rememberListDetailPaneScaffoldNavigator<String>()
    val scope = rememberCoroutineScope()

    NavigableListDetailPaneScaffold(
        navigator = listDetailNavigator,
        listPane = {
            AnimatedPane {
                LogsScreen(
                    onNavigateToAppDetails = { packageName ->
                        scope.launch {
                            listDetailNavigator.navigateTo(
                                ListDetailPaneScaffoldRole.Detail,
                                packageName,
                            )
                        }
                    },
                    selectedPackageName = listDetailNavigator.currentDestination?.contentKey,
                )
            }
        },
        detailPane = {
            AnimatedPane {
                listDetailNavigator.currentDestination?.contentKey?.let { packageName ->
                    DetailsScreen(
                        packageName = packageName,
                        onNavigateBack = {
                            scope.launch {
                                listDetailNavigator.navigateBack()
                            }
                        },
                        isTabletSize = isTablet(),
                    )
                } ?: EmptyDetailState()
            }
        },
        modifier = modifier,
    )
}

@Composable
private fun SettingsScreenWithListDetail(modifier: Modifier = Modifier) {
    val listDetailNavigator = rememberListDetailPaneScaffoldNavigator<String>()
    val scope = rememberCoroutineScope()

    NavigableListDetailPaneScaffold(
        navigator = listDetailNavigator,
        listPane = {
            AnimatedPane {
                SettingsScreen(
                    onNavigateToAppDetails = { packageName ->
                        scope.launch {
                            listDetailNavigator.navigateTo(
                                ListDetailPaneScaffoldRole.Detail,
                                packageName,
                            )
                        }
                    },
                    onNavigateToAbout = {
                        scope.launch {
                            listDetailNavigator.navigateTo(
                                ListDetailPaneScaffoldRole.Detail,
                                "about",
                            )
                        }
                    },
                )
            }
        },
        detailPane = {
            AnimatedPane {
                when (val contentKey = listDetailNavigator.currentDestination?.contentKey) {
                    "about" -> {
                        AboutScreen(
                            onNavigateBack = {
                                scope.launch {
                                    listDetailNavigator.navigateBack()
                                }
                            },
                            isTabletSize = isTablet(),
                        )
                    }

                    null -> { // Empty - no default content needed
                    }

                    else -> {
                        DetailsScreen(
                            packageName = contentKey,
                            onNavigateBack = {
                                scope.launch {
                                    listDetailNavigator.navigateBack()
                                }
                            },
                            isTabletSize = isTablet(),
                        )
                    }
                }
            }
        },
        modifier = modifier,
    )
}

@Composable
private fun EmptyDetailState() {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainer,
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                text = stringResource(R.string.select_app_to_get_started),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun isTablet(): Boolean {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    return when (windowSizeClass.windowWidthSizeClass) {
        WindowWidthSizeClass.MEDIUM, WindowWidthSizeClass.EXPANDED -> true
        else -> false
    }
}

private sealed class BottomNavItem(
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: Int,
) {
    data object Main : BottomNavItem(Screen.Main.route, Icons.Default.Apps, R.string.main_title)

    data object Logs : BottomNavItem(Screen.Logs.route, Icons.Default.History, R.string.logs_title)

    data object Settings :
        BottomNavItem(Screen.Settings.route, Icons.Default.Settings, R.string.settings_title)
}
