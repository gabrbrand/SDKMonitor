package com.bernaferrari.sdkmonitor.ui.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

/**
 * Navigation screens with proper argument definitions
 */
sealed class Screen(val route: String) {
    data object Main : Screen("main")
    data object Settings : Screen("settings")
    data object Logs : Screen("logs")
    data object Details : Screen("details/{packageName}") {
        fun createRoute(packageName: String) = "details/$packageName"
        
        val arguments: List<NamedNavArgument> = listOf(
            navArgument("packageName") {
                type = NavType.StringType
                nullable = false
            }
        )
    }
}
