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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.repair.data.ALL_RECIPES
import com.repair.data.DATA_SOURCES
import com.repair.data.FONT_HINTS
import com.repair.data.FONT_LABELS
import com.repair.data.Member
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
    members: List<Member>,
    fontStep: Int,
    onFontStep: (Int) -> Unit,
    onConstitution: (String) -> Unit,
    onOpenFamily: () -> Unit,
    onOpen: (RecipeRef) -> Unit,
    onToggleFav: (String) -> Unit
) {
    val favList = remember(favs) { ALL_RECIPES.filter { favs.contains(it.id) } }
    val term = TERMS[now.index]
    val cal = remember { Calendar.getInstance() }
    val dayOfYear = remember { cal.get(Calendar.DAY_OF_YEAR) }
    var showSources by remember { mutableStateOf(false) }

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

        // 家人档案
        item {
            SectionTitle(text = "家人档案")
        }
        item {
            PaperCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .clickable { onOpenFamily() }
                        .padding(15.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "按家人的身体状况挑菜",
                                style = MaterialTheme.typography.titleMedium,
                                color = Ink
                            )
                            Spacer(Modifier.height(5.dp))
                            Text(
                                if (members.isEmpty()) {
                                    "填上慢病与近期症状，按当前节气推荐合适的家常菜"
                                } else {
                                    "已添加 ${members.size} 位：" + members.joinToString("、") { it.name }
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = Ink3,
                                lineHeight = 17.sp
                            )
                        }
                        Text("›", style = MaterialTheme.typography.titleMedium, color = Ink3)
                    }
                    if (members.isNotEmpty()) {
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            members.take(5).forEach { m ->
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Jade.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        m.name.take(1),
                                        fontFamily = Serif,
                                        fontSize = 14.sp,
                                        color = Jade
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(13.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Jade.copy(alpha = 0.07f))
                            .padding(vertical = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            if (members.isEmpty()) "去添加家人　›" else "查看与管理　›",
                            style = MaterialTheme.typography.labelLarge,
                            color = Jade
                        )
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

        // 显示字号
        item {
            SectionTitle(text = "显示字号")
        }
        item {
            Column {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FONT_LABELS.forEachIndexed { i, label ->
                        ConsChip(
                            label = label,
                            selected = fontStep == i,
                            modifier = Modifier.weight(1f)
                        ) { onFontStep(i) }
                    }
                }
                Spacer(Modifier.height(9.dp))
                PaperCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(13.dp)) {
                        Text(
                            "预览",
                            style = MaterialTheme.typography.labelSmall,
                            color = Ink3,
                            letterSpacing = 1.2.sp
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "小寒宜食羊肉、山药、白菜。温阳散寒，配萝卜同炖则不滞。",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Ink2,
                            lineHeight = 24.sp
                        )
                        Spacer(Modifier.height(7.dp))
                        Text(
                            FONT_HINTS[fontStep.coerceIn(0, FONT_HINTS.size - 1)],
                            style = MaterialTheme.typography.labelSmall,
                            color = Ink3
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    "调大后全应用的正文、食谱步骤、按钮文字会一起变大，不用逐页设置。",
                    style = MaterialTheme.typography.labelSmall,
                    color = Ink3,
                    lineHeight = 16.sp
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
            InfoRow(
                "历", "节气日期",
                "按 1900—2100 年节气表查得，非公式推算；已与紫金山天文台历书核对"
            )
            Spacer(Modifier.height(9.dp))
            InfoRow(
                "数", "营养数据来源",
                "嘌呤、升糖、钠盐三项取自国家卫健委食养指南　›　点开看每条出处",
                onClick = { showSources = true }
            )
            Spacer(Modifier.height(9.dp))
            InfoRow("养", "性味与功效", "传统食养典籍的通行说法，各家有出入，非实验数据")
            Spacer(Modifier.height(9.dp))
            InfoRow("慎", "免责说明", "本应用仅供食养参考，不能替代医疗诊断与用药")
            FootNote("节气食养 · 顺天时，食有节")
        }
    }

    if (showSources) {
        DataSourceDialog(onClose = { showSources = false })
    }
}

@Composable
private fun DataSourceDialog(onClose: () -> Unit) {
    Dialog(onDismissRequest = onClose) {
        PaperCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    "数据出处",
                    fontFamily = Serif,
                    fontSize = 17.sp,
                    color = Ink
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "本应用的营养数值不做估算。官方表里没有收录的食材，界面上就不显示数字。",
                    style = MaterialTheme.typography.labelSmall,
                    color = Ink3,
                    lineHeight = 16.sp
                )
                Spacer(Modifier.height(14.dp))
                DATA_SOURCES.forEachIndexed { i, pair ->
                    if (i > 0) Spacer(Modifier.height(11.dp))
                    Text(
                        "· ${pair.first}",
                        style = MaterialTheme.typography.titleSmall,
                        color = Jade
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        pair.second,
                        style = MaterialTheme.typography.labelSmall,
                        color = Ink2,
                        lineHeight = 17.sp
                    )
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    "知道了",
                    style = MaterialTheme.typography.titleSmall,
                    color = Jade,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Jade.copy(alpha = 0.08f))
                        .clickable { onClose() }
                        .padding(vertical = 10.dp)
                )
            }
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
private fun InfoRow(
    glyph: String,
    title: String,
    desc: String,
    onClick: (() -> Unit)? = null
) {
    PaperCard(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
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
