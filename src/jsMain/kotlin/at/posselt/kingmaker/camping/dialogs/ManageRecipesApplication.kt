package at.posselt.kingmaker.camping.dialogs

import at.posselt.kingmaker.app.*
import at.posselt.kingmaker.camping.RecipeData
import at.posselt.kingmaker.camping.cookingCost
import at.posselt.kingmaker.camping.discoverCost
import at.posselt.kingmaker.camping.getAllRecipes
import at.posselt.kingmaker.camping.getCamping
import at.posselt.kingmaker.camping.setCamping
import at.posselt.kingmaker.utils.buildPromise
import at.posselt.kingmaker.utils.buildUuid
import at.posselt.kingmaker.utils.launch
import com.foundryvtt.core.Game
import com.foundryvtt.core.ui.TextEditor
import com.foundryvtt.pf2e.actor.PF2ENpc
import js.core.Void
import kotlinx.coroutines.await
import kotlin.js.Promise

@JsExport
class ManageRecipesApplication(
    private val game: Game,
    private val actor: PF2ENpc,
) : CrudApplication(
    title = "Manage Recipes",
    debug = true,
) {
    override fun deleteEntry(id: String) = buildPromise {
        actor.getCamping()?.let { camping ->
            camping.cooking.knownRecipes = camping.cooking.knownRecipes.filter { it != id }.toTypedArray()
            camping.cooking.homebrewMeals = camping.cooking.homebrewMeals.filter { it.name != id }.toTypedArray()
            actor.setCamping(camping)
        }
        undefined
    }

    override fun addEntry(): Promise<Void> = buildPromise {
        RecipeApplication(
            game,
            actor,
            afterSubmit = { render() },
        ).launch()
        undefined
    }

    override fun editEntry(id: String) = buildPromise {
        RecipeApplication(
            game,
            actor,
            actor.getCamping()?.cooking?.homebrewMeals?.find { it.name == id },
            afterSubmit = { render() },
        ).launch()
        undefined
    }

    override fun getItems(): Promise<Array<CrudItem>> = buildPromise {
        actor.getCamping()?.let { camping ->
            val learnedRecipes = camping.cooking.knownRecipes.toSet()
            camping.getAllRecipes()
                .sortedWith(compareBy(RecipeData::level, RecipeData::name))
                .map { recipe ->
                    val recipeName = recipe.name
                    val link = TextEditor.enrichHTML(buildUuid(recipe.uuid, recipeName)).await()
                    val isHomebrew = recipe.isHomebrew ?: false
                    val enabled = learnedRecipes.contains(recipeName)
                    CrudItem(
                        nameIsHtml = true,
                        id = recipeName,
                        name = link,
                        additionalColumns = arrayOf(
                            recipe.rarity,
                            recipe.level.toString(),
                            recipe.cookingLoreDC.toString(),
                            recipe.cookingCost().toDescription(),
                            recipe.discoverCost().toDescription(),
                            recipe.cost,
                        ),
                        enable = CheckboxInput(
                            value = enabled,
                            label = "Enable",
                            hideLabel = true,
                            name = "enabledIds.$recipeName",
                            disabled = recipeName == "Basic Meal" || recipeName == "Hearty Meal",
                        ).toContext(),
                        canBeEdited = isHomebrew,
                        canBeDeleted = isHomebrew,
                    )
                }.toTypedArray()
        } ?: emptyArray()
    }

    override fun getHeadings(): Promise<Array<String>> = buildPromise {
        arrayOf("Rarity", "Level", "DC", "Cooking Cost", "Learning Cost", "Purchase Cost")
    }

    override fun onParsedSubmit(value: CrudData): Promise<Void> = buildPromise {
        console.log("saving", value)
        actor.getCamping()?.let { camping ->
            camping.cooking.knownRecipes = value.enabledIds + arrayOf("Hearty Meal", "Basic Meal")
            actor.setCamping(camping)
        }
        undefined
    }
}
