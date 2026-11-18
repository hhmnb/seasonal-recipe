package com.repair.data

import java.util.Calendar

/** 时令食材 */
data class Food(
    val name: String,
    val glyph: String,
    val nature: String,
    val effect: String
)

/** 节气食谱 */
data class Recipe(
    val name: String,
    val sub: String,
    val time: String,
    val level: String,
    val ingredients: List<String>,
    val steps: List<String>,
    val tip: String
)

/** 单个节气 */
data class Term(
    val name: String,
    val py: String,
    val season: String,
    val seasonEn: String,
    val month: Int,
    val c: Double,
    val phrase: String,
    val hou: String,
    val principle: String,
    val quote: String,
    val keys: List<String>,
    val yi: List<String>,
    val ji: List<String>,
    val foods: List<Food>,
    val recipes: List<Recipe>,
    val living: String,
    val avoid: List<String>
)

/** 食谱引用，用于收藏与弹层定位 */
data class RecipeRef(val termIndex: Int, val recipeIndex: Int) {
    val id: String get() = "$termIndex-$recipeIndex"
    val term: Term get() = TERMS[termIndex]
    val recipe: Recipe get() = TERMS[termIndex].recipes[recipeIndex]
}

/**
 * 当前所处节气。
 * passedDays 为进入该节气第几天（从 0 起），toNextDays 为距下一节气天数。
 */
data class NowTerm(
    val index: Int,
    val startDay: Long,
    val nextIndex: Int,
    val nextDay: Long,
    val today: Long
) {
    val passedDays: Int get() = ((today - startDay) / DAY_MS).toInt()
    val toNextDays: Int get() = ((nextDay - today) / DAY_MS).toInt()
    val progress: Float
        get() {
            val total = (nextDay - startDay).toFloat()
            return if (total <= 0f) 0f else ((today - startDay) / total).coerceIn(0f, 1f)
        }

    companion object {
        const val DAY_MS = 86400000L
    }
}

/**
 * 节气日期推算。
 * 采用通用近似算法：day = ⌊(Y×0.2422 + C)⌋ − ⌊(Y−1)/4⌋，Y 为年份后两位。
 * 与官方历书多数年份一致，个别年份可能有一日之差。
 * 使用 java.util.Calendar 以兼容 minSdk < 26，无需 desugaring。
 */
object SolarCalendar {

    fun dayOfMonth(year: Int, term: Term): Int {
        val y = year % 100
        val v = (y * 0.2422 + term.c).toInt() - (y - 1) / 4
        return v.coerceIn(1, 28)
    }

    fun termMillis(year: Int, term: Term): Long {
        val cal = Calendar.getInstance()
        cal.clear()
        cal.set(year, term.month - 1, dayOfMonth(year, term), 0, 0, 0)
        return cal.timeInMillis
    }

    fun todayMillis(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun now(terms: List<Term> = TERMS): NowTerm {
        val today = todayMillis()
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val entries = ArrayList<Pair<Int, Long>>(terms.size * 3)
        for (y in year - 1..year + 1) {
            terms.forEachIndexed { i, t -> entries.add(i to termMillis(y, t)) }
        }
        entries.sortBy { it.second }
        var cur = entries[0]
        var nxt = entries[1]
        for (k in entries.indices) {
            if (entries[k].second <= today) {
                cur = entries[k]
                if (k + 1 < entries.size) nxt = entries[k + 1]
            }
        }
        return NowTerm(cur.first, cur.second, nxt.first, nxt.second, today)
    }

    /** 用于列表展示：某节气在今年的月/日 */
    fun monthDayLabel(term: Term): String {
        val y = Calendar.getInstance().get(Calendar.YEAR)
        return "${term.month}/${dayOfMonth(y, term)}"
    }
}

/** 全部食谱扁平化，供食单页使用 */
val ALL_RECIPES: List<RecipeRef> = TERMS.indices.flatMap { i ->
    TERMS[i].recipes.indices.map { j -> RecipeRef(i, j) }
}
