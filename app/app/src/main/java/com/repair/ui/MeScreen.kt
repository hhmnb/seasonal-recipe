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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.repair.data.ALL_RECIPES
import com.repair.data.NowTerm
import com.repair.data.RecipeRef
import com.repair.data.TERMS
import java.util.Calendar

private val CONSTITUTIONS = listOf("平和", "阳虚", "阴虚", "痰湿", "湿热", "气郁")

private val CONS_TIP = mapOf(
    "平和" to "平和质 · 阴阳气血调和，按节气常规食养即可，无需刻意偏补。",
    "阳虚" to "阳虚质 · 畏寒肢冷，宜温补。多用羊肉、生姜、桂圆，忌生冷寒凉。",
    "阴虚" to "阴虚质 · 口燥咽干、手足心热，宜清补。多用银耳、百合、梨，忌辛辣温燥。",
    "痰湿" to "痰湿质 · 形体偏胖、身重困倦，宜健脾化湿。多用薏米、赤小豆、冬瓜，忌甜腻。",
    "湿热" to "湿热质 · 面垢油光、易生疮，宜清热利湿。多用绿豆、苦瓜、冬瓜，忌辛辣酒酪。",
    "气郁" to "气郁质 · 情绪低落、胸胁胀满，宜疏肝理气。多用佛手、玫瑰花、柑橘，忌久坐。"
)

@Composable
fun MeScreen(
    now: NowTerm,
    favs: Set<String>,
    constitution: String,
    onConstitution: (String) -> Unit,
    onOpen: (RecipeRef) -> Unit,
    onToggleFav: (String) -> Unit
) {
    val favList = remember(favs) { ALL_RECIPES.filter { favs.contains(it.id) } }
    val term = TERMS[now.index]
    val cal = remember { Calendar.getInstance() }
    val dayOfYear = remember { cal.get(Calendar.DAY_OF_YEAR) }

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
                    .padding(top = 10.dp, bottom = 14.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text("我的", style = MaterialTheme.typography.headlineSmall, color = Ink, fontSize = 21.sp)
                    Spacer(Modifier.height(3.dp))
                    Text(
                        "MY CUISINE",
                        style = MaterialTheme.typography.labelSmall,
                        color = Ink3,
                        letterSpacing = 2.4.sp
                    )
                }
                Spacer(Modifier.weight(1f))
                Column(horizontalAlignment = Alignment.End) {
                    Text("${cal.get(Calendar.YEAR)} 年", style = MaterialTheme.typography.labelSmall, color = Ink3)
                    Spacer(Modifier.height(3.dp))
                    Text(
                        "${term.season}·${term.name}",
                        style = MaterialTheme.typography.labelMedium,
                        color = Jade,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // 资料卡
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF2E5A4C), Color(0xFF24564B), Color(0xFF1E4A44))
                        )
                    )
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 8.dp, bottom = 6.dp)
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.12f), CircleShape)
                )
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "时令食客",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontSize = 19.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "顺时而食 · 依节气养生",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.62f),
                        letterSpacing = 1.4.sp
                    )
                    Spacer(Modifier.height(20.dp))
                    Row {
                        StatItem("收藏食谱", "${favs.size}")
                        Spacer(Modifier.width(26.dp))
                        StatItem("已历节气", "${now.index + 1}")
                        Spacer(Modifier.width(26.dp))
                        StatItem("食养天数", "$dayOfYear")
                    }
                }
            }
        }

        // 体质
        item {
            SectionTitle(text = "我的体质")
        }
        item {
            Column {
                CONSTITUTIONS.chunked(3).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        row.forEach { c ->
                            ConsChip(
                                label = "${c}质",
                                selected = constitution == c,
                                modifier = Modifier.weight(1f)
                            ) { onConstitution(c) }
                        }
                        if (row.size < 3) {
                            repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
                Spacer(Modifier.height(3.dp))
                Text(
                    text = CONS_TIP[constitution] ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = Ink3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Jade.copy(alpha = 0.045f))
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                )
            }
        }

        // 收藏
        item {
            SectionTitle(text = "收藏的食谱", more = "${favList.size} 道")
        }
        if (favList.isEmpty()) {
            item {
                EmptyState(glyph = "书", title = "尚未收藏食谱", hint = "在食单页点星标即可收入")
            }
        } else {
            favList.forEach { ref ->
                item(key = "fav_${ref.id}") {
                    RecipeRow(
                        ref = ref,
                        isFav = true,
                        onClick = { onOpen(ref) },
                        onToggleFav = { onToggleFav(ref.id) }
                    )
                    Spacer(Modifier.height(9.dp))
                }
            }
        }

        // 关于
        item {
            SectionTitle(text = "关于")
            InfoRow("历", "节气日期", "按天文算法推算，个别年份或有一日之差")
            Spacer(Modifier.height(9.dp))
            InfoRow("养", "内容依据", "《黄帝内经》《金匮要略》等传统食养典籍")
            Spacer(Modifier.height(9.dp))
            InfoRow("慎", "免责说明", "本应用仅供食养参考，不能替代医疗诊断")
            FootNote("节气食养 · 顺天时，食有节")
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.6f),
            letterSpacing = 1.4.sp
        )
        Spacer(Modifier.height(4.dp))
        Text(value, fontFamily = Serif, fontSize = 22.sp, color = Color.White)
    }
}

@Composable
private fun ConsChip(label: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) Ink else PaperCard)
            .border(1.dp, if (selected) Ink else Line, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) Paper else Ink2,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun InfoRow(glyph: String, title: String, desc: String) {
    PaperCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Jade.copy(alpha = 0.07f)),
                contentAlignment = Alignment.Center
            ) {
                Text(glyph, fontFamily = Serif, fontSize = 14.sp, color = Jade)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall, color = Ink)
                Spacer(Modifier.height(3.dp))
                Text(desc, style = MaterialTheme.typography.labelSmall, color = Ink3)
            }
        }
    }
}
