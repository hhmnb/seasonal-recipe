package com.repair.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** 纸感卡片 */
@Composable
fun PaperCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    background: Color = PaperCard,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .clip(shape)
            .background(background)
            .border(1.dp, Line, shape)
    ) {
        content()
    }
}

/** 小节标题：竖条 + 标题 + 右侧更多 */
@Composable
fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier,
    more: String? = null,
    onMore: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .width(3.dp)
                .height(13.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Jade)
        )
        Spacer(Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.titleMedium, color = Ink)
        Spacer(Modifier.weight(1f))
        if (more != null) {
            Text(
                text = more,
                style = MaterialTheme.typography.labelSmall,
                color = Ink3,
                modifier = if (onMore != null) Modifier.clickable { onMore() } else Modifier
            )
        }
    }
}

/** 小标签 */
@Composable
fun Tag(text: String, color: Color, bg: Color, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = color,
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bg)
            .padding(horizontal = 7.dp, vertical = 4.dp)
    )
}

/** 描边标签（用于深色底） */
@Composable
fun OutlineTag(text: String, color: Color) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = color,
        modifier = Modifier
            .clip(RoundedCornerShape(3.dp))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(3.dp))
            .padding(horizontal = 9.dp, vertical = 4.dp)
    )
}

/** 慢病友好标签行：低钠 / 高钾 / 低GI / 控糖 / 高纤维 / 少油 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HealthTags(tags: List<String>, modifier: Modifier = Modifier) {
    if (tags.isEmpty()) return
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        tags.forEach { t ->
            val c = when (t) {
                "低钠" -> Color(0xFF3A6B8A)
                "高钾" -> Color(0xFF5B4B8A)
                "低GI" -> Color(0xFF2E6B4F)
                "控糖" -> Color(0xFF2E6B4F)
                "高纤维" -> Color(0xFF7A5E2E)
                "少油" -> Color(0xFF8A4B3A)
                "润肺止咳" -> Color(0xFF4A7A8C)
                "清热降火" -> Color(0xFF2E6B6B)
                "健脾开胃" -> Color(0xFF8A6A2E)
                "温中散寒" -> Color(0xFF9B4B2F)
                "润肠通便" -> Color(0xFF6B5A3A)
                "安神" -> Color(0xFF6A5A8A)
                "利水消肿" -> Color(0xFF3A6B8A)
                "易消化" -> Color(0xFF5C7A6B)
                "高嘌呤" -> Cinnabar
                "高糖" -> Cinnabar
                "高盐" -> Cinnabar
                else -> Ink3
            }
            Text(
                text = t,
                style = MaterialTheme.typography.labelSmall,
                color = c,
                modifier = Modifier
                    .clip(RoundedCornerShape(3.dp))
                    .background(c.copy(alpha = 0.10f))
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            )
        }
    }
}

/** 宜 / 忌 两栏卡片 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun YijiBox(isYi: Boolean, items: List<String>, modifier: Modifier = Modifier) {
    val accent = if (isYi) Jade else Cinnabar
    PaperCard(modifier) {
        Row(
            modifier = Modifier.padding(start = 13.dp, end = 13.dp, top = 12.dp, bottom = 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(19.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(accent),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isYi) "宜" else "忌",
                    color = Color.White,
                    fontSize = 10.sp
                )
            }
            Spacer(Modifier.width(6.dp))
            Text(
                text = if (isYi) "此时宜食" else "此时当忌",
                style = MaterialTheme.typography.titleSmall,
                color = Ink
            )
        }
        FlowRow(
            modifier = Modifier.padding(start = 13.dp, end = 13.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            items.forEach { Tag(it, accent, accent.copy(alpha = 0.07f)) }
        }
    }
}

/** 空状态 */
@Composable
fun EmptyState(glyph: String, title: String, hint: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 44.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(glyph, fontFamily = Serif, fontSize = 32.sp, color = Ink3.copy(alpha = 0.35f))
        Spacer(Modifier.height(10.dp))
        Text(title, style = MaterialTheme.typography.bodyMedium, color = Ink3)
        Spacer(Modifier.height(4.dp))
        Text(hint, style = MaterialTheme.typography.labelSmall, color = Ink3, textAlign = TextAlign.Center)
    }
}

/** 细分隔线 */
@Composable
fun HairLine(modifier: Modifier = Modifier, color: Color = Line) {
    Box(
        modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(color)
    )
}

