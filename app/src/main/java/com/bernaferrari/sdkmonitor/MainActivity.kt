package com.bernaferrari.sdkmonitor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.bernaferrari.sdkmonitor.ui.navigation.AppNavigation
import com.bernaferrari.sdkmonitor.ui.theme.SDKMonitorTheme
import com.bernaferrari.sdkmonitor.ui.theme.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        setContent {
            val themeViewModel: ThemeViewModel = hiltViewModel()
            
            // Handle notification navigation
            var initialPackageName by remember { mutableStateOf<String?>(null) }
            
            LaunchedEffect(intent) {
                if (intent?.getBooleanExtra("navigate_to_details", false) == true) {
                    val packageName = intent.getStringExtra("package_name")
                    if (!packageName.isNullOrEmpty()) {
                        initialPackageName = packageName
                    }
                }
            }
            
            SDKMonitorTheme(
                themeViewModel = themeViewModel
            ) {
                AppNavigation(
                    modifier = Modifier.fillMaxSize(),
                    initialPackageName = initialPackageName
                )
            }
        }
    }
}
