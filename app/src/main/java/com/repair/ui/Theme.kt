package com.repair.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/* ---------- 国风配色 ---------- */
val Paper = Color(0xFFF4EFE3)
val PaperCard = Color(0xFFFCF9F2)
val PaperDeep = Color(0xFFEBE3D2)
val Ink = Color(0xFF211E19)
val Ink2 = Color(0xFF544C3C)
val Ink3 = Color(0xFF8B8271)
val Jade = Color(0xFF2E5A4C)
val JadeLight = Color(0xFF5C8776)
val Cinnabar = Color(0xFFA6432F)
val Line = Color(0x1A211E19)
val LineSoft = Color(0x0D211E19)

/** 四季色卡：主色 / 浅底 / 深字 / 渐变 */
data class SeasonSkin(
    val accent: Color,
    val soft: Color,
    val deep: Color,
    val gradient: Brush
)

fun skinOf(season: String): SeasonSkin = when (season) {
    "春" -> SeasonSkin(
        accent = Color(0xFF5E8B6A), soft = Color(0xFFD7E2D3), deep = Color(0xFF3B6450),
        gradient = Brush.linearGradient(listOf(Color(0xFF54805F), Color(0xFF3A6350), Color(0xFF315645)))
    )
    "夏" -> SeasonSkin(
        accent = Color(0xFFAD4A35), soft = Color(0xFFEBD5CC), deep = Color(0xFF8E3A28),
        gradient = Brush.linearGradient(listOf(Color(0xFFB0553C), Color(0xFF95402E), Color(0xFF7D3425)))
    )
    "秋" -> SeasonSkin(
        accent = Color(0xFF9E8348), soft = Color(0xFFE8DCC0), deep = Color(0xFF7E6538),
        gradient = Brush.linearGradient(listOf(Color(0xFFA78A4C), Color(0xFF8B7038), Color(0xFF75602F)))
    )
    else -> SeasonSkin(
        accent = Color(0xFF39505F), soft = Color(0xFFD3DCE2), deep = Color(0xFF2E4652),
        gradient = Brush.linearGradient(listOf(Color(0xFF44606F), Color(0xFF314A58), Color(0xFF273C48)))
    )
}

/** 标题用衬线，正文用系统无衬线 */
val Serif = FontFamily.Serif

val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = Serif, fontSize = 52.sp, lineHeight = 60.sp,
        letterSpacing = 6.sp, fontWeight = FontWeight.Normal
    ),
    displaySmall = TextStyle(
        fontFamily = Serif, fontSize = 38.sp, lineHeight = 46.sp,
        letterSpacing = 5.sp, fontWeight = FontWeight.Normal
    ),
    headlineSmall = TextStyle(
        fontFamily = Serif, fontSize = 20.sp, lineHeight = 28.sp,
        letterSpacing = 2.sp, fontWeight = FontWeight.Bold
    ),
    titleLarge = TextStyle(
        fontFamily = Serif, fontSize = 17.sp, lineHeight = 24.sp,
        letterSpacing = 2.sp, fontWeight = FontWeight.Bold
    ),
    titleMedium = TextStyle(
        fontFamily = Serif, fontSize = 15.sp, lineHeight = 22.sp,
        letterSpacing = 1.5.sp, fontWeight = FontWeight.Medium
    ),
    titleSmall = TextStyle(
        fontFamily = Serif, fontSize = 13.5.sp, lineHeight = 20.sp,
        letterSpacing = 1.sp, fontWeight = FontWeight.Medium
    ),
    bodyLarge = TextStyle(fontSize = 14.5.sp, lineHeight = 26.sp, letterSpacing = 0.4.sp),
    bodyMedium = TextStyle(fontSize = 13.sp, lineHeight = 23.sp, letterSpacing = 0.4.sp),
    bodySmall = TextStyle(fontSize = 11.5.sp, lineHeight = 19.sp, letterSpacing = 0.3.sp),
    labelLarge = TextStyle(fontSize = 12.5.sp, letterSpacing = 1.2.sp),
    labelMedium = TextStyle(fontSize = 11.sp, letterSpacing = 0.8.sp),
    labelSmall = TextStyle(fontSize = 10.sp, lineHeight = 16.sp, letterSpacing = 0.8.sp)
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Jade,
            onPrimary = Color.White,
            secondary = Cinnabar,
            background = Paper,
            onBackground = Ink,
            surface = PaperCard,
            onSurface = Ink,
            surfaceVariant = PaperDeep,
            onSurfaceVariant = Ink2,
            outline = Line
        ),
        typography = AppTypography,
        content = content
    )
}
