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
  primary = TurquoisePrimary,
  onPrimary = OnTurquoisePrimary,
  secondary = TurquoiseSecondary,
  onSecondary = OnTurquoiseSecondary,
  tertiary = TurquoiseTertiary,
  background = DarkBackground,
  surface = DarkSurface,
  surfaceVariant = DarkSurfaceVariant,
  onBackground = TextLight,
  onSurface = TextLight,
  outline = TextMuted
)

private val LightColorScheme = darkColorScheme(
  primary = TurquoisePrimary,
  onPrimary = OnTurquoisePrimary,
  secondary = TurquoiseSecondary,
  onSecondary = OnTurquoiseSecondary,
  tertiary = TurquoiseTertiary,
  background = DarkBackground,
  surface = DarkSurface,
  surfaceVariant = DarkSurfaceVariant,
  onBackground = TextLight,
  onSurface = TextLight,
  outline = TextMuted
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force dark theme by default as requested
  dynamicColor: Boolean = false, // Disable dynamic colors to keep turquoise accents
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
