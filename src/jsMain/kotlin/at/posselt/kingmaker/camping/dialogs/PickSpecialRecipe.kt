package at.posselt.kingmaker.camping.dialogs

import at.posselt.kingmaker.app.FormElementContext
import at.posselt.kingmaker.app.RadioInput
import at.posselt.kingmaker.app.awaitablePrompt
import at.posselt.kingmaker.camping.CampingData
import at.posselt.kingmaker.camping.FoodCost
import at.posselt.kingmaker.camping.RecipeData
import at.posselt.kingmaker.camping.buildFoodCost
import at.posselt.kingmaker.camping.discoverCost
import at.posselt.kingmaker.camping.findCurrentRegion
import at.posselt.kingmaker.camping.getAllRecipes
import at.posselt.kingmaker.camping.getCompendiumFoodItems
import at.posselt.kingmaker.camping.getFoodAmount
import at.posselt.kingmaker.utils.buildUuid
import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.ui.TextEditor
import com.foundryvtt.pf2e.actor.PF2EParty
import kotlinx.coroutines.async
import kotlinx.coroutines.await
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.js.JsPlainObject

@JsPlainObject
private external interface LearnSpecialRecipeData {
    val recipe: String
}

@JsPlainObject
private external interface RecipeContextRow {
    val label: String
    val dc: Int
    val discoverCost: FoodCost
    val input: FormElementContext
}

@JsPlainObject
private external interface LearnSpecialRecipeContext {
    val formRows: Array<RecipeContextRow>
}

suspend fun pickSpecialRecipe(
    partyActor: PF2EParty?,
    camping: CampingData
): RecipeData? = coroutineScope {
    val learnedRecipes = camping.cooking.knownRecipes.toSet()
    val allRecipes = camping.getAllRecipes()
    val items = getCompendiumFoodItems()
    val totalItems = camping.getFoodAmount(partyActor, items)
    val rows = allRecipes.asSequence()
        .filter { it.level < (camping.findCurrentRegion()?.level ?: 0) }
        .filter { it.name !in learnedRecipes }
        .sortedBy { it.level }
        .mapIndexed { index, recipe ->
            async {
                val label = TextEditor.enrichHTML(buildUuid(recipe.uuid, recipe.name)).await()
                RecipeContextRow(
                    label = label,
                    dc = recipe.cookingLoreDC,
                    discoverCost = buildFoodCost(
                        amount = recipe.discoverCost(),
                        totalAmount = totalItems,
                        items = items
                    ),
                    input = RadioInput(
                        name = "recipe",
                        value = index == 0,
                        label = label,
                        escapeLabel = false,
                        hideLabel = true,
                    ).toContext(),
                )
            }
        }.toList()
        .awaitAll()
        .toTypedArray()
    console.log(rows)
    awaitablePrompt<LearnSpecialRecipeData, RecipeData?>(
        title = "Recipes learnable in Zone",
        templatePath = "applications/camping/learn-recipe.hbs",
        templateContext = LearnSpecialRecipeContext(
            formRows = rows,
        ).unsafeCast<AnyObject>()
    ) { data ->
        allRecipes.find { it.name == data.recipe }
    }
}