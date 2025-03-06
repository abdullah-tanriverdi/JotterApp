package com.tbox.jotter.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.tbox.jotter.PoppinsFont


// Açık mod renk paleti
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4CAF50),  // Yeşil (Primary)
    secondary = Color(0xFF2196F3), // Mavi (Secondary)
    tertiary = Color(0xFFE59300),  //  (Tertiary)
    background = Color(0xFFF5F5F5), // Açık gri arka plan
    surface = Color(0xFFFFFFFF), // Beyaz yüzey
    onPrimary = Color.White, // Primary üzerindeki metin beyaz
    onSecondary = Color.White, // Secondary üzerindeki metin beyaz
    onTertiary = Color.Black, // Tertiary üzerindeki metin siyah
    onBackground = Color.Black, // Arka plandaki metin siyah
    onSurface = Color.Black // Yüzeydeki metin siyah,


)

// Koyu mod renk paleti
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4CAF50),  // Yeşil (Primary)
    secondary = Color(0xFF2196F3), // Mavi (Secondary)
    tertiary = Color(0xFFE59300),  //  (Tertiary)
    background = Color(0xFFF5F5F5), // Açık gri arka plan
    surface = Color(0xFFFFFFFF), // Beyaz yüzey
    onPrimary = Color.White, // Primary üzerindeki metin beyaz
    onSecondary = Color.White, // Secondary üzerindeki metin beyaz
    onTertiary = Color.Black, // Tertiary üzerindeki metin siyah
    onBackground = Color.Black, // Arka plandaki metin siyah
    onSurface = Color.Black // Yüzeydeki metin siyah,
)


val PoppinsTypography = Typography(


    //App Bar
    headlineLarge = TextStyle(
        fontFamily = PoppinsFont,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp
    ),


    //Card Başlıkları
    titleMedium = TextStyle(
        fontFamily = PoppinsFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp
    ),

    // İçerik metni (ana gövde metni)
    bodyLarge = TextStyle(
        fontFamily = PoppinsFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    // İçerik metni (ikincil)
    bodyMedium = TextStyle(
        fontFamily = PoppinsFont,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    // İçerik metni (küçük)
    bodySmall = TextStyle(
        fontFamily = PoppinsFont,
        fontWeight = FontWeight.Light,
        fontSize = 12.sp
    ),
    // Etiket metinleri (buton, yardımcı metinler vs.)
    labelLarge = TextStyle(
        fontFamily = PoppinsFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    ),
    labelMedium = TextStyle(
        fontFamily = PoppinsFont,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    labelSmall = TextStyle(
        fontFamily = PoppinsFont,
        fontWeight = FontWeight.Light,
        fontSize = 10.sp
    )
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
        typography = PoppinsTypography,
        content = content
    )
}