package com.tbox.jotter.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color



// Açık mod renk paleti
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4CAF50),  // Yeşil (Primary)
    secondary = Color(0xFF2196F3), // Mavi (Secondary)
    tertiary = Color(0xFFFFFFFF),  // Beyaz (Tertiary)
    background = Color(0xFFF5F5F5), // Açık gri arka plan
    surface = Color(0xFFFFFFFF), // Beyaz yüzey
    onPrimary = Color.White, // Primary üzerindeki metin beyaz
    onSecondary = Color.White, // Secondary üzerindeki metin beyaz
    onTertiary = Color.Black, // Tertiary üzerindeki metin siyah
    onBackground = Color.Black, // Arka plandaki metin siyah
    onSurface = Color.Black // Yüzeydeki metin siyah
)

// Koyu mod renk paleti
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF388E3C), // Koyu Yeşil (Primary)
    secondary = Color(0xFF1976D2), // Koyu Mavi (Secondary)
    tertiary = Color(0xFFFFFFFF), // Beyaz (Tertiary)
    background = Color(0xFF121212), // Koyu arka plan
    surface = Color(0xFF121212), // Koyu yüzey
    onPrimary = Color.Black, // Primary üzerindeki metin siyah
    onSecondary = Color.White, // Secondary üzerindeki metin beyaz
    onTertiary = Color.Black, // Tertiary üzerindeki metin siyah
    onBackground = Color.White, // Arka plandaki metin beyaz
    onSurface = Color.White // Yüzeydeki metin beyaz
)


@Composable
fun JotterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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