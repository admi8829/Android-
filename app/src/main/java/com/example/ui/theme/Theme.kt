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

private val DarkColorScheme =
  darkColorScheme(
    primary = EthioBlue,
    secondary = EthioBlueSecondary,
    tertiary = EthioGoldAccent,
    background = EthioTextPrimary,
    surface = EthioTextPrimary,
    onPrimary = PureWhite,
    onBackground = EthioBackground,
    onSurface = EthioBackground
  )

private val LightColorScheme =
  lightColorScheme(
    primary = EthioBlue,
    secondary = EthioBlueSecondary,
    tertiary = EthioGoldAccent,
    background = EthioBackground,
    surface = PureWhite,
    onPrimary = PureWhite,
    onSecondary = PureWhite,
    onTertiary = PureWhite,
    onBackground = EthioTextPrimary,
    onSurface = EthioTextPrimary
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Disable dynamic color fallback to guarantee user gets our stylized white/blue academic guidelines
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> LightColorScheme // Stick to LightColorScheme as requested by user ("color white")
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
