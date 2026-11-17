package com.repair.data

/**
 * 可查证的食材数据。
 *
 * 这里每一个数值都有出处，不做估算 ———— 查不到官方收录的，宁可留空，界面就不显示。
 *
 * ── 嘌呤（mg/100g）──────────────────────────────
 * 出处：《成人高尿酸血症与痛风食养指南（2024 年版）》附录 1 表 1-2
 *       国家卫生健康委员会 2024 年 2 月发布
 *       表下原注：数据来源《中国食物成分表》2019 年第 6 版 / 第二册，嘌呤含量为总嘌呤值。
 * 分级（同指南表 1-1）：
 *       高     150–1000   痛风急性期避免
 *       较高    75–150     缓解期严格限量
 *       较低    30–75      稍加注意
 *       低      < 30       几乎无需顾忌
 *
 * ── 升糖（GI）──────────────────────────────────
 * 出处：《成人糖尿病食养指南（2023 年版）》表 1.1「各类食物 GI 分类表」
 *       低 GI ≤ 55 ／ 中 GI 55–70 ／ 高 GI > 70
 *
 * ── 盐 ────────────────────────────────────────
 * 出处：《成人高血压食养指南（2023 年版）》表 2.7
 *       2000mg 钠 = 5g 盐，对等于：食盐 5g / 鸡精 10g / 味精 24g /
 *       豆瓣酱 30g / 酱油 32g / 咸菜 63g / 黄酱 78g / 腐乳 84g
 *       该指南同时写明：我国居民膳食中 75% 以上的钠来自家庭烹调盐与高盐调味品。
 *
 * 说明：中医性味与功效为传统食养记载，来自《中华本草》《食疗本草》一类典籍的通行说法，
 *      各家表述存在出入，不属可量化的实验数据，故不标注数值。
 */
data class Nutri(
    /** 嘌呤 mg/100g，null 表示官方表未收录 */
    val purine: Int? = null,
    /** 升糖分级：低 / 中 / 高，空串表示未收录 */
    val gi: String = "",
    /** 其他已核实的数据，如铁、钾、草酸 */
    val extra: String = ""
)

/** 嘌呤含量分级，依据卫健委 2024 指南表 1-1 */
fun purineLevel(mg: Int): String = when {
    mg >= 150 -> "高"
    mg >= 75 -> "较高"
    mg >= 30 -> "较低"
    else -> "低"
}

/** 给痛风人群的一句话判断 */
fun purineAdvice(mg: Int): String = when {
    mg >= 150 -> "急性期避免"
    mg >= 75 -> "缓解期严格限量"
    mg >= 30 -> "少量无妨"
    else -> "可放心吃"
}

/**
 * 食材名 → 查证数据。
 * 键名与 TermData 中的 Food.name 一致；同一种食材在不同节气的叫法若有差异，都在此对齐。
 */
