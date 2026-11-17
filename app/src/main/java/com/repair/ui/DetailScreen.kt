package com.repair.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.repair.data.Food
import com.repair.data.RecipeRef
import com.repair.data.SolarCalendar
import com.repair.data.TERMS
import com.repair.data.Term
import com.repair.data.gist
import com.repair.data.groupedFoods
import com.repair.data.health
import com.repair.data.nutriOf
import com.repair.data.peakPurine
import com.repair.data.principleTitles
import com.repair.data.purineAdvice
import com.repair.data.purineLevel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetailScreen(
    index: Int,
    favs: Set<String>,
    onBack: () -> Unit,
    onOpenTerm: (Int) -> Unit,
    onOpenRecipe: (RecipeRef) -> Unit,
    onToggleFav: (String) -> Unit
) {
    val term = TERMS[index]
    val skin = skinOf(term.season)
    val range = remember(index) { SolarCalendar.rangeOf(index) }
    val nowDayOfTerm = remember(index) { SolarCalendar.now().let { if (it.index == index) it.passedDays else -1 } }
    var showPicker by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    // 头部（item 0）滚出去之后，切换条才浮出来
    val showSwitchBar by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 140
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Paper)
            .navigationBarsPadding()
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
        // 渐变头
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(skin.gradient)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 46.dp, end = 22.dp)
                        .size(56.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                )
                Column(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 26.dp)
                ) {
                    Text(
                        "‹　返回",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier
                            .clickable { onBack() }
                            .padding(vertical = 6.dp, horizontal = 2.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        term.name,
                        style = MaterialTheme.typography.displaySmall,
                        color = Color.White
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "${term.py}　·　第 ${index + 1} 个节气 · ${term.phrase}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.55f),
                        letterSpacing = 2.sp
                    )
                    Spacer(Modifier.height(10.dp))
                    // 起止日期与持续天数
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "${range.label} · ${range.daysLabel}",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier
                                .border(1.dp, Color.White.copy(alpha = 0.28f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                        if (nowDayOfTerm >= 0) {
                            Text(
                                "今日第 ${nowDayOfTerm + 1} 天",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        term.keys.forEach { OutlineTag(it, Color.White.copy(alpha = 0.85f)) }
                    }
                    Spacer(Modifier.height(14.dp))
                    Text(
                        term.hou,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.78f),
                        letterSpacing = 1.2.sp
                    )
                }
            }
        }

        // 主体
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {

                // 一、本节食养：先讲清「为什么这么吃」，再谈具体吃什么
                DTitle("本节食养", "WHY THIS SEASON", skin.accent)
                GistCard(
                    text = term.gist,
                    keys = term.focus,
                    accent = skin.accent,
                    soft = skin.soft,
                    deep = skin.deep
                )

                // 二、饮食原则
                DTitle("饮食原则", "DIETARY PRINCIPLE", skin.accent)

                // 气候与身体
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(skin.soft.copy(alpha = 0.55f))
                        .padding(horizontal = 12.dp, vertical = 11.dp)
                ) {
                    Text(
                        "气候",
                        style = MaterialTheme.typography.labelSmall,
                        color = skin.deep,
                        modifier = Modifier.padding(top = 2.dp, end = 9.dp)
                    )
                    Text(
                        term.climate,
                        style = MaterialTheme.typography.bodySmall,
                        color = Ink2,
                        textAlign = TextAlign.Justify
                    )
                }

                Spacer(Modifier.height(12.dp))
                val titles = term.principleTitles
                term.principle.forEachIndexed { i, para ->
                    val t = titles.getOrNull(i) ?: ""
                    if (t.isEmpty()) {
                        // 没有小标题时退回原来的编号段落
                        Row(modifier = Modifier.padding(bottom = 10.dp)) {
                            Text(
                                "0${i + 1}",
                                fontFamily = Serif,
                                fontSize = 10.sp,
                                color = skin.accent.copy(alpha = 0.7f),
                                modifier = Modifier.padding(top = 3.dp, end = 9.dp)
                            )
                            Text(
                                para,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Ink2,
                                textAlign = TextAlign.Justify
                            )
                        }
                    } else {
                        PrincipleCard(index = i, title = t, text = para, accent = skin.accent)
                    }
                }

                Spacer(Modifier.height(12.dp))
                QuoteBlock(term.quote, skin.accent)

                // 三、宜忌
                DTitle("宜与忌", "DO & DON'T", skin.accent)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    YijiBox(isYi = true, items = term.yi, modifier = Modifier.weight(1f))
                    YijiBox(isYi = false, items = term.ji, modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(8.dp))
                SectionHint("「宜」是这一节最该常吃的几样，「忌」是这段时间要少碰的做法与状态，不是绝对禁食。")

                // 四、时令食材
                DTitle("时令食材", "SEASONAL FOODS", skin.accent)
                SectionHint("每味都写了功效、适合谁吃、谁要慎食。挑菜时先看「适合」，再对一眼「慎食」。")
                term.groupedFoods().forEach { (cat, list) ->
                    Row(
                        modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(cat, style = MaterialTheme.typography.labelMedium, color = skin.deep)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "${list.size} 味",
                            style = MaterialTheme.typography.labelSmall,
                            color = Ink3
                        )
                        Spacer(Modifier.width(10.dp))
                        Box(
                            Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(LineSoft)
                        )
                    }
                    list.forEach { food ->
                        FoodRow(food, skin.accent)
                        Spacer(Modifier.height(7.dp))
                    }
                }

                // 五、节气食谱
                DTitle("节气食谱", "RECIPES", skin.accent)
                SectionHint("每道都是家常做法，成本低、步骤少。卡片里标了「为什么这一节吃它」和「谁适合、谁要少吃」。")
                term.recipes.forEachIndexed { j, _ ->
                    val ref = RecipeRef(index, j)
                    DetailRecipeBlock(
                        ref = ref,
                        isFav = favs.contains(ref.id),
                        accent = skin.accent,
                        onToggleFav = { onToggleFav(ref.id) }
                    )
                    Spacer(Modifier.height(10.dp))
                }

                DTitle("起居调摄", "DAILY CARE", skin.accent)
                Text(
                    term.living,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Ink2,
                    textAlign = TextAlign.Justify
                )

                DTitle("此时当忌", "AVOID", skin.accent)
                term.avoid.forEach { AvoidItem(it) }

                // 上下节气
                Spacer(Modifier.height(30.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Line)
                )
                Spacer(Modifier.height(18.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    val prev = (index + 23) % 24
                    val next = (index + 1) % 24
                    TermNavCard("‹ 上一节气", TERMS[prev].name, Modifier.weight(1f)) { onOpenTerm(prev) }
                    TermNavCard("下一节气 ›", TERMS[next].name, Modifier.weight(1f)) { onOpenTerm(next) }
                }
                FootNote("以上为传统食养经验，体质特殊者请遵医嘱")
            }
        }
        }

        // 头部滚出视野后，节气切换条常驻顶部，随时可跳到上一 / 下一节气
        AnimatedVisibility(
            visible = showSwitchBar,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            val prevIndex = (index + 23) % 24
            val nextIndex = (index + 1) % 24
            TermSwitchBar(
                current = term.name,
                prevName = TERMS[prevIndex].name,
                nextName = TERMS[nextIndex].name,
                accent = skin.accent,
                onBack = onBack,
                onPrev = { onOpenTerm(prevIndex) },
                onNext = { onOpenTerm(nextIndex) },
                onPick = { showPicker = true }
            )
        }
    }

    if (showPicker) {
        TermPicker(
            currentIndex = index,
            accent = skin.accent,
            onPick = {
                showPicker = false
                if (it != index) onOpenTerm(it)
            },
            onDismiss = { showPicker = false }
        )
    }
}

