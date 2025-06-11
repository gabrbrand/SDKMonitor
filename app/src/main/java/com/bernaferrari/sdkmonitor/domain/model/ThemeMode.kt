package com.bernaferrari.sdkmonitor.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.ui.graphics.vector.ImageVector
import com.bernaferrari.sdkmonitor.R

/**
 * 🎨 Beautiful Theme Mode Options - The Perfect Visual Experience!
 * Supports Material You dynamic theming with gorgeous fallbacks
 */
enum class ThemeMode(
    val displayNameRes: Int,
    val descriptionRes: Int,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
) {
    SYSTEM(
        displayNameRes = R.string.theme_system,
        descriptionRes = R.string.theme_system_description,
        icon = Icons.Outlined.PhoneAndroid,
        selectedIcon = Icons.Filled.PhoneAndroid,
    ),
    MATERIAL_YOU(
        displayNameRes = R.string.theme_material_you,
        descriptionRes = R.string.theme_material_you_description,
        icon = Icons.Outlined.Palette,
        selectedIcon = Icons.Filled.Palette,
    ),
    LIGHT(
        displayNameRes = R.string.theme_light,
        descriptionRes = R.string.theme_light_description,
        icon = Icons.Outlined.LightMode,
        selectedIcon = Icons.Filled.LightMode,
    ),
    DARK(
        displayNameRes = R.string.theme_dark,
        descriptionRes = R.string.theme_dark_description,
        icon = Icons.Outlined.DarkMode,
        selectedIcon = Icons.Filled.DarkMode,
    ),
}