val NUTRI: Map<String, Nutri> = mapOf(
    // ── 蔬菜 ──────────────────────────────────
    "菠菜" to Nutri(purine = 8, gi = "低", extra = "铁 2.9mg/100g，草酸 300–600mg"),
    "芹菜" to Nutri(purine = 5, gi = "低", extra = "钾高钠低"),
    "莴笋" to Nutri(purine = 12, gi = "低"),
    "芦笋" to Nutri(gi = "低", extra = "嘌呤约 50mg，属较低档"),
    "番茄" to Nutri(purine = 17, gi = "低"),
    "圣女果" to Nutri(purine = 17, gi = "低"),
    "茄子" to Nutri(purine = 13, gi = "低"),
    "黄瓜" to Nutri(purine = 11, gi = "低"),
    "苦瓜" to Nutri(purine = 12),
    "冬瓜" to Nutri(purine = 1, extra = "嘌呤几乎为零，钠低钾高"),
    "丝瓜" to Nutri(purine = 14),
    "南瓜" to Nutri(purine = 29, gi = "高"),
    "大白菜" to Nutri(purine = 14),
    "白萝卜" to Nutri(purine = 11),
    "空心菜" to Nutri(purine = 22),
    "茼蒿" to Nutri(purine = 15),
    "香椿" to Nutri(purine = 40, extra = "亚硝酸盐偏高，务必焯水"),
    "豌豆" to Nutri(purine = 86),
    "豇豆" to Nutri(purine = 45),
    "蚕豆" to Nutri(purine = 307, gi = "低", extra = "蚕豆病者绝对禁食"),
    "山药" to Nutri(purine = 15, gi = "低"),
    "芋头" to Nutri(purine = 15, gi = "低"),
    "莲藕" to Nutri(purine = 10),
    "春笋" to Nutri(purine = 13, extra = "草酸高，焯水去涩"),
    "韭菜" to Nutri(extra = "嘌呤未收录于官方表"),
    "豆芽" to Nutri(purine = 11, extra = "按绿豆芽计；黄豆芽为 29mg"),
    "荠菜" to Nutri(extra = "钙含量在叶菜中居前"),
    "苋菜" to Nutri(extra = "铁 5.4mg/100g，高于菠菜，但同属非血红素铁"),
    "秋葵" to Nutri(extra = "官方表未收录"),
    "板栗" to Nutri(purine = 35, extra = "按栗子（熟）计"),
    "香菇" to Nutri(purine = 37, extra = "鲜香菇 37mg；干香菇 405mg，差 11 倍"),
    "红薯" to Nutri(purine = 19, gi = "高"),

    // ── 水果 ──────────────────────────────────
    "苹果" to Nutri(purine = 1, gi = "低"),
    "梨" to Nutri(purine = 5, gi = "低"),
    "桃" to Nutri(purine = 14, gi = "低"),
    "樱桃" to Nutri(purine = 11, gi = "低", extra = "铁仅约 0.4mg，不补铁"),
    "葡萄" to Nutri(purine = 8, gi = "低"),
    "草莓" to Nutri(gi = "低"),
    "猕猴桃" to Nutri(gi = "低"),
    "芒果" to Nutri(purine = 12, gi = "低"),
    "西瓜" to Nutri(purine = 6, gi = "高"),
    "菠萝" to Nutri(purine = 11, gi = "中"),
    "哈密瓜" to Nutri(gi = "中"),
    "柚子" to Nutri(gi = "低", extra = "与他汀类降脂药、部分降压药有相互作用，服药期间慎食"),
    "脐橙" to Nutri(gi = "低"),
    "砂糖橘" to Nutri(purine = 5, gi = "低"),
    "金桔" to Nutri(gi = "低"),
    "杨梅" to Nutri(purine = 10),
    "杏" to Nutri(purine = 5),
    "龙眼" to Nutri(purine = 7, extra = "按桂圆干计"),
    "荔枝" to Nutri(extra = "空腹大量可致急性低血糖，儿童尤甚"),
    "柿子" to Nutri(extra = "空腹食易生胃柿石"),
    "石榴" to Nutri(extra = "官方表未收录"),
    "桑葚" to Nutri(extra = "官方表未收录"),
    "枇杷" to Nutri(extra = "官方表未收录"),
    "山楂" to Nutri(extra = "官方表未收录"),
    "甘蔗" to Nutri(extra = "含糖约 16%，以蔗糖为主"),
    "青枣" to Nutri(extra = "官方表未收录"),
    "冬枣" to Nutri(extra = "维生素 C 约 243mg/100g，果中最高"),
    "鲜枣" to Nutri(purine = 13, extra = "铁仅 1.2mg，晒干后 2.3mg，均不补铁"),

    // ── 谷豆 ──────────────────────────────────
    "小米" to Nutri(purine = 20, gi = "中", extra = "小米粥 GI 中；煮成稀粥更快"),
    "薏米" to Nutri(purine = 15),
    "糯米" to Nutri(purine = 50, gi = "高"),
    "黑米" to Nutri(purine = 63, gi = "低"),
    "黑芝麻" to Nutri(purine = 43, extra = "按黑芝麻（熟）计"),
    "花生" to Nutri(purine = 85, gi = "低"),
    "绿豆" to Nutri(purine = 196, gi = "低"),
    "黄豆" to Nutri(purine = 218, gi = "低", extra = "干豆 218mg，做成豆腐降到 68mg、豆浆 8mg"),
    "黑豆" to Nutri(purine = 170, gi = "低"),
    "赤小豆" to Nutri(purine = 156, extra = "按红小豆计"),
    "白扁豆" to Nutri(extra = "生豆含皂苷，必须煮透"),
    "黄豆芽" to Nutri(purine = 29),

    // ── 肉蛋 ──────────────────────────────────
    "鸡蛋" to Nutri(purine = 1, extra = "嘌呤几乎为零，痛风可放心吃"),
    // 别名：菜谱配料里常写「猪肉馅」「老鸭」「虾仁」「香干」，与官方表的学名对不上，
    // 这里补上，避免菜谱明明有肉却被判成「查不到」。
    "猪肉" to Nutri(purine = 138, extra = "含馅料、肉末"),
    "老鸭" to Nutri(purine = 88, extra = "按烧鸭（熟）计，生鸭肉更高"),
    "虾仁" to Nutri(purine = 187, extra = "按基围虾（生）计"),
    "香干" to Nutri(purine = 94, extra = "按干豆腐（南豆腐）计"),
    "猪里脊" to Nutri(purine = 138),
    "瘦猪肉" to Nutri(purine = 138),
    "猪肝" to Nutri(purine = 275, extra = "铁 22.6mg/100g，血红素铁，吸收率 15–35%"),
    "牛肉" to Nutri(purine = 105),
    "瘦牛肉" to Nutri(purine = 105),
    "羊肉" to Nutri(purine = 109),
    "鸡胸肉" to Nutri(purine = 208),
    "鸡腿" to Nutri(purine = 208, extra = "去皮后脂肪更低"),
    "鸭肉" to Nutri(purine = 88, extra = "按烧鸭（熟）计，生鸭肉更高"),
    "鲫鱼" to Nutri(purine = 154, extra = "熟品 190mg，属高嘌呤"),
    "鲈鱼" to Nutri(purine = 133, extra = "按河鲈鱼计；海鲈鱼 227mg"),
    "鳜鱼" to Nutri(purine = 121, extra = "按桂鱼计"),
    "黄鳝" to Nutri(purine = 127),

    // ── 调味 / 其他 ────────────────────────────
    "大葱" to Nutri(purine = 31),
    "小葱" to Nutri(purine = 31, extra = "按大葱计"),
    "香菜" to Nutri(purine = 21),
    "干辣椒" to Nutri(purine = 6, extra = "按尖椒计"),
    "香醋" to Nutri(purine = 12, extra = "按山西陈醋计"),
    "豆豉" to Nutri(extra = "含盐量高，尽量少用"),
    "蜂蜜" to Nutri(gi = "高", extra = "1 岁以内婴儿禁食"),
    "红枣" to Nutri(purine = 13, extra = "铁仅 2.3mg/100g，不补铁"),
    "枸杞" to Nutri(extra = "官方表未收录"),
    "百合" to Nutri(extra = "官方表未收录"),
    "荷叶" to Nutri(extra = "官方表未收录")
)

