package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimarySleekDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    secondary = SecondarySleek,
    onSecondary = OnSecondaryWhite,
    background = BackgroundNight,
    onBackground = OnBackgroundNight,
    surface = SurfaceNight,
    onSurface = OnSurfaceNight,
    surfaceVariant = SurfaceVariantNight,
    onSurfaceVariant = OnSurfaceVariantNight
)

private val LightColorScheme = lightColorScheme(
    primary = PrimarySleek,
    onPrimary = OnPrimaryWhite,
    primaryContainer = PrimaryContainerSleek,
    onPrimaryContainer = OnPrimaryContainerSleek,
    secondary = SecondarySleek,
    onSecondary = OnSecondaryWhite,
    secondaryContainer = SecondaryContainerSleek,
    tertiary = TertiarySleek,
    onTertiary = OnTertiaryWhite,
    background = BackgroundSleek,
    onBackground = OnBackgroundDark,
    surface = SurfaceSleek,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantSleek,
    onSurfaceVariant = OnSurfaceVariantDark
)

@Composable
fun RuangCeritaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our handcrafted literary theme
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
