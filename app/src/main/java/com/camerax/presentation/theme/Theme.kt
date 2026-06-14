package com.camerax.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.camerax.domain.model.ThemeMode

private val DarkColorScheme =
    darkColorScheme(
        primary = DarkPrimary,
        secondary = DarkSecondary,
        tertiary = DarkTertiary,
        background = DarkBackground,
        surface = DarkSurface,
        surfaceVariant = DarkSurfaceVariant,
        onPrimary = Color.White,
        onSecondary = Color.White,
        onTertiary = Color.Black,
        onBackground = DarkOnBackground,
        onSurface = DarkOnSurface,
        error = DarkError,
    )

private val LightColorScheme =
    lightColorScheme(
        primary = LightPrimary,
        secondary = LightSecondary,
        tertiary = LightTertiary,
        background = LightBackground,
        surface = LightSurface,
        surfaceVariant = LightSurfaceVariant,
        onPrimary = Color.White,
        onSecondary = Color.White,
        onTertiary = Color.Black,
        onBackground = LightOnBackground,
        onSurface = LightOnSurface,
        error = LightError,
    )

data class CameraXGradients(
    val primaryGradient: Brush,
    val backgroundGradient: Brush,
    val captureGradient: Brush,
)

val LocalCameraXGradients =
    staticCompositionLocalOf {
        CameraXGradients(
            primaryGradient = Brush.linearGradient(DarkPrimaryGradient),
            backgroundGradient = Brush.verticalGradient(listOf(DarkBackground, DarkSurface)),
            captureGradient = Brush.linearGradient(DarkCaptureGradient),
        )
    }

object CameraXTheme {
    val gradients: CameraXGradients
        @Composable
        @ReadOnlyComposable
        get() = LocalCameraXGradients.current
}

@Composable
fun CameraXTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit,
) {
    val darkTheme =
        when (themeMode) {
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
            ThemeMode.SYSTEM -> isSystemInDarkTheme()
        }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val customGradients =
        CameraXGradients(
            primaryGradient =
                Brush.linearGradient(
                    if (darkTheme) DarkPrimaryGradient else LightPrimaryGradient,
                ),
            backgroundGradient =
                Brush.verticalGradient(
                    if (darkTheme) {
                        listOf(DarkBackground, DarkBackground, DarkSurface)
                    } else {
                        listOf(LightBackground, LightBackground, LightSurface)
                    },
                ),
            captureGradient =
                Brush.linearGradient(
                    if (darkTheme) DarkCaptureGradient else LightCaptureGradient,
                ),
        )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            @Suppress("DEPRECATION")
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            @Suppress("DEPRECATION")
            window.navigationBarColor = android.graphics.Color.TRANSPARENT

            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !darkTheme
            controller.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    CompositionLocalProvider(
        LocalCameraXGradients provides customGradients,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content,
        )
    }
}
