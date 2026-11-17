package com.repair.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

/** 家庭成员。一个人可以有多个慢病与多个当下症状 */
data class Member(
    val id: String,
    val name: String,
    /** 儿童 / 成人 / 老年 */
    val age: String,
    /** 慢病，取值见 CONDITIONS */
    val conditions: List<String>,
    /** 当下症状，取值见 SYMPTOMS */
    val symptoms: List<String>
)

val AGE_GROUPS = listOf("儿童", "成人", "老年")

val CONDITIONS = listOf("高血压", "血糖高", "血脂高", "痛风")

val SYMPTOMS = listOf("咳嗽", "咽干", "上火", "便秘", "腹泻", "食欲差", "失眠", "畏寒", "鼻塞", "水肿")

/** 症状 / 慢病对应的挑菜方向，用厨房里的话讲，不摆术语 */
fun conditionHint(name: String): String = when (name) {
    "高血压" -> "挑盐少、钾高、不油腻的菜"
    "血糖高" -> "挑升糖慢、不加糖、纤维多的菜"
    "血脂高" -> "挑油少、纤维多的菜"
    "痛风" -> "避开螺蛳、鳝段、虾皮、蛤蜊等高嘌呤的菜"
    else -> ""
}

fun symptomHint(name: String): String = when (name) {
    "咳嗽" -> "润肺止咳，梨、银耳、百合、萝卜一类"
    "咽干" -> "润肺生津，忌辛辣燥热"
    "上火" -> "清热降火，绿豆、苦瓜、冬瓜、芹菜一类"
    "便秘" -> "润肠通便，粗纤维与油脂适量"
    "腹泻" -> "健脾止泻，山药、小米、扁豆一类"
    "食欲差" -> "健脾开胃，少油少腻"
    "失眠" -> "安神，莲藕、百合、茼蒿一类"
    "畏寒" -> "温中散寒，姜、葱、羊肉、花椒一类"
    "鼻塞" -> "辛温通窍，葱白、生姜一类"
    "水肿" -> "利水消肿，冬瓜、薏米、赤小豆一类"
    else -> ""
}

/** 慢病与年龄对应的标签，权重低于症状 */
fun conditionTags(m: Member): Set<String> {
    val set = linkedSetOf<String>()
    if (m.age == "儿童" || m.age == "老年") set += "易消化"
    m.conditions.forEach {
        when (it) {
            "高血压" -> set += listOf("低钠", "高钾", "少油")
            "血糖高" -> set += listOf("低GI", "控糖", "高纤维")
            "血脂高" -> set += listOf("少油", "高纤维")
        }
    }
    return set
}

/** 当下症状对应的标签，权重高于慢病 */
fun symptomTags(m: Member): Set<String> {
    val set = linkedSetOf<String>()
    m.symptoms.forEach {
        when (it) {
            "咳嗽", "咽干" -> set += "润肺止咳"
            "上火" -> set += "清热降火"
            "便秘" -> set += "润肠通便"
            "腹泻", "食欲差" -> set += "健脾开胃"
            "失眠" -> set += "安神"
            "畏寒", "鼻塞" -> set += "温中散寒"
            "水肿" -> set += "利水消肿"
        }
    }
    return set
}

/** 该成员应当优先匹配的全部标签 */
fun wantedTags(m: Member): Set<String> {
    val set = linkedSetOf<String>()
    set.addAll(conditionTags(m))
    set.addAll(symptomTags(m))
    return set
}

/** 该成员必须避开的标签 */
fun blockedTags(m: Member): Set<String> {
    val set = mutableSetOf<String>()
    if (m.conditions.contains("痛风")) set += "高嘌呤"
    if (m.conditions.contains("血糖高")) set += "高糖"
    if (m.conditions.contains("高血压")) set += "高盐"
    return set
}

/** 一条推荐结果 */
data class Suggestion(
    val ref: RecipeRef,
    /** 命中的标签，界面用来解释「为什么推荐这道」 */
    val hits: List<String>,
    val inCurrentTerm: Boolean,
    val score: Int
)

/**
 * 按成员条件推荐食谱。
 * 打分：命中一个症状标签 +12，命中一个慢病标签 +8；
 * 属于当前节气 +12，属于同一季节 +4。
 * 分数优先于节气，避免「本节气只沾一个标签的菜」压过「别处全面对口的菜」。
 * 命中禁忌标签的菜直接排除。
 */
fun suggestFor(member: Member, termIndex: Int, limit: Int = 18): List<Suggestion> {
    val sym = symptomTags(member)
    val cond = conditionTags(member)
    val block = blockedTags(member)
    val season = TERMS[termIndex].season
    return ALL_RECIPES
        .filter { ref -> ref.recipe.tags.none { block.contains(it) } }
        .map { ref ->
            val hits = ref.recipe.tags.filter { sym.contains(it) || cond.contains(it) }
            var score = ref.recipe.tags.count { sym.contains(it) } * 12 +
                ref.recipe.tags.count { cond.contains(it) } * 8
            if (ref.termIndex == termIndex) score += 12
            else if (ref.term.season == season) score += 4
            Suggestion(ref, hits, ref.termIndex == termIndex, score)
        }
        // 填了条件时，只保留至少命中一个标签的菜；没填就按节气全排
        .filter { (sym.isEmpty() && cond.isEmpty()) || it.hits.isNotEmpty() }
        .sortedWith(
            compareByDescending<Suggestion> { it.score }
                .thenByDescending { it.inCurrentTerm }
                .thenBy { it.ref.termIndex }
        )
        .take(limit)
}

/** 家人档案本地存储（SharedPreferences + JSON） */
class MemberStore(context: Context) {

    private val prefs = context.getSharedPreferences("jieqi_prefs", Context.MODE_PRIVATE)

    fun load(): List<Member> {
        val raw = prefs.getString("members", null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            val list = ArrayList<Member>(arr.length())
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                list.add(
                    Member(
                        id = o.optString("id"),
                        name = o.optString("name"),
                        age = o.optString("age", "成人"),
                        conditions = o.optJSONArray("cond").toStringList(),
                        symptoms = o.optJSONArray("sym").toStringList()
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun save(list: List<Member>) {
        val arr = JSONArray()
        list.forEach { m ->
            arr.put(
                JSONObject().apply {
                    put("id", m.id)
                    put("name", m.name)
                    put("age", m.age)
                    put("cond", JSONArray(m.conditions))
                    put("sym", JSONArray(m.symptoms))
                }
            )
        }
        prefs.edit().putString("members", arr.toString()).apply()
    }

    companion object {
        fun newId(): String = UUID.randomUUID().toString().take(8)
    }
}

private fun JSONArray?.toStringList(): List<String> {
    if (this == null) return emptyList()
    val list = ArrayList<String>(length())
    for (i in 0 until length()) list.add(optString(i))
    return list
}
