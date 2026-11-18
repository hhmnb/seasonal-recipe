package com.repair.ui

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.repair.data.Food
import com.repair.data.RecipeRef
import com.repair.data.TERMS
import com.repair.data.Term

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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Paper)
            .navigationBarsPadding()
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
                        "${term.py}　·　第 ${index + 1} 个节气",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.55f),
                        letterSpacing = 2.sp
                    )
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
                DTitle("饮食原则", "DIETARY PRINCIPLE", skin.accent)
                Text(
                    term.principle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Ink2,
                    textAlign = TextAlign.Justify
                )

                Spacer(Modifier.height(22.dp))
                QuoteBlock(term.quote, skin.accent)

                DTitle("宜与忌", "DO & DON'T", skin.accent)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    YijiBox(isYi = true, items = term.yi, modifier = Modifier.weight(1f))
                    YijiBox(isYi = false, items = term.ji, modifier = Modifier.weight(1f))
                }

                DTitle("时令食材", "SEASONAL FOODS", skin.accent)
                term.foods.forEach { food ->
                    FoodRow(food, skin.accent)
                    Spacer(Modifier.height(8.dp))
                }

                DTitle("节气食谱", "RECIPES", skin.accent)
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

@Composable
private fun FoodRow(food: Food, accent: Color) {
    PaperCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(11.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
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
                Spacer(Modifier.height(4.dp))
                Text(food.effect, style = MaterialTheme.typography.labelSmall, color = Ink3)
            }
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

/** 详情页里完整展开的食谱块 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetailRecipeBlock(
    ref: RecipeRef,
    isFav: Boolean,
    accent: Color,
    onToggleFav: () -> Unit
) {
    val r = ref.recipe
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
            }
            Spacer(Modifier.height(12.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(LineSoft)
            )
            Spacer(Modifier.height(18.dp))
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
            Spacer(Modifier.height(8.dp))
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
            Spacer(Modifier.height(4.dp))
            Text(
                "宜忌提示 · ${r.tip}",
                style = MaterialTheme.typography.labelSmall,
                color = Jade,
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
            Spacer(Modifier.height(8.dp))

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
                "宜忌提示 · ${r.tip}",
                style = MaterialTheme.typography.bodySmall,
                color = Jade,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Jade.copy(alpha = 0.055f))
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            )
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
