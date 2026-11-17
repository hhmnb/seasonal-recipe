package com.repair.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.repair.data.Member
import com.repair.data.RecipeRef
import com.repair.data.Suggestion
import com.repair.data.TERMS
import com.repair.data.health
import com.repair.data.plainTag
import com.repair.data.suggestFor
import com.repair.data.wantedTags

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecommendScreen(
    member: Member,
    termIndex: Int,
    favs: Set<String>,
    onBack: () -> Unit,
    onOpenRecipe: (RecipeRef) -> Unit,
    onToggleFav: (String) -> Unit,
    onEditMember: () -> Unit
) {
    var pickTerm by remember { mutableStateOf(termIndex) }
    var showPicker by remember { mutableStateOf(false) }
    val term = TERMS[pickTerm]
    val list = remember(member, pickTerm) { suggestFor(member, pickTerm) }
    val here = list.filter { it.inCurrentTerm }
    val rest = list.filter { !it.inCurrentTerm }
    val want = remember(member) { wantedTags(member) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Paper)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, bottom = 30.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "‹　返回",
                    style = MaterialTheme.typography.labelLarge,
                    color = Ink3,
                    modifier = Modifier
                        .clickable { onBack() }
                        .padding(vertical = 8.dp, horizontal = 2.dp)
                )
                Spacer(Modifier.weight(1f))
                Text(
                    "改条件",
                    style = MaterialTheme.typography.labelSmall,
                    color = Ink3,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { onEditMember() }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "为 ${member.name} 挑的菜",
                style = MaterialTheme.typography.headlineSmall,
                color = Ink,
                fontSize = 22.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "${term.name} ▾　·　共 ${list.size} 道　·　${member.age}",
                style = MaterialTheme.typography.labelSmall,
                color = Ink3,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { showPicker = true }
                    .padding(vertical = 3.dp, horizontal = 2.dp)
            )

            Spacer(Modifier.height(13.dp))
            if (member.conditions.isEmpty() && member.symptoms.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(9.dp))
                        .background(PaperDeep)
                        .padding(horizontal = 12.dp, vertical = 11.dp)
                ) {
                    Text(
                        "还没填身体状况，先按本季时令给你排。点上方的「改条件」补上慢病和症状，推荐会更准。",
                        style = MaterialTheme.typography.labelSmall,
                        color = Ink2,
                        lineHeight = 18.sp
                    )
                }
            } else {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    member.conditions.forEach {
                        Tag(it, Cinnabar.copy(alpha = 0.9f), Cinnabar.copy(alpha = 0.08f))
                    }
                    member.symptoms.forEach {
                        Tag(it, JadeLight, Jade.copy(alpha = 0.09f))
                    }
                }
                if (want.isNotEmpty()) {
                    Spacer(Modifier.height(9.dp))
                    Text(
                        "优先匹配：" + want.joinToString(" · ") { plainTag(it) },
                        style = MaterialTheme.typography.labelSmall,
                        color = Ink3,
                        lineHeight = 17.sp
                    )
                }
                if (member.conditions.contains("痛风")) {
                    Spacer(Modifier.height(9.dp))
                    Text(
                        "痛风急性期请避开肉汤、海鲜、动物内脏；这里只硬性排除了螺蛳、鳝段、虾皮、蛤蜊等最明确的几道，羊肉牛肉类请自己控量。",
                        style = MaterialTheme.typography.labelSmall,
                        color = Cinnabar.copy(alpha = 0.9f),
                        lineHeight = 17.sp
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
        }

        if (here.isNotEmpty()) {
            item {
                SectionHeader("本节气 · ${term.name}", "${here.size} 道")
            }
            items(here, key = { it.ref.id }) { s ->
                SuggestionCard(s, favs, onOpenRecipe, onToggleFav)
            }
        }

        if (rest.isNotEmpty()) {
            item {
                SectionHeader("其他节气的同类菜", "${rest.size} 道")
            }
            items(rest, key = { it.ref.id }) { s ->
                SuggestionCard(s, favs, onOpenRecipe, onToggleFav)
            }
        }

        if (list.isEmpty()) {
            item {
                EmptyState(
                    glyph = "空",
                    title = "这个组合一时没有对口的菜",
                    hint = "试试少选几个症状，或者到食单页自己翻"
                )
            }
        }

        item {
            FootNote("按菜品标签匹配，属饮食参考，不能替代药物治疗与医生给出的膳食方案")
        }
    }

    if (showPicker) {
        TermPicker(
            currentIndex = pickTerm,
            accent = Jade,
            onPick = {
                showPicker = false
                pickTerm = it
            },
            onDismiss = { showPicker = false }
        )
    }
}

@Composable
private fun SectionHeader(title: String, count: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .width(3.dp)
                .height(12.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(Jade)
        )
        Spacer(Modifier.width(8.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, color = Ink)
        Spacer(Modifier.width(8.dp))
        Text(count, style = MaterialTheme.typography.labelSmall, color = Ink3)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SuggestionCard(
    s: Suggestion,
    favs: Set<String>,
    onOpenRecipe: (RecipeRef) -> Unit,
    onToggleFav: (String) -> Unit
) {
    val r = s.ref.recipe
    PaperCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .clickable { onOpenRecipe(s.ref) }
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(r.name, style = MaterialTheme.typography.titleMedium, color = Ink)
                        Spacer(Modifier.width(7.dp))
                        Text(
                            s.ref.term.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = Ink3,
                            modifier = Modifier
                                .border(1.dp, Line, RoundedCornerShape(3.dp))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        )
                    }
                    Spacer(Modifier.height(5.dp))
                    Text(r.sub, style = MaterialTheme.typography.labelSmall, color = Ink3)
                    Spacer(Modifier.height(5.dp))
                    Text(
                        "⏱ ${r.time}　·　难度 ${r.level}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Ink3
                    )
                }
                FavStar(
                    isFav = favs.contains(s.ref.id),
                    onClick = { onToggleFav(s.ref.id) },
                    size = 28
                )
            }

            if (s.hits.isNotEmpty()) {
                Spacer(Modifier.height(11.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("为什么推荐", style = MaterialTheme.typography.labelSmall, color = Jade)
                    Spacer(Modifier.width(7.dp))
                    FlowRow(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        s.hits.forEach { t ->
                            Text(
                                plainTag(t),
                                style = MaterialTheme.typography.labelSmall,
                                color = Jade,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Jade.copy(alpha = 0.10f))
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "适合 · ${s.ref.health.fitHead}",
                style = MaterialTheme.typography.labelSmall,
                color = Ink2,
                maxLines = 2,
                lineHeight = 17.sp
            )
        }
    }
}
