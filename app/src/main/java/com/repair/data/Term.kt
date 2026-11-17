package com.repair.data

import java.util.Calendar

/** 时令食材。cat 取值：蔬菜 / 水果 / 谷豆 / 肉蛋 / 调味 / 其他 */
data class Food(
    val name: String,
    val glyph: String,
    val nature: String,
    val effect: String,
    val cat: String,
    /** 适宜人群，写给「谁吃了有好处」 */
    val fit: String = "",
    /** 慎食人群，写给「谁要少吃或忌口」 */
    val avoid: String = ""
)

/** 节气食谱 */
data class Recipe(
    val name: String,
    val sub: String,
    val time: String,
    val level: String,
    val ingredients: List<String>,
    val steps: List<String>,
    val tip: String,
    /** 慢病友好标签，取值见 HEALTH_TAGS */
    val tags: List<String> = emptyList()
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
    /** 气候与身体的一句话概括 */
    val climate: String,
    /** 饮食原则，三段 */
    val principle: List<String>,
    /** 三条核心要点 */
    val focus: List<String>,
    val quote: String,
    val keys: List<String>,
    val yi: List<String>,
    val ji: List<String>,
    val foods: List<Food>,
    val recipes: List<Recipe>,
    val living: String,
    val avoid: List<String>
)

/** 食材分类的固定展示顺序 */
val FOOD_CATS = listOf("蔬菜", "水果", "谷豆", "肉蛋", "调味", "其他")

/** 慢病友好标签，用于食谱筛选。高血压看「低钠/高钾/少油」，血糖看「低GI/控糖/高纤维」 */
val HEALTH_TAGS = listOf("低钠", "高钾", "低GI", "控糖", "高纤维", "少油")

/** 标签对应的说明，用于界面提示 */
fun tagHint(tag: String): String = when (tag) {
    "低钠" -> "盐用量控制在 2g 以内，不用酱油、豆瓣、腌制品提味"
    "高钾" -> "钾含量较高，有助于钠排出，适合高血压人群"
    "低GI" -> "升糖指数低，餐后血糖波动平缓"
    "控糖" -> "少糖或无糖做法，不勾甜芡、不加糖"
    "高纤维" -> "膳食纤维丰富，延缓糖分吸收，增加饱腹感"
    "少油" -> "每份用油不超过 10g，以蒸、煮、焯、快炒为主"
    else -> ""
}

/** 按分类分组，空分类自动略去 */
fun Term.groupedFoods(): List<Pair<String, List<Food>>> =
    FOOD_CATS.map { c -> c to foods.filter { it.cat == c } }
        .filter { it.second.isNotEmpty() }

/** 食谱引用，用于收藏与弹层定位 */
data class RecipeRef(val termIndex: Int, val recipeIndex: Int) {
    val id: String get() = "$termIndex-$recipeIndex"
    val term: Term get() = TERMS[termIndex]
    val recipe: Recipe get() = TERMS[termIndex].recipes[recipeIndex]
}

/** 某个节气的起止区间 */
data class TermRange(
    val startMonth: Int,
    val startDay: Int,
    val endMonth: Int,
    val endDay: Int,
    val days: Int
) {
    val label: String get() = "${startMonth}月${startDay}日 — ${endMonth}月${endDay}日"
    val daysLabel: String get() = "共 $days 天"
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
 *
 * 采用查表法：直接读 TERM_DAYS 中 1900—2100 年逐年推算好的日号。
 * 早先用近似公式 day = ⌊(Y×0.2422 + C)⌋ − L 计算，存在两处硬伤：
 *   1. 闰年修正项分月取值，用错会让闰年 3 月以后所有节气整体偏晚一天；
 *   2. C 值公式本身有若干分钟误差，交节时刻贴近午夜时会差一天
 *      （如 2026 年雨水实为 2 月 18 日 23:52）。
 * 查表后 1900—2100 年不再有累积误差，也不再依赖 Calendar 的闰年逻辑。
 * 超出该范围时退回最后的已知值，按 24 节气正常月份显示。
 */
object SolarCalendar {

    /** 节气名 -> 序号，建一次表避免每次线性查找 */
    private val idx: Map<String, Int> by lazy {
        TERM_ORDER.withIndex().associate { it.value to it.index }
    }

    fun dayOfMonth(year: Int, term: Term): Int {
        val y = year.coerceIn(1900, 2100)
        val row = TERM_DAYS[y - 1900]
        val i = idx[term.name] ?: return 15
        if (row.length < (i + 1) * 2) return 15
        return row.substring(i * 2, i * 2 + 2).toIntOrNull() ?: 15
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

    /** 三年窗口内所有节气的 (索引, 时刻)，按时间升序 */
    private fun window(terms: List<Term>): List<Pair<Int, Long>> {
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val list = ArrayList<Pair<Int, Long>>(terms.size * 3)
        for (y in year - 1..year + 1) {
            terms.forEachIndexed { i, t -> list.add(i to termMillis(y, t)) }
        }
        list.sortBy { it.second }
        return list
    }

    fun now(terms: List<Term> = TERMS): NowTerm {
        val today = todayMillis()
        val entries = window(terms)
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

    /** 取第 index 个节气的起止区间（用于详情页展示「持续多久」） */
    fun rangeOf(index: Int, terms: List<Term> = TERMS): TermRange {
        val today = todayMillis()
        val entries = window(terms)
        // 选出该节气里离今天最近的一次起始时刻
        var pos = -1
        var best = Long.MAX_VALUE
        entries.forEachIndexed { k, e ->
            if (e.first == index) {
                val d = Math.abs(e.second - today)
                if (d < best) { best = d; pos = k }
            }
        }
        if (pos < 0) return TermRange(0, 0, 0, 0, 0)
        val startMs = entries[pos].second
        val endMs = if (pos + 1 < entries.size) entries[pos + 1].second else startMs + 15L * NowTerm.DAY_MS
        val s = Calendar.getInstance().apply { timeInMillis = startMs }
        val e = Calendar.getInstance().apply { timeInMillis = endMs }
        val days = Math.round((endMs - startMs).toDouble() / NowTerm.DAY_MS).toInt()
        return TermRange(
            s.get(Calendar.MONTH) + 1, s.get(Calendar.DAY_OF_MONTH),
            e.get(Calendar.MONTH) + 1, e.get(Calendar.DAY_OF_MONTH),
            days
        )
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
