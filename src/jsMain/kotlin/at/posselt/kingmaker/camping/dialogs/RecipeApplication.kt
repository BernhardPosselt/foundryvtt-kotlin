package at.posselt.kingmaker.camping.dialogs

import at.posselt.kingmaker.app.*
import at.posselt.kingmaker.app.FormApp
import at.posselt.kingmaker.app.forms.CheckboxInput
import at.posselt.kingmaker.app.forms.FormElementContext
import at.posselt.kingmaker.app.forms.NumberInput
import at.posselt.kingmaker.app.forms.SectionsContext
import at.posselt.kingmaker.app.forms.Select
import at.posselt.kingmaker.app.forms.TextInput
import at.posselt.kingmaker.app.forms.formContext
import at.posselt.kingmaker.app.forms.toOption
import at.posselt.kingmaker.camping.*
import at.posselt.kingmaker.data.general.Rarity
import at.posselt.kingmaker.fromCamelCase
import at.posselt.kingmaker.utils.buildPromise
import at.posselt.kingmaker.utils.fromUuidTypeSafe
import at.posselt.kingmaker.utils.launch
import com.foundryvtt.core.Game
import com.foundryvtt.core.applications.api.HandlebarsRenderOptions
import com.foundryvtt.pf2e.actor.PF2ENpc
import com.foundryvtt.pf2e.item.PF2EEffect
import js.array.push
import js.core.Void
import kotlinx.coroutines.await
import kotlinx.js.JsPlainObject
import org.w3c.dom.HTMLElement
import org.w3c.dom.get
import org.w3c.dom.pointerevents.PointerEvent
import kotlin.js.Promise

@JsPlainObject
external interface RecipeContext : SectionsContext, HandlebarsRenderContext {
    val isFormValid: Boolean
}

@JsPlainObject
external interface OutcomeSubmitData {
    val uuid: String
    val removeAfterRest: Boolean
    val doublesHealing: Boolean
    val changeRestDurationSeconds: Int
}

@JsPlainObject
external interface RecipeSubmitData {
    val name: String
    val uuid: String
    val level: Int
    val rarity: String
    val cost: String
    val cookingLoreDC: Int
    val survivalDC: Int
    val basicIngredients: Int
    val specialIngredients: Int
    val favoriteMeal: OutcomeSubmitData
    val criticalSuccess: OutcomeSubmitData
    val success: OutcomeSubmitData
    val criticalFailure: OutcomeSubmitData
}