/** 页脚说明 */
@Composable
fun FootNote(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = Ink3,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 26.dp, bottom = 8.dp)
    )
}

/* ============ 讲解型排版组件 ============ */

/** 序号 + 小标题 + 正文的段落卡，用于「饮食原则」三段 */
@Composable
fun PrincipleCard(index: Int, title: String, text: String, accent: Color) {
    PaperCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
    ) {
        Column(modifier = Modifier.padding(13.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(accent),
                    contentAlignment = Alignment.Center
                ) {
                    Text("${index + 1}", color = Color.White, fontSize = 11.sp)
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    title,
                    style = MaterialTheme.typography.titleSmall,
                    color = Ink,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text,
                style = MaterialTheme.typography.bodySmall,
                color = Ink2,
                lineHeight = 20.sp,
                textAlign = TextAlign.Justify
            )
        }
    }
}

/** 「为什么这一节吃它」：竖线 + 小标题 + 正文，用于食谱的功效讲解 */
@Composable
fun WhyBlock(text: String, accent: Color, label: String = "为什么这一节吃它") {
    Row(modifier = Modifier.fillMaxWidth()) {
        Box(
            Modifier
                .width(2.dp)
                .heightIn(min = 34.dp)
                .background(accent.copy(alpha = 0.5f))
        )
        Spacer(Modifier.width(9.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = accent,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text,
                style = MaterialTheme.typography.bodySmall,
                color = Ink2,
                lineHeight = 19.sp,
                textAlign = TextAlign.Justify
            )
        }
    }
}

/**
 * 一行「人群 + 做法」。
 * 上行为具体人群（加粗、用节气主色），下行为该怎么吃、怎么改，
 * 让不会看标签的人也能一眼判断「这道菜我家能不能做」。
 */
@Composable
fun PointLine(
    mark: String,
    head: String,
    body: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Text(
            text = mark,
            fontSize = 9.sp,
            lineHeight = 12.sp,
            color = color,
            modifier = Modifier
                .padding(top = 2.dp)
                .border(1.dp, color.copy(alpha = 0.45f), RoundedCornerShape(2.dp))
                .padding(horizontal = 4.dp, vertical = 2.dp)
        )
        Spacer(Modifier.width(7.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                head,
                style = MaterialTheme.typography.bodySmall,
                color = color,
                fontWeight = FontWeight.Medium,
                lineHeight = 18.sp
            )
            if (body.isNotEmpty()) {
                Spacer(Modifier.height(2.dp))
                Text(
                    body,
                    style = MaterialTheme.typography.labelSmall,
                    color = Ink3,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

/** 本节食养总纲：一段大字讲解，末尾带三个要点小签 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GistCard(text: String, keys: List<String>, accent: Color, soft: Color, deep: Color) {
    PaperCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(15.dp)) {
            Text(
                "食养要义",
                style = MaterialTheme.typography.labelSmall,
                color = accent,
                letterSpacing = 1.4.sp
            )
            Spacer(Modifier.height(7.dp))
            Text(
                text,
                style = MaterialTheme.typography.bodyLarge,
                color = Ink2,
                lineHeight = 26.sp,
                textAlign = TextAlign.Justify
            )
            if (keys.isNotEmpty()) {
                Spacer(Modifier.height(11.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    keys.forEach {
                        Text(
                            it,
                            style = MaterialTheme.typography.labelSmall,
                            color = deep,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(soft.copy(alpha = 0.7f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

/** 区块下方的灰色说明，解释这块内容怎么用 */
@Composable
fun SectionHint(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelSmall,
        color = Ink3,
        lineHeight = 17.sp,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

/** 适宜 / 慎食人群的一行说明，宜用节气主色，忌用朱砂色 */
@Composable
fun CrowdLine(mark: String, text: String, color: Color, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.Top) {
        Text(
            mark,
            fontSize = 9.sp,
            lineHeight = 13.sp,
            color = color,
            modifier = Modifier
                .padding(top = 1.dp)
                .border(1.dp, color.copy(alpha = 0.45f), RoundedCornerShape(2.dp))
                .padding(horizontal = 3.dp, vertical = 1.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text,
            style = MaterialTheme.typography.labelSmall,
            color = Ink2,
            lineHeight = 17.sp,
            modifier = Modifier.weight(1f)
        )
    }
}
