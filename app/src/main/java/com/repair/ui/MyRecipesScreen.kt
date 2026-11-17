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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.repair.data.MyRecipe
import com.repair.data.RecipeRef

@Composable
fun MyRecipesScreen(
    notes: List<MyRecipe>,
    favCount: Int,
    onAdd: (String, String, String) -> Unit,
    onDelete: (String) -> Unit,
    onImportFavs: () -> Unit,
    onPickFav: (RecipeRef) -> Unit
) {
    var editing by rememberSaveable { mutableStateOf(false) }

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
                    .padding(top = 10.dp, bottom = 6.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        "我的食谱",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Ink,
                        fontSize = 21.sp
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        "MY RECIPE BOOK　·　共 ${notes.size} 道",
                        style = MaterialTheme.typography.labelSmall,
                        color = Ink3,
                        letterSpacing = 2.sp
                    )
                }
                Spacer(Modifier.weight(1f))
                Text(
                    "＋ 记一道",
                    style = MaterialTheme.typography.labelLarge,
                    color = Jade,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, Jade.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                        .clickable { editing = true }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                )
            }
        }

        // 空状态
        if (notes.isEmpty()) {
            item {
                PaperCard(modifier = Modifier.fillMaxWidth()) {
                    EmptyState(
                        glyph = "谱",
                        title = "这里还空着",
                        hint = "把自己常做的菜记下来，比翻别人的菜谱管用\n也可以把食单里收藏的菜一次性导入"
                    )
                }
            }
        }

        // 列表
        items(notes, key = { it.id }) { r ->
            Spacer(Modifier.height(10.dp))
            PaperCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.Top) {
                        Text(
                            r.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = Ink,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            "删除",
                            style = MaterialTheme.typography.labelSmall,
                            color = Cinnabar.copy(alpha = 0.75f),
                            modifier = Modifier
                                .clickable { onDelete(r.id) }
                                .padding(start = 10.dp, top = 3.dp)
                        )
                    }
                    if (r.ingredients.isNotBlank()) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "食材　${r.ingredients}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Ink2
                        )
                    }
                    if (r.steps.isNotBlank()) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            r.steps,
                            style = MaterialTheme.typography.bodySmall,
                            color = Ink2,
                            textAlign = TextAlign.Justify
                        )
                    }
                    if (r.note.isNotBlank()) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            r.note,
                            style = MaterialTheme.typography.labelSmall,
                            color = Jade,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Jade.copy(alpha = 0.055f))
                                .padding(horizontal = 9.dp, vertical = 7.dp)
                        )
                    }
                }
            }
        }

        // 导入收藏
        item {
            Spacer(Modifier.height(18.dp))
            PaperCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onImportFavs() }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "从收藏导入",
                            style = MaterialTheme.typography.titleSmall,
                            color = Ink
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            if (favCount == 0) "食单里还没有收藏的菜"
                            else "食单里已收藏 $favCount 道，一键存进本子",
                            style = MaterialTheme.typography.labelSmall,
                            color = Ink3
                        )
                    }
                    Text(
                        "导入 ›",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (favCount == 0) Ink3 else Jade
                    )
                }
            }
        }

        item {
            FootNote("自己记的食谱只存在本机，不会上传\n做法与份量请按自己的口味调整")
        }
    }

    if (editing) {
        RecipeEditorDialog(
            onDismiss = { editing = false },
            onConfirm = { name, ing, steps ->
                onAdd(name, ing, steps)
                editing = false
            }
        )
    }
}

@Composable
private fun RecipeEditorDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var ing by rememberSaveable { mutableStateOf("") }
    var steps by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PaperCard,
        title = {
            Text("记一道菜", fontFamily = Serif, fontSize = 19.sp, color = Ink)
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("菜名", fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = ing,
                    onValueChange = { ing = it },
                    label = { Text("食材（用顿号分隔）", fontSize = 12.sp) },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = steps,
                    onValueChange = { steps = it },
                    label = { Text("做法", fontSize = 12.sp) },
                    minLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onConfirm(name.trim(), ing.trim(), steps.trim()) },
                enabled = name.isNotBlank()
            ) {
                Text("保存", color = if (name.isNotBlank()) Jade else Ink3)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消", color = Ink3)
            }
        }
    )
}

/** 收藏列表里的一条（供导入时预览用，可留空不调用） */
@Composable
fun FavRow(ref: RecipeRef, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(6.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(Jade.copy(alpha = 0.6f))
        )
        Spacer(Modifier.width(9.dp))
        Text(ref.recipe.name, style = MaterialTheme.typography.bodySmall, color = Ink2)
        Spacer(Modifier.weight(1f))
        Text(ref.term.name, style = MaterialTheme.typography.labelSmall, color = Ink3)
    }
}
