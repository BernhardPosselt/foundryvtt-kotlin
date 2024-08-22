package at.posselt.kingmaker.camping.dialogs

import at.posselt.kingmaker.app.RadioInput
import at.posselt.kingmaker.app.awaitablePrompt
import at.posselt.kingmaker.camping.CampingData
import at.posselt.kingmaker.camping.Cooking
import at.posselt.kingmaker.camping.RecipeData
import at.posselt.kingmaker.camping.findCurrentRegion
import at.posselt.kingmaker.camping.getAllRecipes
import at.posselt.kingmaker.utils.awaitAll
import at.posselt.kingmaker.utils.buildUuid
import com.foundryvtt.core.ui.TextEditor
import com.foundryvtt.pf2e.actor.PF2ECreature
import js.array.toTypedArray
import js.objects.recordOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.await
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.js.JsPlainObject

@JsPlainObject
external interface LearnSpecialRecipeData {
    val recipe: String
}


suspend fun learnSpecialRecipe(
    actor: PF2ECreature,
    camping: CampingData,
): RecipeData? = coroutineScope {
    val learnedRecipes = camping.cooking.knownRecipes.toSet()
    val allRecipes = camping.getAllRecipes()
    val learnableRecipes = allRecipes.asSequence()
        .filter { it.level < (camping.findCurrentRegion()?.level ?: 0) }
        .filter { it.name !in learnedRecipes }
        .mapIndexed { index, recipe ->
            async {
                RadioInput(
                    name = "recipe",
                    value = index == 0,
                    label = TextEditor.enrichHTML(buildUuid(recipe.uuid, recipe.name)).await(),

                    )
            }
        }
        .toList()
        .awaitAll()

    awaitablePrompt<LearnSpecialRecipeData, RecipeData?>(
        title = "Recipes learnable in Zone",
        templatePath = "components/forms/form.hbs",
        templateContext = recordOf(
            "formRows" to learnableRecipes
        )
    ) { data ->
        allRecipes.find { it.name == data.recipe }
    }
}