/** 取数据，没有就返回 null，界面据此决定是否显示 */
fun nutriOf(food: String): Nutri? = NUTRI[food]

/**
 * 一道菜里嘌呤最高的那一味主料。
 * 配料名常带量词（「鲫鱼 1 条」），所以用包含匹配；取最长命中，免得「鱼」盖过「鲫鱼」。
 * 返回 null 表示这道菜的配料都不在官方表里 —— 此时不显示，也不估算。
 */
fun peakPurine(ingredients: List<String>): Pair<String, Int>? {
    var bestName = ""
    var bestMg = -1
    ingredients.forEach { ing ->
        val hit = NUTRI.entries
            .filter { it.value.purine != null && ing.contains(it.key) }
            .maxByOrNull { it.key.length }
        if (hit != null) {
            val mg = hit.value.purine ?: return@forEach
            if (mg > bestMg) {
                bestMg = mg
                bestName = hit.key
            }
        }
    }
    return if (bestMg < 0) null else bestName to bestMg
}

/**
 * 盐换算。数字取自《成人高血压食养指南（2023 年版）》表 2.7：
 * 每 2000mg 钠（= 5g 盐）对应的调味品用量。
 */
object SaltRef {
    const val DAILY_LIMIT = 5.0      // 每日食盐上限，g
    const val SODIUM_PER_GRAM = 400.0 // 1g 盐 = 400mg 钠

    /** 一平勺（约 10ml）生抽约 12g，约合 1.9g 盐 */
    const val SOY_PER_SPOON = 1.9
    /** 一勺豆瓣酱（约 15g）约合 2.5g 盐 */
    const val BEAN_PASTE_PER_SPOON = 2.5
    /** 一块腐乳（约 12g）约合 0.7g 盐 */
    const val FURU_PER_PIECE = 0.7

    /** 把盐量换算成「占一天上限的百分比」，用于一句话提示 */
    fun share(saltGram: Double): String {
        val pct = (saltGram / DAILY_LIMIT * 100).toInt()
        return "约占一天上限（5g）的 $pct%"
    }
}

/** 数据来源的完整说明，展示在「我的 → 数据说明」 */
val DATA_SOURCES: List<Pair<String, String>> = listOf(
    "嘌呤含量" to "《成人高尿酸血症与痛风食养指南（2024 年版）》附录 1 表 1-2\n国家卫生健康委员会 2024 年 2 月发布\n表下原注：数据出自《中国食物成分表》2019 年第 6 版 / 第二册，为总嘌呤值",
    "升糖指数" to "《成人糖尿病食养指南（2023 年版）》表 1.1\n国家卫生健康委员会发布\n低 GI ≤55，中 GI 55–70，高 GI >70",
    "钠与盐" to "《成人高血压食养指南（2023 年版）》表 2.7\n2000mg 钠 = 5g 盐，约等于酱油 32g / 豆瓣酱 30g / 腐乳 84g\n该指南写明：我国居民 75% 以上的钠来自家庭烹调盐与高盐调味品",
    "性味与功效" to "传统食养记载，取自《中华本草》《食疗本草》一类典籍的通行说法。\n各家表述存在出入，不属实验数据，故不标数值，也不作疗效承诺。",
    "没有标注数值的食材" to "官方表中未收录的，此处留空，不估算、不补值。\n宁可少一个数字，也不给一个编的数字。"
)
