package com.bernaferrari.sdkmonitor.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Indigo80,
    secondary = IndigoGrey80,
    tertiary = Blue80
)

private val LightColorScheme = lightColorScheme(
    primary = Indigo40,
    secondary = IndigoGrey40,
    tertiary = Blue40
)

@Composable
fun SDKMonitorTheme(
    darkTheme: Boolean? = null,
    dynamicColor: Boolean? = null,
    themeViewModel: ThemeViewModel,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    
    // Use theme from ViewModel if no override provided
    val shouldUseDarkTheme = darkTheme ?: themeViewModel.shouldUseDarkTheme()
    val shouldUseDynamicColor = dynamicColor ?: themeViewModel.shouldUseDynamicColor()

    val colorScheme = when {
        shouldUseDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (shouldUseDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        shouldUseDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !shouldUseDarkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
