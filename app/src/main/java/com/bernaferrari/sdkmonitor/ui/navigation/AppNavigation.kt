package com.bernaferrari.sdkmonitor.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bernaferrari.sdkmonitor.ui.details.DetailsScreen
import com.bernaferrari.sdkmonitor.ui.logs.LogsScreen
import com.bernaferrari.sdkmonitor.ui.main.MainScreen
import com.bernaferrari.sdkmonitor.ui.settings.SettingsScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        composable(Screen.Main.route) {
            MainScreen(
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToLogs = {
                    navController.navigate(Screen.Logs.route)
                },
                onNavigateToAppDetails = { packageName ->
                    navController.navigate(Screen.Details.createRoute(packageName))
                }
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Logs.route) {
            LogsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Details.route) { backStackEntry ->
            val packageName = backStackEntry.arguments?.getString("packageName") ?: ""
            DetailsScreen(
                packageName = packageName,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
