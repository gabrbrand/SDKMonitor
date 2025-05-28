package com.bernaferrari.sdkmonitor.ui.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

/**
 * Modern navigation screens with proper argument definitions
 */
sealed class Screen(val route: String) {
    object Main : Screen("main")
    object Settings : Screen("settings") 
    object Logs : Screen("logs")
    object Details : Screen("details/{packageName}") {
        fun createRoute(packageName: String) = "details/$packageName"
        
        val arguments: List<NamedNavArgument> = listOf(
            navArgument("packageName") {
                type = NavType.StringType
                nullable = false
            }
        )
    }
}