@Composable
private fun DTitle(text: String, en: String, accent: Color) {
    Row(
        modifier = Modifier.padding(top = 26.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .width(3.dp)
                .height(12.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(accent)
        )
        Spacer(Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.titleMedium, color = Ink)
        Spacer(Modifier.width(8.dp))
        Text(en, style = MaterialTheme.typography.labelSmall, color = Ink3, letterSpacing = 1.4.sp)
    }
}

@Composable
private fun QuoteBlock(quote: String, accent: Color) {
    val parts = remember(quote) { quote.split("　——") }
    Row(modifier = Modifier.fillMaxWidth()) {
        Box(
            Modifier
                .width(2.dp)
                .heightIn(min = 40.dp)
                .background(accent.copy(alpha = 0.55f))
        )
        Spacer(Modifier.width(13.dp))
        Column {
            Text(
                parts[0],
                fontFamily = Serif,
                fontSize = 12.5.sp,
                lineHeight = 24.sp,
                letterSpacing = 1.sp,
                color = Ink2
            )
            if (parts.size > 1) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "——${parts[1]}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Ink3,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * 单味食材卡。
 * 结构：名称 + 性味 → 功效 → 适合谁 / 谁要慎食。
 * 「适合」「慎食」用方签标出来，比单写「宜」「忌」好认。
 */
@Composable
private fun FoodRow(food: Food, accent: Color) {
    PaperCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(11.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 1.dp)
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(PaperDeep)
                    .border(1.dp, LineSoft, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(food.glyph, fontFamily = Serif, fontSize = 15.sp, color = accent)
            }
            Spacer(Modifier.width(11.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(food.name, style = MaterialTheme.typography.titleSmall, color = Ink)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        food.nature,
                        style = MaterialTheme.typography.labelSmall,
                        color = accent,
                        modifier = Modifier
                            .border(1.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(2.dp))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
                Spacer(Modifier.height(5.dp))
                // 功效：稍深一档的字色，和下面的宜忌区分开
                Text(
                    food.effect,
                    style = MaterialTheme.typography.bodySmall,
                    color = Ink2,
                    lineHeight = 18.sp
                )
                NutriLine(food.name, accent)
                if (food.fit.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    PointLine(
                        mark = "适合",
                        head = food.fit,
                        body = "",
                        color = accent
                    )
                }
                if (food.avoid.isNotEmpty()) {
                    Spacer(Modifier.height(6.dp))
                    PointLine(
                        mark = "慎食",
                        head = food.avoid,
                        body = "",
                        color = Cinnabar.copy(alpha = 0.88f)
                    )
                }
            }
        }
    }
}

/**
 * 食材的可查证数据条。
 * 数值全部来自国家卫健委食养指南，官方表未收录的食材不显示（不估算、不补值）。
 */
/** 配料里出现这些词，就说明这道菜有隐性盐 */
private val SALTY = listOf(
    "酱油", "生抽", "老抽", "豆瓣酱", "豆豉", "腐乳", "盐",
    "腊肉", "咸肉", "榨菜", "火腿", "虾皮", "咸菜", "酱"
)

/**
 * 控盐换算。数字取自《成人高血压食养指南（2023 年版）》表 2.7：
 * 2000mg 钠 = 5g 盐，对等于酱油 32g / 豆瓣酱 30g / 腐乳 84g。
 */
@Composable
private fun SaltHint() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Cinnabar.copy(alpha = 0.05f))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(
            "控盐 · 这道菜有咸味调料",
            style = MaterialTheme.typography.titleSmall,
            color = Cinnabar.copy(alpha = 0.9f)
        )
        Spacer(Modifier.height(5.dp))
        Text(
            "《成人高血压食养指南》的标准是每人每天食盐不超过 5g。做菜时这些都要算进总量：\n" +
                "一勺生抽（约 12g）≈ 1.9g 盐，占一天上限的 38%\n" +
                "一勺豆瓣酱（约 15g）≈ 2.5g 盐　一块腐乳（约 12g）≈ 0.7g 盐\n" +
                "我国居民 75% 以上的钠，就来自家里的盐罐和高盐调味品。",
            style = MaterialTheme.typography.labelSmall,
            color = Ink2,
            lineHeight = 17.sp
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun NutriLine(name: String, accent: Color) {
    val n = nutriOf(name) ?: return
    val chips = mutableListOf<Pair<String, Boolean>>()
    n.purine?.let { mg ->
        val lv = purineLevel(mg)
        chips += "嘌呤 $mg mg/100g · $lv" to (lv == "低" || lv == "较低")
    }
    if (n.gi.isNotEmpty()) {
        chips += "升糖 ${n.gi}" to (n.gi == "低")
    }
    if (chips.isEmpty() && n.extra.isEmpty()) return

    Column(modifier = Modifier.padding(top = 7.dp)) {
        if (chips.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                chips.forEach { pair ->
                    val tone = if (pair.second) Jade else Cinnabar.copy(alpha = 0.82f)
                    Text(
                        pair.first,
                        style = MaterialTheme.typography.labelSmall,
                        color = tone,
                        modifier = Modifier
                            .clip(RoundedCornerShape(2.dp))
                            .background(tone.copy(alpha = 0.09f))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
            }
        }
        val note = buildString {
            n.purine?.let { append(purineAdvice(it)) }
            if (n.extra.isNotEmpty()) {
                if (isNotEmpty()) append("；")
                append(n.extra)
            }
        }
        if (note.isNotEmpty()) {
            Spacer(Modifier.height(5.dp))
            Text(
                note,
                style = MaterialTheme.typography.labelSmall,
                color = Ink3,
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
private fun AvoidItem(text: String) {
    Row(
        modifier = Modifier.padding(bottom = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            Modifier
                .padding(top = 7.dp)
                .size(5.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(Cinnabar.copy(alpha = 0.8f))
        )
        Spacer(Modifier.width(10.dp))
        Text(text, style = MaterialTheme.typography.bodySmall, color = Ink2)
    }
}

/** 吸顶节气切换条：滚到任何位置都能切上一 / 下一节气，点中间可展开全部节气 */
@Composable
private fun TermSwitchBar(
    current: String,
    prevName: String,
    nextName: String,
    accent: Color,
    onBack: () -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onPick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Paper)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Text("←", fontSize = 15.sp, color = Ink2)
            }
            Text(
                "‹ $prevName",
                style = MaterialTheme.typography.labelMedium,
                color = Ink3,
                maxLines = 1,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onPrev() }
                    .padding(vertical = 9.dp, horizontal = 3.dp)
            )
            Text(
                "$current ▾",
                style = MaterialTheme.typography.titleSmall,
                color = accent,
                maxLines = 1,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onPick() }
                    .padding(horizontal = 9.dp, vertical = 7.dp)
            )
            Text(
                "$nextName ›",
                style = MaterialTheme.typography.labelMedium,
                color = Ink3,
                maxLines = 1,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onNext() }
                    .padding(vertical = 9.dp, horizontal = 3.dp)
            )
        }
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Line)
        )
    }
}

