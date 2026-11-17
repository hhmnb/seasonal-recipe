package com.repair.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.repair.data.SolarCalendar
import com.repair.data.TERMS
import com.repair.data.Term
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

@Composable
fun TermsScreen(nowIndex: Int, onOpenTerm: (Int) -> Unit) {
    var sel by remember { mutableStateOf(nowIndex) }
    val selTerm = TERMS[sel]
    val selSkin = skinOf(selTerm.season)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Paper),
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 8.dp, bottom = 28.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 6.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text("二十四节气", style = MaterialTheme.typography.headlineSmall, color = Ink, fontSize = 21.sp)
                    Spacer(Modifier.height(3.dp))
                    Text(
                        "THE 24 SOLAR TERMS",
                        style = MaterialTheme.typography.labelSmall,
                        color = Ink3,
                        letterSpacing = 2.4.sp
                    )
                }
                Spacer(Modifier.weight(1f))
                Column(horizontalAlignment = Alignment.End) {
                    Text("点击轮盘换节气", style = MaterialTheme.typography.labelSmall, color = Ink3)
                    Spacer(Modifier.height(3.dp))
                    Text(
                        "一年 · 四季 · 二十四候",
                        style = MaterialTheme.typography.labelMedium,
                        color = Jade,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // 轮盘
        item {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                val diameter = minOf(maxWidth, 320.dp)
                Wheel(
                    diameter = diameter,
                    selected = sel,
                    nowIndex = nowIndex,
                    onSelect = { sel = it }
                )
                // 中心内容
                Column(
                    modifier = Modifier.width(diameter * 0.52f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "${selTerm.season} · ${selTerm.seasonEn}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Ink3,
                        letterSpacing = 2.sp
                    )
                    Spacer(Modifier.height(7.dp))
                    Text(
                        selTerm.name,
                        style = MaterialTheme.typography.displaySmall,
                        color = selSkin.deep,
                        fontSize = 32.sp,
                        letterSpacing = 3.sp
                    )
                    Spacer(Modifier.height(5.dp))
                    Text(
                        "${selTerm.month} 月 · ${selTerm.phrase}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Ink3
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "进入节气 ›",
                        style = MaterialTheme.typography.labelMedium,
                        color = Jade,
                        modifier = Modifier
                            .border(1.dp, Jade.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { onOpenTerm(sel) }
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    )
                }
            }
        }

        item {
            Text(
                "外圈朱点标示当下所处节气",
                style = MaterialTheme.typography.labelSmall,
                color = Ink3,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            )
        }

        // 四季分组
        listOf("春", "夏", "秋", "冬").forEach { season ->
            item {
                SeasonBlock(
                    season = season,
                    nowIndex = nowIndex,
                    onOpenTerm = onOpenTerm
                )
            }
        }

        item {
            FootNote("点击任一节气，查看该节气的完整食养指引")
        }
    }
}

