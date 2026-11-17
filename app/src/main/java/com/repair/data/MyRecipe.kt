package com.repair.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

/** 用户自己记的食谱 */
data class MyRecipe(
    val id: String,
    val name: String,
    val ingredients: String,
    val steps: String,
    val note: String = ""
)

/** 我的食谱本地存储（SharedPreferences + JSON，无需数据库） */
class MyRecipeStore(context: Context) {

    private val prefs = context.getSharedPreferences("jieqi_prefs", Context.MODE_PRIVATE)

    fun load(): List<MyRecipe> {
        val raw = prefs.getString("my_recipes", null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            val list = ArrayList<MyRecipe>(arr.length())
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                list.add(
                    MyRecipe(
                        id = o.optString("id"),
                        name = o.optString("name"),
                        ingredients = o.optString("ing"),
                        steps = o.optString("steps"),
                        note = o.optString("note")
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun save(list: List<MyRecipe>) {
        val arr = JSONArray()
        list.forEach { r ->
            arr.put(
                JSONObject().apply {
                    put("id", r.id)
                    put("name", r.name)
                    put("ing", r.ingredients)
                    put("steps", r.steps)
                    put("note", r.note)
                }
            )
        }
        prefs.edit().putString("my_recipes", arr.toString()).apply()
    }

    companion object {
        fun newId(): String = UUID.randomUUID().toString().take(8)
    }
}