/** 全部节气快选面板 */
@Composable
fun TermPicker(
    currentIndex: Int,
    accent: Color,
    onPick: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Paper)
                .padding(horizontal = 14.dp, vertical = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("跳到节气", style = MaterialTheme.typography.titleMedium, color = Ink)
                Spacer(Modifier.weight(1f))
                Text(
                    "收起",
                    style = MaterialTheme.typography.labelSmall,
                    color = Ink3,
                    modifier = Modifier
                        .clickable { onDismiss() }
                        .padding(vertical = 4.dp, horizontal = 6.dp)
                )
            }
            Spacer(Modifier.height(12.dp))
            for (row in 0 until 4) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (col in 0 until 6) {
                        val i = row * 6 + col
                        val on = i == currentIndex
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (on) accent else PaperDeep)
                                .clickable { onPick(i) }
                                .padding(vertical = 9.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                TERMS[i].name,
                                fontSize = 11.5.sp,
                                color = if (on) Color.White else Ink2,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TermNavCard(hint: String, name: String, modifier: Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(PaperCard)
            .border(1.dp, Line, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(hint, style = MaterialTheme.typography.labelSmall, color = Ink3)
        Spacer(Modifier.height(5.dp))
        Text(name, style = MaterialTheme.typography.titleMedium, color = Ink)
    }
}

/**
 * 详情页里完整展开的一道菜。
 * 顺序：菜名 → 一句话介绍 → 时间难度 → 为什么这一节吃它 →
 * 适合谁 → 谁要少吃 → 食材 → 步骤 → 做法要点。
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetailRecipeBlock(
    ref: RecipeRef,
    isFav: Boolean,
    accent: Color,
    onToggleFav: () -> Unit
) {
    val r = ref.recipe
    val h = ref.health
    PaperCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(15.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    r.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Ink,
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 2.dp)
                )
                FavStar(isFav = isFav, onClick = onToggleFav, size = 30)
            }
            Spacer(Modifier.height(6.dp))
            Text(r.sub, style = MaterialTheme.typography.labelSmall, color = Ink3)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("⏱ ${r.time}", style = MaterialTheme.typography.labelSmall, color = Ink3)
                Text("难度 ${r.level}", style = MaterialTheme.typography.labelSmall, color = Ink3)
                Text("${r.ingredients.size} 味料", style = MaterialTheme.typography.labelSmall, color = Ink3)
            }

            Spacer(Modifier.height(12.dp))
            WhyBlock(h.why, accent)

            Spacer(Modifier.height(11.dp))
            PointLine(mark = "适合", head = h.fitHead, body = h.fitBody, color = Jade)
            Spacer(Modifier.height(7.dp))
            PointLine(
                mark = "慎食",
                head = h.avoidHead,
                body = h.avoidBody,
                color = Cinnabar.copy(alpha = 0.88f)
            )

            Spacer(Modifier.height(12.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(LineSoft)
            )
            Spacer(Modifier.height(14.dp))
            // 食材
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                r.ingredients.forEach {
                    Text(
                        it,
                        style = MaterialTheme.typography.labelSmall,
                        color = Ink2,
                        modifier = Modifier
                            .clip(RoundedCornerShape(3.dp))
                            .background(PaperDeep)
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }
            }
            Spacer(Modifier.height(9.dp))
            // 步骤
            r.steps.forEachIndexed { i, step ->
                Row(
                    modifier = Modifier.padding(bottom = 7.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(accent.copy(alpha = 0.85f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${i + 1}", fontSize = 9.sp, color = Color.White)
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(step, style = MaterialTheme.typography.bodySmall, color = Ink2)
                }
            }
            Spacer(Modifier.height(5.dp))
            Text(
                "做法要点 · ${r.tip}",
                style = MaterialTheme.typography.labelSmall,
                color = Jade,
                lineHeight = 17.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(Jade.copy(alpha = 0.055f))
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            )
        }
    }
}

/* ================= 食谱底部弹层 ================= */

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecipeSheet(
    ref: RecipeRef,
    favs: Set<String>,
    onDismiss: () -> Unit,
    onToggleFav: (String) -> Unit,
    onOpenTerm: (Int) -> Unit
) {
    val config = LocalConfiguration.current
    val maxH = (config.screenHeightDp * 0.86f).dp
    val r = ref.recipe
    val term = ref.term
    val h = ref.health
    val skin = skinOf(term.season)
    val isFav = favs.contains(ref.id)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x571E1A14))
            .clickable(
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                indication = null
            ) { onDismiss() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = maxH)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(Paper)
                .pointerInput(Unit) { detectTapGestures { } }
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Box(
                Modifier
                    .padding(top = 10.dp, bottom = 4.dp)
                    .align(Alignment.CenterHorizontally)
                    .size(width = 34.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Line)
            )
            Spacer(Modifier.height(12.dp))
            Text(r.name, style = MaterialTheme.typography.headlineSmall, color = Ink, fontSize = 22.sp)
            Spacer(Modifier.height(6.dp))
            Text(
                "${r.sub}　·　${term.name}食单",
                style = MaterialTheme.typography.labelSmall,
                color = Ink3
            )
            Spacer(Modifier.height(16.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Line)
            )
            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("⏱ ${r.time}", style = MaterialTheme.typography.labelMedium, color = Ink3)
                Text("难度 ${r.level}", style = MaterialTheme.typography.labelMedium, color = Ink3)
                Text("${r.ingredients.size} 味料", style = MaterialTheme.typography.labelMedium, color = Ink3)
            }
            Spacer(Modifier.height(14.dp))

            WhyBlock(h.why, skin.accent, label = "为什么${term.name}吃它")
            Spacer(Modifier.height(14.dp))

            PointLine(mark = "适合", head = h.fitHead, body = h.fitBody, color = Jade)
            Spacer(Modifier.height(8.dp))
            PointLine(
                mark = "慎食",
                head = h.avoidHead,
                body = h.avoidBody,
                color = Cinnabar.copy(alpha = 0.88f)
            )
            Spacer(Modifier.height(16.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                r.ingredients.forEach {
                    Text(
                        it,
                        style = MaterialTheme.typography.labelSmall,
                        color = Ink2,
                        modifier = Modifier
                            .clip(RoundedCornerShape(3.dp))
                            .background(PaperDeep)
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }
            }

            // 配料里嘌呤最高的一味，直接给出处数据
            val peak = peakPurine(r.ingredients)
            if (peak != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "最重的一味 · ${peak.first}　嘌呤 ${peak.second} mg/100g（${purineLevel(peak.second)}）· ${purineAdvice(peak.second)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (peak.second >= 75) Cinnabar.copy(alpha = 0.85f) else Jade,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            (if (peak.second >= 75) Cinnabar else Jade).copy(alpha = 0.05f)
                        )
                        .padding(horizontal = 10.dp, vertical = 7.dp)
                )
            }
            Spacer(Modifier.height(10.dp))

            r.steps.forEachIndexed { i, step ->
                Row(
                    modifier = Modifier.padding(bottom = 9.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 3.dp)
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(skin.accent.copy(alpha = 0.9f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${i + 1}", fontSize = 9.sp, color = Color.White)
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(step, style = MaterialTheme.typography.bodyMedium, color = Ink2)
                }
            }

            Text(
                "做法要点 · ${r.tip}",
                style = MaterialTheme.typography.bodySmall,
                color = Jade,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Jade.copy(alpha = 0.055f))
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            )

            // 配料里带咸味调料的，才提示控盐
            if (r.ingredients.any { ing -> SALTY.any { ing.contains(it) } }) {
                Spacer(Modifier.height(10.dp))
                SaltHint()
            }
            Spacer(Modifier.height(18.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SheetButton(
                    text = if (isFav) "已收藏 · 点击取消" else "收入我的食单",
                    modifier = Modifier.weight(1f)
                ) { onToggleFav(ref.id) }
                SheetButton(
                    text = "看${term.name}全篇",
                    modifier = Modifier.weight(1f)
                ) { onOpenTerm(ref.termIndex) }
            }
            Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
private fun SheetButton(text: String, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(PaperCard)
            .border(1.dp, Line, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge, color = Jade)
    }
}
