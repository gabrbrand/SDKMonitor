package com.bernaferrari.sdkmonitor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.bernaferrari.sdkmonitor.ui.navigation.SDKMonitorNavigation
import com.bernaferrari.sdkmonitor.ui.theme.SDKMonitorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge display
        enableEdgeToEdge()
        
        // Ensure the app draws behind system bars
        WindowCompat.setDecorFitsSystemWindows(window, true)
        
        setContent {
            SDKMonitorTheme {
                SDKMonitorNavigation(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
