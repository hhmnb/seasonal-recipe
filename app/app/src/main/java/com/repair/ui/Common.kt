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