@OptIn(ExperimentalJsExport::class)
@JsExport
class RecipeApplication(
    private val game: Game,
    private val actor: PF2ENpc,
    recipe: RecipeData? = null,
    private val afterSubmit: () -> Unit,
) : FormApp<RecipeContext, RecipeSubmitData>(
    title = if (recipe == null) "Add Recipe" else "Edit Recipe: ${recipe.name}",
    template = "components/forms/application-form.hbs",
    debug = true,
) {
    private val editRecipeName = recipe?.name
    private var currentRecipe: RecipeData? = recipe

    override fun _onClickAction(event: PointerEvent, target: HTMLElement) {
        when (target.dataset["action"]) {
            "openDocumentLink" -> buildPromise {
                event.preventDefault()
                event.stopPropagation()
                target.dataset["uuid"]?.let { fromUuidTypeSafe<PF2EEffect>(it)?.sheet?.launch() }
            }

            "save" -> save()
        }
    }

    override fun _preparePartContext(
        partId: String,
        context: HandlebarsRenderContext,
        options: HandlebarsRenderOptions
    ): Promise<RecipeContext> = buildPromise {
        val parent = super._preparePartContext(partId, context, options).await()
        val effects = game.items.contents
            .filterIsInstance<PF2EEffect>()
        val recipeItem = currentRecipe?.uuid?.let { fromUuidTypeSafe<PF2EEffect>(it) } ?: effects.firstOrNull()
        val favoriteMeal = createMealInputs(
            namePrefix = "favoriteMeal",
            cookingOutcome = currentRecipe?.favoriteMeal,
            allEffects = effects,
        )
        val criticalSuccess = createMealInputs(
            namePrefix = "criticalSuccess",
            cookingOutcome = currentRecipe?.criticalSuccess,
            allEffects = effects,
        )
        val success = createMealInputs(
            namePrefix = "success",
            cookingOutcome = currentRecipe?.success,
            allEffects = effects,
        )
        val criticalFailure = createMealInputs(
            namePrefix = "criticalFailure",
            cookingOutcome = currentRecipe?.criticalFailure,
            allEffects = effects,
        )
        RecipeContext(
            partId = parent.partId,
            isFormValid = isFormValid,
            sections = arrayOf(
                at.posselt.kingmaker.app.forms.SectionContext(
                    legend = "Basic",
                    formRows = formContext(
                        TextInput(
                            stacked = false,
                            label = "Name",
                            name = "name",
                            disabled = editRecipeName != null,
                            value = currentRecipe?.name ?: "",
                            required = true,
                            help = "To override an existing recipe, use the same name",
                        ),
                        Select(
                            label = "Recipe Item",
                            name = "uuid",
                            help = "Needs to be an Effect Item",
                            value = recipeItem?.uuid,
                            options = effects.mapNotNull { it.toOption(useUuid = true) },
                            stacked = false,
                            item = recipeItem,
                        ),
                        Select.level(
                            stacked = false,
                            value = currentRecipe?.level ?: 1,
                        ),
                        Select.fromEnum<Rarity>(
                            label = "Rarity",
                            name = "rarity",
                            stacked = false,
                            elementClasses = listOf("km-rarity"),
                            value = currentRecipe?.rarity?.let { fromCamelCase<Rarity>(it) } ?: Rarity.COMMON,
                        ),
                        TextInput(
                            label = "Cost",
                            name = "cost",
                            stacked = false,
                            value = currentRecipe?.cost ?: "0 gp",
                        )
                    )
                ),
                at.posselt.kingmaker.app.forms.SectionContext(
                    legend = "Cooking",
                    formRows = formContext(
                        Select.dc(
                            label = "Cooking Lore DC",
                            name = "cookingLoreDC",
                            stacked = false,
                            value = currentRecipe?.cookingLoreDC ?: 13,
                        ),
                        Select.dc(
                            label = "Survival DC",
                            name = "survivalDC",
                            stacked = false,
                            value = currentRecipe?.survivalDC ?: 15,
                        ),
                        NumberInput(
                            label = "Basic Ingredients",
                            name = "basicIngredients",
                            stacked = false,
                            value = currentRecipe?.basicIngredients ?: 0,
                        ),
                        NumberInput(
                            label = "Special Ingredients",
                            name = "specialIngredients",
                            stacked = false,
                            value = currentRecipe?.specialIngredients ?: 0,
                        ),
                    )
                ),
                at.posselt.kingmaker.app.forms.SectionContext(
                    legend = "Favorite Meal",
                    formRows = favoriteMeal,
                ),
                at.posselt.kingmaker.app.forms.SectionContext(
                    legend = "Critical Success",
                    formRows = criticalSuccess,
                ),
                at.posselt.kingmaker.app.forms.SectionContext(
                    legend = "Success",
                    formRows = success,
                ),
                at.posselt.kingmaker.app.forms.SectionContext(
                    legend = "Critical Failure",
                    formRows = criticalFailure,
                ),
            )
        )
    }


    fun save(): Promise<Void> = buildPromise {
        if (isValid()) {
            actor.getCamping()?.let { camping ->
                currentRecipe?.let { data ->
                    camping.cooking.homebrewMeals = camping.cooking.homebrewMeals
                        .filter { it.name != data.name }
                        .toTypedArray()
                    camping.cooking.homebrewMeals.push(data)
                    actor.setCamping(camping)
                    close().await()
                    afterSubmit()
                }
            }
        }
        undefined
    }

    override fun onParsedSubmit(value: RecipeSubmitData): Promise<Void> = buildPromise {
        currentRecipe = RecipeData(
            name = editRecipeName ?: value.name,
            basicIngredients = value.basicIngredients,
            specialIngredients = value.specialIngredients,
            cookingLoreDC = value.cookingLoreDC,
            survivalDC = value.survivalDC,
            uuid = value.uuid,
            level = value.level,
            cost = value.cost,
            rarity = value.rarity,
            isHomebrew = true,
            criticalSuccess = toOutcome(value.criticalSuccess),
            success = toOutcome(value.success),
            criticalFailure = toOutcome(value.criticalFailure),
            favoriteMeal = toOutcome(value.favoriteMeal),
        )
        undefined
    }

}

private suspend fun createMealInputs(
    namePrefix: String,
    cookingOutcome: CookingOutcome?,
    allEffects: List<PF2EEffect>,
): Array<FormElementContext> {
    val firstEffect = cookingOutcome?.effects?.firstOrNull()
    val item = firstEffect?.uuid
        ?.let { fromUuidTypeSafe<PF2EEffect>(it) }
        ?: allEffects.firstOrNull()
    return formContext(
        Select(
            label = "Effect",
            name = "$namePrefix.uuid",
            options = allEffects.mapNotNull { it.toOption(useUuid = true) },
            stacked = false,
            item = item,
            value = item?.uuid,
        ),
        CheckboxInput(
            label = "Remove after Rest",
            name = "$namePrefix.removeAfterRest",
            stacked = false,
            value = firstEffect?.removeAfterRest ?: false,
        ),
        CheckboxInput(
            label = "Doubles Healing",
            help = "Double HP regained from resting, does not stack with other effects that double healing",
            name = "$namePrefix.doublesHealing",
            value = firstEffect?.doublesHealing ?: false,
        ),
        NumberInput(
            label = "Rest Duration",
            help = "Seconds to add to an individuals rest duration; can be negative",
            name = "$namePrefix.changeRestDurationSeconds",
            stacked = false,
            value = firstEffect?.changeRestDurationSeconds ?: 0,
        ),
    )
}

private fun toOutcome(outcome: OutcomeSubmitData): CookingOutcome =
    CookingOutcome(
        effects = arrayOf(
            MealEffect(
                uuid = outcome.uuid,
                removeAfterRest = outcome.removeAfterRest,
                changeRestDurationSeconds = outcome.changeRestDurationSeconds,
                doublesHealing = outcome.doublesHealing,
            )
        )
    )