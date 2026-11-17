package com.repair

import android.content.Context
import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.repair.data.ALL_RECIPES
import com.repair.data.FONT_STEPS
import com.repair.data.Food
import com.repair.data.Member
import com.repair.data.MemberStore
import com.repair.data.MyRecipe
import com.repair.data.MyRecipeStore
import com.repair.data.RecipeRef
import com.repair.data.SolarCalendar
import com.repair.data.UiPrefs
import com.repair.ui.AppTheme
import com.repair.ui.DetailScreen
import com.repair.ui.FamilyScreen
import com.repair.ui.HomeScreen
import com.repair.ui.Ink
import com.repair.ui.Ink3
import com.repair.ui.Jade
import com.repair.ui.MeScreen
import com.repair.ui.MenuScreen
import com.repair.ui.MyRecipesScreen
import com.repair.ui.Paper
import com.repair.ui.PaperCard
import com.repair.ui.RecipeSheet
import com.repair.ui.RecommendScreen
import com.repair.ui.Serif
import com.repair.ui.TermsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT)
        )
        setContent {
            val ctx = LocalContext.current
            // 全应用字号档位：把 fontScale 整体乘上去，连写死的 sp 一起变
            var fontStep by remember { mutableStateOf(UiPrefs.load(ctx)) }
            val ambient = LocalDensity.current
            CompositionLocalProvider(
                LocalDensity provides Density(
                    ambient.density,
                    ambient.fontScale * FONT_STEPS[fontStep.coerceIn(0, FONT_STEPS.size - 1)]
                )
            ) {
                AppTheme {
                    AppRoot(
                        fontStep = fontStep,
                        onFontStep = { step ->
                            fontStep = step
                            UiPrefs.save(ctx, step)
                        }
                    )
                }
            }
        }
    }
}

private data class TabItem(val label: String, val icon: ImageVector)

private val TAB_ITEMS = listOf(
    TabItem("今日", Icons.Filled.Home),
    TabItem("节气", Icons.Filled.DateRange),
    TabItem("食单", Icons.Filled.Menu),
    TabItem("食谱本", Icons.Filled.Edit),
    TabItem("我的", Icons.Filled.Person)
)

