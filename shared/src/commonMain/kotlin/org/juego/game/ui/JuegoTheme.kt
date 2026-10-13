package org.juego.game.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val PaperBackground = Color(0xFFF3E6C9)
val BoardCream = Color(0xFFFFF8EA)
val GivenCell = Color(0xFFE8D7B5)
val SelectedCell = Color(0xFFF0D48A)
val Ink = Color(0xFF2A2118)
val GridLine = Color(0xFF3A2F24)
val Teal = Color(0xFF1C5854)
val TealSoft = Color(0xFFD7E6E3)
val Coral = Color(0xFFC45C4A)
val CorrectGreen = Color(0xFF2E7D57)
val ErrorRed = Color(0xFFB3261E)
val MutedInk = Color(0xFF7A6A58)

private val KubokColorScheme: ColorScheme = lightColorScheme(
    primary = Teal,
    onPrimary = Color(0xFFFFFBF3),
    primaryContainer = TealSoft,
    onPrimaryContainer = Color(0xFF0E2F2D),
    secondary = Coral,
    onSecondary = Color(0xFFFFFBF3),
    secondaryContainer = Color(0xFFF4D4CE),
    onSecondaryContainer = Color(0xFF4A1F18),
    background = PaperBackground,
    onBackground = Ink,
    surface = BoardCream,
    onSurface = Ink,
    surfaceVariant = GivenCell,
    onSurfaceVariant = MutedInk,
    outline = GridLine,
    error = ErrorRed,
    onError = Color(0xFFFFFBF3),
)

private val KubokTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        letterSpacing = 1.2.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        letterSpacing = 0.4.sp,
    ),
)

@Composable
fun JuegoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = KubokColorScheme,
        typography = KubokTypography,
        content = content,
    )
}
