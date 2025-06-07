package com.bernaferrari.sdkmonitor.ui.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bernaferrari.sdkmonitor.R
import com.bernaferrari.sdkmonitor.ui.details.DetailsScreen
import com.bernaferrari.sdkmonitor.ui.logs.LogsScreen
import com.bernaferrari.sdkmonitor.ui.main.MainScreen
import com.bernaferrari.sdkmonitor.ui.settings.SettingsScreen

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    initialPackageName: String? = null
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Handle initial navigation from notification
    LaunchedEffect(initialPackageName) {
        if (!initialPackageName.isNullOrEmpty()) {
            navController.navigate(Screen.Details.createRoute(initialPackageName))
        }
    }

    val bottomNavItems = listOf(
        BottomNavItem.Main,
        BottomNavItem.Logs,
        BottomNavItem.Settings
    )

    val isDetailsScreen = currentDestination?.route == Screen.Details.route

    SharedTransitionLayout {
        Box(modifier = modifier) {
            // Main navigation with bottom bar (always present)
            Scaffold(
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                bottomBar = {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ) {
                        bottomNavItems.forEach { item ->
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = null
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
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        }
                    }
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = Screen.Main.route,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .consumeWindowInsets(innerPadding),
                ) {
                    composable(Screen.Main.route) {
                        MainScreen(
                            onNavigateToAppDetails = { packageName ->
                                navController.navigate(Screen.Details.createRoute(packageName))
                            },
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this
                        )
                    }

                    composable(Screen.Settings.route) {
                        SettingsScreen(
                            onNavigateToAppDetails = { packageName ->
                                navController.navigate(Screen.Details.createRoute(packageName))
                            }
                        )
                    }

                    composable(Screen.Logs.route) {
                        LogsScreen(
                            onNavigateToAppDetails = { packageName ->
                                navController.navigate(Screen.Details.createRoute(packageName))
                            }
                        )
                    }

                    composable(
                        route = Screen.Details.route,
                        arguments = Screen.Details.arguments
                    ) { backStackEntry ->
                        val packageName = backStackEntry.arguments?.getString("packageName") ?: ""
                        DetailsScreen(
                            packageName = packageName,
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

private sealed class BottomNavItem(
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: Int
) {
    data object Main : BottomNavItem(Screen.Main.route, Icons.Default.Apps, R.string.main_title)
    data object Logs :
        BottomNavItem(Screen.Logs.route, Icons.Default.History, R.string.logs_title)

    data object Settings :
        BottomNavItem(Screen.Settings.route, Icons.Default.Settings, R.string.settings_title)
}