@Composable
private fun Wheel(diameter: Dp, selected: Int, nowIndex: Int, onSelect: (Int) -> Unit) {
    val density = LocalDensity.current
    val outerPx = with(density) { diameter.toPx() / 2f - 8.dp.toPx() }
    val innerPx = with(density) { diameter.toPx() / 2f - 8.dp.toPx() - diameter.toPx() * 0.126f }
    val midPx = (outerPx + innerPx) / 2f
    val labelSize = with(density) { (diameter.value * 0.029f).coerceIn(8.5f, 10f) }
    val selSeason = TERMS[selected].season

    Box(
        modifier = Modifier
            .size(diameter)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    val dx = offset.x - cx
                    val dy = offset.y - cy
                    val r = hypot(dx.toDouble(), dy.toDouble()).toFloat()
                    if (r in innerPx..outerPx) {
                        var deg = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                        deg = ((deg + 90f) % 360f + 360f) % 360f
                        val idx = (((deg + 7.5f) / 15f).toInt()) % 24
                        onSelect(idx)
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            drawCircle(
                color = Color(0x14211E19),
                radius = outerPx + 7.dp.toPx(),
                center = Offset(cx, cy),
                style = Stroke(1.dp.toPx())
            )
            TERMS.forEachIndexed { i, t ->
                val skin = skinOf(t.season)
                val color = when {
                    i == selected -> skin.accent
                    t.season == selSeason -> skin.soft
                    else -> Color(0xFFE6DDCA)
                }
                drawArc(
                    color = color,
                    startAngle = -97.5f + i * 15f,
                    sweepAngle = 13.4f,
                    useCenter = true,
                    topLeft = Offset(cx - outerPx, cy - outerPx),
                    size = Size(outerPx * 2, outerPx * 2)
                )
            }
            // 中心遮罩，形成环
            drawCircle(color = Color(0xFFFCF9F2), radius = innerPx, center = Offset(cx, cy))
            drawCircle(
                color = Color(0x1A211E19),
                radius = innerPx,
                center = Offset(cx, cy),
                style = Stroke(1.dp.toPx())
            )
            // 当下节气朱点
            val ang = Math.toRadians(-90.0 + nowIndex * 15.0)
            drawCircle(
                color = Cinnabar,
                radius = 2.6.dp.toPx(),
                center = Offset(
                    cx + (outerPx + 8.dp.toPx()) * cos(ang).toFloat(),
                    cy + (outerPx + 8.dp.toPx()) * sin(ang).toFloat()
                )
            )
        }

        // 24 个节气名
        TERMS.forEachIndexed { i, t ->
            val ang = Math.toRadians(-90.0 + i * 15.0)
            val dx = (midPx * cos(ang)).toFloat()
            val dy = (midPx * sin(ang)).toFloat()
            Text(
                text = t.name,
                fontSize = labelSize.sp,
                fontFamily = Serif,
                letterSpacing = 0.6.sp,
                color = if (i == selected) Color.White else Color(0xFF5F5745),
                modifier = Modifier
                    .align(Alignment.Center)
                    .graphicsLayer {
                        translationX = dx
                        translationY = dy
                    }
            )
        }
    }
}

@Composable
private fun SeasonBlock(season: String, nowIndex: Int, onOpenTerm: (Int) -> Unit) {
    val skin = skinOf(season)
    val items = TERMS.mapIndexed { i, t -> i to t }.filter { it.second.season == season }
    val rows = items.chunked(3)

    Column(modifier = Modifier.padding(top = 26.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                season,
                style = MaterialTheme.typography.titleLarge,
                color = skin.accent,
                fontSize = 16.sp
            )
            Spacer(Modifier.width(9.dp))
            Box(
                Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(Line)
            )
            Spacer(Modifier.width(9.dp))
            Text(
                items.firstOrNull()?.second?.seasonEn ?: "",
                style = MaterialTheme.typography.labelSmall,
                color = Ink3,
                letterSpacing = 2.sp
            )
        }
        Spacer(Modifier.height(11.dp))
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                row.forEach { (idx, term) ->
                    TermCell(
                        term = term,
                        isNow = idx == nowIndex,
                        skin = skin,
                        modifier = Modifier.weight(1f),
                        onClick = { onOpenTerm(idx) }
                    )
                }
                repeat(3 - row.size) {
                    Spacer(Modifier.weight(1f))
                }
            }
            Spacer(Modifier.height(9.dp))
        }
    }
}

@Composable
private fun TermCell(
    term: Term,
    isNow: Boolean,
    skin: SeasonSkin,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isNow) skin.soft.copy(alpha = 0.45f) else PaperCard)
            .border(
                1.dp,
                if (isNow) Cinnabar.copy(alpha = 0.35f) else Line,
                RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 11.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(term.name, style = MaterialTheme.typography.titleSmall, color = Ink)
        Spacer(Modifier.height(3.dp))
        Text(
            SolarCalendar.monthDayLabel(term),
            style = MaterialTheme.typography.labelSmall,
            color = Ink3
        )
        Spacer(Modifier.height(6.dp))
        Text(
            term.phrase,
            style = MaterialTheme.typography.labelSmall,
            color = if (isNow) Cinnabar else skin.accent,
            textAlign = TextAlign.Center
        )
    }
}
