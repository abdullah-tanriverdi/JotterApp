package com.tovaxtechnology.jotter.ui.theme

import android.app.Activity
import android.os.Build
import android.provider.CalendarContract.Colors
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.tovaxtechnology.jotter.R

val Quicksand = FontFamily(
    Font(R.font.quicksand_regular, FontWeight.Normal),
    Font(R.font.quicksand_medium, FontWeight.Medium),
    Font(R.font.quicksand_light, FontWeight.Light),
    Font(R.font.quicksand_bold, FontWeight.Bold),
    Font(R.font.quicksand_semibold, FontWeight.SemiBold)
)





private val DarkColorScheme = darkColorScheme(
    secondary = PurpleGrey80,


    tertiary = Color(0xfff85c70),
    primary = Color(0xFFA6E9A2),
    onSurface = Color.White, //yazı rengi
    onTertiary = Color(0xFFCCCCCC), //açık gri
    background = Color.Black, //siyah renk
    surface = Color(0xFF004AAD), // kartlar vs
    onBackground = Color(0xFFFF9800)


)

private val LightColorScheme = lightColorScheme(

    secondary = PurpleGrey40,


    tertiary = Color(0xfff85c70),
    primary = Color(0xFF2E7D32),
    onSurface = Color.Black, //yazı rengi
    onTertiary = Color(0xFF616161), //kapalı gri
    background = Color.White, //beyaz renk
    surface = Color(0xFFBBDEFB), //kartlar vs.
    onBackground = Color(0xFFFF9800)


    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun JotterTheme(
    darkTheme: Boolean,
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