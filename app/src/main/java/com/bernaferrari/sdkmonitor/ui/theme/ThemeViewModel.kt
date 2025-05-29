package com.bernaferrari.sdkmonitor.ui.theme

import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bernaferrari.sdkmonitor.domain.model.ThemeMode
import com.bernaferrari.sdkmonitor.domain.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _themeMode = MutableStateFlow(ThemeMode.MATERIAL_YOU)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    init {
        observeThemePreferences()
    }

    private fun observeThemePreferences() {
        viewModelScope.launch {
            preferencesRepository.getUserPreferences()
                .catch { /* Handle error silently, use default */ }
                .collect { preferences ->
                    _themeMode.value = preferences.themeMode
                }
        }
    }

    @Composable
    fun shouldUseDarkTheme(): Boolean {
        val currentTheme by themeMode.collectAsState()
        val systemInDarkTheme = isSystemInDarkTheme()

        return when (currentTheme) {
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
            ThemeMode.SYSTEM -> systemInDarkTheme
            ThemeMode.MATERIAL_YOU -> systemInDarkTheme
        }
    }

    @Composable
    fun shouldUseDynamicColor(): Boolean {
        val currentTheme by themeMode.collectAsState()
        return currentTheme == ThemeMode.MATERIAL_YOU && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    }
}
