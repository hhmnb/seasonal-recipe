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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.repair.data.AGE_GROUPS
import com.repair.data.CONDITIONS
import com.repair.data.Member
import com.repair.data.MemberStore
import com.repair.data.SYMPTOMS
import com.repair.data.TERMS

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FamilyScreen(
    members: List<Member>,
    nowIndex: Int,
    editId: String? = null,
    onEditConsumed: () -> Unit = {},
    onBack: () -> Unit,
    onSave: (Member) -> Unit,
    onDelete: (String) -> Unit,
    onOpen: (Member) -> Unit
) {
    var editing by remember { mutableStateOf<Member?>(null) }
    var showEditor by remember { mutableStateOf(false) }
    val term = TERMS[nowIndex]

    // 从推荐页点「改条件」时，直接打开对应成员的编辑框
    LaunchedEffect(editId) {
        if (editId != null) {
            val m = members.firstOrNull { it.id == editId }
            if (m != null) {
                editing = m
                showEditor = true
            }
            onEditConsumed()
        }
    }

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
                    .padding(top = 10.dp, bottom = 4.dp),
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
            }
            Spacer(Modifier.height(6.dp))
            Text("家人档案", style = MaterialTheme.typography.headlineSmall, color = Ink, fontSize = 22.sp)
            Spacer(Modifier.height(6.dp))
            Text(
                "填入家人的慢病与当下症状，按当前节气（${term.name}）挑出合适且能买到的家常菜。",
                style = MaterialTheme.typography.labelSmall,
                color = Ink3,
                lineHeight = 18.sp
            )
            Spacer(Modifier.height(18.dp))
        }

        if (members.isEmpty()) {
            item {
                EmptyState(
                    glyph = "亲",
                    title = "还没有添加家人",
                    hint = "点下面的按钮，填上称呼、年龄和身体状况，就会按节气给出推荐"
                )
            }
        }

        items(members, key = { it.id }) { m ->
            MemberCard(
                member = m,
                onClick = { onOpen(m) },
                onEdit = {
                    editing = m
                    showEditor = true
                }
            )
            Spacer(Modifier.height(10.dp))
        }

        item {
            Spacer(Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, Line, RoundedCornerShape(10.dp))
                    .clickable {
                        editing = null
                        showEditor = true
                    }
                    .padding(vertical = 15.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("＋ 添加家人", style = MaterialTheme.typography.titleSmall, color = Jade)
            }
            Spacer(Modifier.height(16.dp))
            FootNote("推荐按标签匹配，属饮食参考；用药与治疗请遵医嘱")
        }
    }

    if (showEditor) {
        MemberEditor(
            initial = editing,
            onDismiss = { showEditor = false },
            onDelete = editing?.let { m -> { onDelete(m.id); showEditor = false } },
            onConfirm = {
                onSave(it)
                showEditor = false
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MemberCard(member: Member, onClick: () -> Unit, onEdit: () -> Unit) {
    PaperCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(15.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Jade.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        member.name.take(1),
                        fontFamily = Serif,
                        fontSize = 17.sp,
                        color = Jade
                    )
                }
                Spacer(Modifier.width(11.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(member.name, style = MaterialTheme.typography.titleMedium, color = Ink)
                    Spacer(Modifier.height(3.dp))
                    Text(member.age, style = MaterialTheme.typography.labelSmall, color = Ink3)
                }
                Text(
                    "编辑",
                    style = MaterialTheme.typography.labelSmall,
                    color = Ink3,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { onEdit() }
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                )
            }

            if (member.conditions.isNotEmpty() || member.symptoms.isNotEmpty()) {
                Spacer(Modifier.height(11.dp))
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
            }

            Spacer(Modifier.height(13.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Jade.copy(alpha = 0.07f))
                    .clickable { onClick() }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "为 ${member.name} 挑本季的菜　›",
                    style = MaterialTheme.typography.labelLarge,
                    color = Jade
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MemberEditor(
    initial: Member?,
    onDismiss: () -> Unit,
    onDelete: (() -> Unit)?,
    onConfirm: (Member) -> Unit
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var age by remember { mutableStateOf(initial?.age ?: "成人") }
    var conds by remember { mutableStateOf(initial?.conditions ?: emptyList()) }
    var syms by remember { mutableStateOf(initial?.symptoms ?: emptyList()) }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Paper)
                .heightIn(max = 560.dp)
        ) {
            Column(
                modifier = Modifier
                    .heightIn(max = 470.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Text(
                    if (initial == null) "添加家人" else "编辑 ${initial.name}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Ink
                )
                Spacer(Modifier.height(14.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { if (it.length <= 8) name = it },
                    label = { Text("称呼") },
                    placeholder = { Text("如 爸爸、妈妈、小宝") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))
                EditorLabel("年龄")
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    AGE_GROUPS.forEach { a ->
                        PickChip(a, age == a) { age = a }
                    }
                }

                Spacer(Modifier.height(16.dp))
                EditorLabel("慢病（可多选，没有就跳过）")
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    CONDITIONS.forEach { c ->
                        PickChip(c, conds.contains(c), accent = Cinnabar) {
                            conds = if (conds.contains(c)) conds - c else conds + c
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
                EditorLabel("最近的不舒服（可多选，会随节气推荐对口的菜）")
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    SYMPTOMS.forEach { sy ->
                        PickChip(sy, syms.contains(sy)) {
                            syms = if (syms.contains(sy)) syms - sy else syms + sy
                        }
                    }
                }
                Spacer(Modifier.height(6.dp))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 12.dp, top = 6.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onDelete != null) {
                    Text(
                        "删除",
                        style = MaterialTheme.typography.labelMedium,
                        color = Cinnabar,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onDelete() }
                            .padding(horizontal = 8.dp, vertical = 8.dp)
                    )
                }
                Spacer(Modifier.weight(1f))
                TextButton(onClick = onDismiss) {
                    Text("取消", color = Ink3)
                }
                TextButton(
                    onClick = {
                        onConfirm(
                            Member(
                                id = initial?.id ?: MemberStore.newId(),
                                name = name.trim(),
                                age = age,
                                conditions = conds,
                                symptoms = syms
                            )
                        )
                    },
                    enabled = name.trim().isNotEmpty()
                ) {
                    Text(if (name.trim().isEmpty()) "填个称呼" else "保存", color = Jade)
                }
            }
        }
    }
}

@Composable
private fun EditorLabel(text: String) {
    Text(text, style = MaterialTheme.typography.labelMedium, color = Ink2)
}

@Composable
private fun PickChip(
    text: String,
    selected: Boolean,
    accent: Color = Jade,
    onClick: () -> Unit
) {
    Text(
        text,
        style = MaterialTheme.typography.labelMedium,
        color = if (selected) Color.White else Ink2,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(7.dp))
            .background(if (selected) accent else PaperDeep)
            .border(
                1.dp,
                if (selected) accent else Line,
                RoundedCornerShape(7.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 7.dp)
    )
}
