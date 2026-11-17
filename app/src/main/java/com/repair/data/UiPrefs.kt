package com.repair.data

import android.content.Context

/**
 * 界面字号档位。
 *
 * 做法是把整棵界面的 fontScale 乘上这里的系数（见 MainActivity），
 * 而不是逐个改 fontSize——这样所有写死的 sp 也会一起变大，
 * 不会出现「标题变大了、小字还是那么小」的半吊子效果。
 * 上限定在 1.35：再大会把节气切换条、底部导航挤变形。
 */
val FONT_STEPS = listOf(1.00f, 1.10f, 1.22f, 1.35f)

val FONT_LABELS = listOf("标准", "较大", "大", "特大")

val FONT_HINTS = listOf(
    "原始排版，信息一屏最多",
    "略放大，适合刚开始老花",
    "明显放大，长时间看更省力",
    "最大字号，视力下降较重时用"
)

object UiPrefs {
    private const val FILE = "jieqi_prefs"
    private const val KEY = "font_step"

    fun load(ctx: Context): Int =
        ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .getInt(KEY, 0)
            .coerceIn(0, FONT_STEPS.size - 1)

    fun save(ctx: Context, step: Int) {
        ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY, step.coerceIn(0, FONT_STEPS.size - 1))
            .apply()
    }
}
