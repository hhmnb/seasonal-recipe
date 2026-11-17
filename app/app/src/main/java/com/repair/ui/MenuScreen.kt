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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.repair.data.ALL_RECIPES
import com.repair.data.RecipeRef
import com.repair.data.TERMS

@Composable
fun MenuScreen(
    favs: Set<String>,
    filter: String,
    onFilter: (String) -> Unit,
    onOpen: (RecipeRef) -> Unit,
    onToggleFav: (String) -> Unit
) {
    val filters = listOf(
        "all" to "全部", "春" to "春", "夏" to "夏",
        "秋" to "秋", "冬" to "冬", "fav" to "我的收藏"
    )
    val list = remember(filter, favs) {
        when (filter) {
            "fav" -> ALL_RECIPES.filter { favs.contains(it.id) }
            "all" -> ALL_RECIPES
            else -> ALL_RECIPES.filter { TERMS[it.termIndex].season == filter }
        }
    }
    val grouped = remember(list) { list.groupBy { it.termIndex } }

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
                    Text("节气食单", style = MaterialTheme.typography.headlineSmall, color = Ink, fontSize = 21.sp)
                    Spacer(Modifier.height(3.dp))
                    Text(
                        "SEASONAL RECIPES",
                        style = MaterialTheme.typography.labelSmall,
                        color = Ink3,
                        letterSpacing = 2.4.sp
                    )
                }
                Spacer(Modifier.weight(1f))
                Column(horizontalAlignment = Alignment.End) {
                    Text("共 ${ALL_RECIPES.size} 道时令菜", style = MaterialTheme.typography.labelSmall, color = Ink3)
                    Spacer(Modifier.height(3.dp))
                    Text(
                        "已收藏 ${favs.size} 道",
                        style = MaterialTheme.typography.labelMedium,
                        color = Jade,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filters) { (key, label) ->
                    FilterChip(label = label, selected = filter == key) { onFilter(key) }
                }
            }
        }

        if (grouped.isEmpty()) {
            item {
                EmptyState(glyph = "空", title = "还没有收藏的食单", hint = "在食谱里点星标即可收藏")
            }
        }

        grouped.forEach { (termIndex, refs) ->
            val term = TERMS[termIndex]
            val skin = skinOf(term.season)
            item(key = "head_$termIndex") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 22.dp, bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        term.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = skin.accent
                    )
                    Spacer(Modifier.width(9.dp))
                    Box(
                        Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(Line)
                    )
                    Spacer(Modifier.width(9.dp))
                    Text(term.phrase, style = MaterialTheme.typography.labelSmall, color = Ink3)
                }
            }
            items(refs, key = { it.id }) { ref ->
                RecipeRow(
                    ref = ref,
                    isFav = favs.contains(ref.id),
                    onClick = { onOpen(ref) },
                    onToggleFav = { onToggleFav(ref.id) }
                )
                Spacer(Modifier.height(9.dp))
            }
        }

        item { FootNote("共 ${ALL_RECIPES.size} 道节气食谱 · 依二十四节气时令编排") }
    }
}

@Composable
private fun FilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        color = if (selected) Paper else Ink2,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) Ink else PaperCard)
            .border(1.dp, if (selected) Ink else Line, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 15.dp, vertical = 7.dp)
    )
}

@Composable
fun RecipeRow(
    ref: RecipeRef,
    isFav: Boolean,
    onClick: () -> Unit,
    onToggleFav: () -> Unit
) {
    val term = ref.term
    val skin = skinOf(term.season)
    PaperCard(modifier = Modifier.clickable { onClick() }) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(skin.gradient),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    ref.recipe.name.take(1),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(ref.recipe.name, style = MaterialTheme.typography.titleSmall, color = Ink)
                Spacer(Modifier.height(4.dp))
                Text(
                    ref.recipe.sub,
                    style = MaterialTheme.typography.labelSmall,
                    color = Ink3,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "${ref.recipe.time} · ${ref.recipe.level} · ${ref.recipe.ingredients.size} 味料",
                    style = MaterialTheme.typography.labelSmall,
                    color = Ink3
                )
            }
            Spacer(Modifier.width(8.dp))
            FavStar(isFav = isFav, onClick = onToggleFav)
        }
    }
}

/** 收藏星标：用字符绘制，避免图标依赖 */
@Composable
fun FavStar(isFav: Boolean, onClick: () -> Unit, size: Int = 30) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(if (isFav) Cinnabar.copy(alpha = 0.08f) else Color.Transparent)
            .border(1.dp, if (isFav) Cinnabar.copy(alpha = 0.3f) else Line, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isFav) "★" else "☆",
            fontSize = (size * 0.45f).sp,
            color = if (isFav) Cinnabar else Ink3
        )
    }
}