@Composable
fun AppRoot(
    fontStep: Int = 0,
    onFontStep: (Int) -> Unit = {}
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("jieqi_prefs", Context.MODE_PRIVATE) }

    var favs by remember {
        mutableStateOf(
            prefs.getStringSet("favs", emptySet())?.toSet() ?: emptySet()
        )
    }
    var constitution by rememberSaveable {
        mutableStateOf(prefs.getString("cons", "平和") ?: "平和")
    }
    val noteStore = remember { MyRecipeStore(context) }
    var myNotes by remember { mutableStateOf(noteStore.load()) }
    val memberStore = remember { MemberStore(context) }
    var members by remember { mutableStateOf(memberStore.load()) }
    var familyOpen by rememberSaveable { mutableStateOf(false) }
    var recId by rememberSaveable { mutableStateOf<String?>(null) }
    var editTarget by rememberSaveable { mutableStateOf<String?>(null) }
    var tab by rememberSaveable { mutableStateOf(0) }
    var filter by rememberSaveable { mutableStateOf("all") }

    // 详情页：-1 表示关闭；shownDetail 用于退出动画期间保持内容
    var detail by rememberSaveable { mutableStateOf(-1) }
    var shownDetail by remember { mutableStateOf(0) }
    LaunchedEffect(detail) { if (detail >= 0) shownDetail = detail }

    // 食谱弹层
    var sheet by remember { mutableStateOf<RecipeRef?>(null) }
    var shownSheet by remember { mutableStateOf<RecipeRef?>(null) }
    LaunchedEffect(sheet) { if (sheet != null) shownSheet = sheet }

    // 食材说明弹窗
    var foodInfo by remember { mutableStateOf<Food?>(null) }

    val now = remember { SolarCalendar.now() }

    val toggleFav: (String) -> Unit = { id ->
        val next = favs.toMutableSet()
        if (!next.add(id)) next.remove(id)
        favs = next
        prefs.edit().putStringSet("favs", next).apply()
    }
    val setConstitution: (String) -> Unit = { value ->
        constitution = value
        prefs.edit().putString("cons", value).apply()
    }
    val openRecipe: (RecipeRef) -> Unit = { sheet = it }

    val addNote: (String, String, String) -> Unit = { name, ing, steps ->
        val next = listOf(MyRecipe(MyRecipeStore.newId(), name, ing, steps)) + myNotes
        myNotes = next
        noteStore.save(next)
    }
    val deleteNote: (String) -> Unit = { id ->
        val next = myNotes.filterNot { it.id == id }
        myNotes = next
        noteStore.save(next)
    }
    val importFavs: () -> Unit = {
        val exists = myNotes.map { it.name }.toHashSet()
        val add = favs.mapNotNull { id -> ALL_RECIPES.firstOrNull { it.id == id } }
            .filterNot { exists.contains(it.recipe.name) }
            .map { ref ->
                MyRecipe(
                    MyRecipeStore.newId(),
                    ref.recipe.name,
                    ref.recipe.ingredients.joinToString("、"),
                    ref.recipe.steps.mapIndexed { i, st -> "${i + 1}. $st" }.joinToString("\n"),
                    "来自${ref.term.name} · ${ref.recipe.time} · ${ref.recipe.level}"
                )
            }
        if (add.isNotEmpty()) {
            val next = add + myNotes
            myNotes = next
            noteStore.save(next)
        }
    }

    val saveMember: (Member) -> Unit = { m ->
        val next = if (members.any { it.id == m.id }) {
            members.map { if (it.id == m.id) m else it }
        } else {
            members + m
        }
        members = next
        memberStore.save(next)
    }
    val deleteMember: (String) -> Unit = { id ->
        val next = members.filterNot { it.id == id }
        members = next
        memberStore.save(next)
        if (recId == id) recId = null
    }

    BackHandler(enabled = detail >= 0 || sheet != null || familyOpen || recId != null) {
        when {
            sheet != null -> sheet = null
            recId != null -> recId = null
            familyOpen -> familyOpen = false
            else -> detail = -1
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Paper)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Paper,
            bottomBar = {
                NavigationBar(
                    containerColor = Color(0xFFFAF6EE),
                    tonalElevation = 0.dp
                ) {
                    TAB_ITEMS.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = tab == index,
                            onClick = { tab = index },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = {
                                Text(item.label, fontSize = 10.sp, letterSpacing = 1.sp)
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Jade,
                                selectedTextColor = Jade,
                                indicatorColor = Jade.copy(alpha = 0.1f),
                                unselectedIconColor = Ink3,
                                unselectedTextColor = Ink3
                            )
                        )
                    }
                }
            }
        ) { inner ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(inner)
            ) {
                when (tab) {
                    0 -> HomeScreen(
                        now = now,
                        onOpenTerm = { detail = it },
                        onOpenRecipe = { ti, ri -> openRecipe(RecipeRef(ti, ri)) },
                        onOpenFood = { ti, fi ->
                            foodInfo = com.repair.data.TERMS[ti].foods.getOrNull(fi)
                        }
                    )
                    1 -> TermsScreen(
                        nowIndex = now.index,
                        onOpenTerm = { detail = it }
                    )
                    2 -> MenuScreen(
                        favs = favs,
                        filter = filter,
                        onFilter = { filter = it },
                        onOpen = {
                            openRecipe(it)
                        },
                        onToggleFav = toggleFav
                    )
                    3 -> MyRecipesScreen(
                        notes = myNotes,
                        favCount = favs.size,
                        onAdd = addNote,
                        onDelete = deleteNote,
                        onImportFavs = importFavs,
                        onPickFav = { openRecipe(it) }
                    )
                    else -> MeScreen(
                        now = now,
                        favs = favs,
                        constitution = constitution,
                        members = members,
                        fontStep = fontStep,
                        onFontStep = onFontStep,
                        onConstitution = setConstitution,
                        onOpenFamily = { familyOpen = true },
                        onOpen = { openRecipe(it) },
                        onToggleFav = toggleFav
                    )
                }
            }
        }

        // 节气详情
        AnimatedVisibility(
            visible = detail >= 0,
            modifier = Modifier.fillMaxSize(),
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it })
        ) {
            DetailScreen(
                index = shownDetail,
                favs = favs,
                onBack = { detail = -1 },
                onOpenTerm = { detail = it },
                onOpenRecipe = { openRecipe(it) },
                onToggleFav = toggleFav
            )
        }

        // 家人档案
        AnimatedVisibility(
            visible = familyOpen,
            modifier = Modifier.fillMaxSize(),
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it })
        ) {
            FamilyScreen(
                members = members,
                nowIndex = now.index,
                editId = editTarget,
                onEditConsumed = { editTarget = null },
                onBack = { familyOpen = false },
                onSave = saveMember,
                onDelete = deleteMember,
                onOpen = { recId = it.id }
            )
        }

        // 按家人条件推荐
        val recMember = members.firstOrNull { it.id == recId }
        var shownRec by remember { mutableStateOf<Member?>(null) }
        LaunchedEffect(recMember) { if (recMember != null) shownRec = recMember }
        AnimatedVisibility(
            visible = recMember != null,
            modifier = Modifier.fillMaxSize(),
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it })
        ) {
            shownRec?.let { m ->
                RecommendScreen(
                    member = m,
                    termIndex = now.index,
                    favs = favs,
                    onBack = { recId = null },
                    onOpenRecipe = { openRecipe(it) },
                    onToggleFav = toggleFav,
                    onEditMember = {
                        editTarget = m.id
                        recId = null
                    }
                )
            }
        }

        // 食谱弹层
        AnimatedVisibility(
            visible = sheet != null,
            modifier = Modifier.fillMaxSize(),
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            shownSheet?.let { ref ->
                RecipeSheet(
                    ref = ref,
                    favs = favs,
                    onDismiss = { sheet = null },
                    onToggleFav = toggleFav,
                    onOpenTerm = { ti ->
                        sheet = null
                        detail = ti
                    }
                )
            }
        }
    }

    // 食材说明
    foodInfo?.let { food ->
        AlertDialog(
            onDismissRequest = { foodInfo = null },
            containerColor = PaperCard,
            title = {
                Text(food.name, fontFamily = Serif, fontSize = 20.sp, color = Ink)
            },
            text = {
                Column {
                    Text(
                        "性味　${food.nature}　·　${food.cat}",
                        style = MaterialTheme.typography.labelMedium,
                        color = Jade
                    )
                    Text(
                        food.effect,
                        style = MaterialTheme.typography.bodyMedium,
                        color = com.repair.ui.Ink2,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    if (food.fit.isNotEmpty()) {
                        com.repair.ui.CrowdLine(
                            mark = "宜",
                            text = food.fit,
                            color = Jade,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                    if (food.avoid.isNotEmpty()) {
                        com.repair.ui.CrowdLine(
                            mark = "忌",
                            text = food.avoid,
                            color = com.repair.ui.Cinnabar.copy(alpha = 0.85f),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { foodInfo = null }) {
                    Text("知道了", color = Jade)
                }
            }
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun AppPreview() {
    AppTheme {
        AppRoot()
    }
}
