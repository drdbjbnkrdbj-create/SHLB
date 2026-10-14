package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val StreqaDarkColorScheme = darkColorScheme(
    primary = StreqaTeal,
    onPrimary = Color(0xFF042F2E),
    primaryContainer = StreqaTealDark,
    onPrimaryContainer = StreqaTealLight,
    secondary = StreqaPink,
    onSecondary = Color(0xFF4C0519),
    secondaryContainer = StreqaPinkDark,
    onSecondaryContainer = Color(0xFFFCE7F3),
    tertiary = StreqaAmber,
    background = StreqaDarkBg,
    onBackground = TextPrimary,
    surface = StreqaCardBg,
    onSurface = TextPrimary,
    surfaceVariant = StreqaSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = StreqaCardBorder,
    error = StatusFailedRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to stunning dark theme matching reference images
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = StreqaDarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = StreqaDarkBg.toArgb()
            window.navigationBarColor = StreqaDarkBg.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
