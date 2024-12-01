package com.example.tiratuoferta.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Definir la paleta de colores personalizada
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF00695C), // Verde petróleo
    secondary = Color(0xFFFF7043), // Naranja coral
    background = Color(0xFF37474F), // Negro carbón
    surface = Color(0xFF37474F), // Fondo oscuro
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    tertiary = Color(0xFFFDD835) // Amarillo mostaza
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF00695C), // Verde petróleo
    secondary = Color(0xFFFF7043), // Naranja coral
    background = Color(0xFFECEFF1), // Gris humo
    surface = Color(0xFFECEFF1), // Fondo claro
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF37474F), // Texto oscuro en fondo claro
    onSurface = Color(0xFF37474F), // Texto oscuro en superficies claras
    tertiary = Color(0xFFFDD835) // Amarillo mostaza
)

@Composable
fun TiraTuOfertaTheme(
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