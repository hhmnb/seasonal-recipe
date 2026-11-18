package com.repair.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.repair.data.Food
import com.repair.data.NowTerm
import com.repair.data.TERMS
import java.util.Calendar

private val WD = arrayOf("日", "一", "二", "三", "四", "五", "六")

@Composable
fun HomeScreen(
    now: NowTerm,
    onOpenTerm: (Int) -> Unit,
    onOpenRecipe: (Int, Int) -> Unit,
    onOpenFood: (Int, Int) -> Unit
) {
    val term = TERMS[now.index]
    val next = TERMS[now.nextIndex]
    val skin = skinOf(term.season)
    val cal = remember { Calendar.getInstance() }
    val dateLabel = remember {
        "${cal.get(Calendar.YEAR)}.${pad2(cal.get(Calendar.MONTH) + 1)}.${pad2(cal.get(Calendar.DAY_OF_MONTH))} 周${WD[cal.get(Calendar.DAY_OF_WEEK) - 1]}"
    }
    val dishIndex = remember { cal.get(Calendar.DAY_OF_MONTH) % term.recipes.size }
    val dish = term.recipes[dishIndex]

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Paper),
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 8.dp, bottom = 28.dp)
    ) {
        // 顶部
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 14.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        "节气食养",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Ink,
                        fontSize = 21.sp
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        "SOLAR TERM CUISINE",
                        style = MaterialTheme.typography.labelSmall,
                        color = Ink3,
                        letterSpacing = 2.4.sp
                    )
                }
                Spacer(Modifier.weight(1f))
                Column(horizontalAlignment = Alignment.End) {
                    Text(dateLabel, style = MaterialTheme.typography.labelSmall, color = Ink3)
                    Spacer(Modifier.height(3.dp))
                    Text(
                        "${term.season}·${term.name} 第 ${now.passedDays + 1} 天",
                        style = MaterialTheme.typography.labelMedium,
                        color = Jade,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // 主卡
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(skin.gradient)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 20.dp, end = 20.dp)
                        .size(50.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.22f), CircleShape)
                )
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "${term.season} · 第 ${now.index + 1} 个节气 · ${term.seasonEn}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.78f),
                            letterSpacing = 1.6.sp
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = term.name,
                        style = MaterialTheme.typography.displayLarge,
                        color = Color.White
                    )
                    Text(
                        text = term.py,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.5f),
                        letterSpacing = 4.sp
                    )
                    Spacer(Modifier.height(14.dp))
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color.White.copy(alpha = 0.18f))
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "${term.phrase}　·　此时宜养${organOf(term.season)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = term.hou,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.62f),
                        letterSpacing = 1.2.sp
                    )
                    Spacer(Modifier.height(16.dp))
                    // 节气进度
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .clip(RoundedCornerShape(1.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Box(
                            Modifier
                                .fillMaxWidth(now.progress.coerceIn(0.02f, 1f))
                                .height(2.dp)
                                .background(Color.White.copy(alpha = 0.9f))
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "距${next.name} ${now.toNextDays} 天",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = "节气详解 ›",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.92f),
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .border(1.dp, Color.White.copy(alpha = 0.34f), RoundedCornerShape(20.dp))
                                .clickable { onOpenTerm(now.index) }
                                .padding(horizontal = 13.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // 宜忌
        item {
            Row(
                modifier = Modifier.padding(top = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                YijiBox(
                    isYi = true,
                    items = term.yi.take(4),
                    modifier = Modifier.weight(1f)
                )
                YijiBox(
                    isYi = false,
                    items = term.ji.take(3),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 时令食材
        item {
            SectionTitle(
                text = "时令食材",
                more = "全部 ${term.foods.size} 味 ›",
                onMore = { onOpenTerm(now.index) }
            )
        }
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(end = 4.dp)
            ) {
                items(term.foods) { food ->
                    FoodChip(food, skin.accent) {
                        onOpenFood(now.index, term.foods.indexOf(food))
                    }
                }
            }
        }

        // 今日一菜
        item {
            SectionTitle(
                text = "今日一菜",
                more = "看做法 ›",
                onMore = { onOpenRecipe(now.index, dishIndex) }
            )
        }
        item {
            PaperCard(modifier = Modifier.clickable { onOpenRecipe(now.index, dishIndex) }) {
                Row(modifier = Modifier.padding(13.dp)) {
                    Box(
                        modifier = Modifier
                            .size(74.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(skin.gradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            dish.name.take(1),
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                    }
                    Spacer(Modifier.width(13.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(dish.name, style = MaterialTheme.typography.titleMedium, color = Ink)
                        Spacer(Modifier.height(5.dp))
                        Text(
                            dish.sub,
                            style = MaterialTheme.typography.bodySmall,
                            color = Ink3,
                            maxLines = 2
                        )
                        Spacer(Modifier.height(9.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            MetaChip(dish.time)
                            MetaChip(dish.level)
                            MetaChip("${dish.ingredients.size} 味料")
                        }
                    }
                }
            }
        }

        // 本季食养
        item {
            PaperCard(
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Column(Modifier.padding(15.dp)) {
                    Text(
                        "${term.season}季食养 · ${term.seasonEn}",
                        style = MaterialTheme.typography.titleSmall,
                        color = skin.accent
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(term.living, style = MaterialTheme.typography.bodySmall, color = Ink2)
                }
            }
        }

        item {
            FootNote("节气日期按天文算法推算，个别年份或有一日之差\n本内容为传统食养参考，不替代医疗建议")
        }
    }
}

private fun pad2(v: Int): String = if (v < 10) "0$v" else "$v"

private fun organOf(season: String): String = when (season) {
    "春" -> "肝"
    "夏" -> "心"
    "秋" -> "肺"
    else -> "肾"
}

@Composable
private fun MetaChip(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = Ink3,
        modifier = Modifier
            .clip(RoundedCornerShape(3.dp))
            .border(1.dp, Line, RoundedCornerShape(3.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    )
}

@Composable
private fun FoodChip(food: Food, accent: Color, onClick: () -> Unit) {
    PaperCard(
        modifier = Modifier
            .width(104.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(PaperDeep)
                    .border(1.dp, LineSoft, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(food.glyph, style = MaterialTheme.typography.titleLarge, color = accent)
            }
            Spacer(Modifier.height(9.dp))
            Text(food.name, style = MaterialTheme.typography.titleSmall, color = Ink)
            Spacer(Modifier.height(3.dp))
            Text(food.nature, style = MaterialTheme.typography.labelSmall, color = Ink3)
        }
    }
}
