package at.posselt.pfrpg.actions.handlers

import at.posselt.pfrpg.actions.ActionDispatcher
import at.posselt.pfrpg.actions.ActionMessage
import at.posselt.pfrpg.camping.MealChoice
import at.posselt.pfrpg.camping.applyConsumptionMealEffects
import at.posselt.pfrpg.camping.findCookingChoices
import at.posselt.pfrpg.camping.getActorsCarryingFood
import at.posselt.pfrpg.camping.getActorsInCamp
import at.posselt.pfrpg.camping.getAllRecipes
import at.posselt.pfrpg.camping.getCamping
import at.posselt.pfrpg.camping.getCampingActor
import at.posselt.pfrpg.camping.getCompendiumFoodItems
import at.posselt.pfrpg.camping.reduceFoodBy
import at.posselt.pfrpg.camping.sum
import at.posselt.pfrpg.data.checks.DegreeOfSuccess
import at.posselt.pfrpg.fromCamelCase
import com.foundryvtt.core.Game
import com.foundryvtt.pf2e.actor.PF2ECharacter
import kotlinx.js.JsPlainObject

@JsPlainObject
external interface ApplyMealEffects {
    val recipe: String
    val degree: String
}

class ApplyMealEffectsHandler(val game: Game) : ActionHandler("applyMealEffects") {
    override suspend fun execute(action: ActionMessage, dispatcher: ActionDispatcher) {
        val camping = game.getCampingActor()?.getCamping() ?: return
        val recipesByName = camping.getAllRecipes().associateBy { it.name }
        val data = action.data.unsafeCast<ApplyMealEffects>()
        val degree = fromCamelCase<DegreeOfSuccess>(data.degree) ?: return
        val recipe = recipesByName[data.recipe] ?: return
        val outcome = when (degree) {
            DegreeOfSuccess.CRITICAL_FAILURE -> recipe.criticalFailure
            DegreeOfSuccess.SUCCESS -> recipe.success
            DegreeOfSuccess.CRITICAL_SUCCESS -> recipe.criticalSuccess
            else -> null
        } ?: return
        val charactersInCampByUuid = camping.getActorsInCamp().filterIsInstance<PF2ECharacter>().associateBy { it.uuid }
        val parsed = camping.findCookingChoices(
            charactersInCampByUuid = charactersInCampByUuid,
            recipesByName = recipesByName
        )
        val mealChoices = parsed.meals
            .filterIsInstance<MealChoice.ParsedMeal>()
        val chosenMeals = mealChoices
            .filter { it.name == data.recipe }
        val actors = chosenMeals.map { it.actor }
        val actorUuids = actors.map { it.uuid }.toSet()

        applyConsumptionMealEffects(
            actors = actors,
            outcome = outcome,
        )

        recipe.favoriteMeal?.let { favoriteOutcome ->
            val favoriteMealActors = mealChoices
                .filter { it.favoriteMeal?.name == data.recipe && it.actor.uuid in actorUuids }
                .map { it.actor }
            applyConsumptionMealEffects(
                actors = favoriteMealActors,
                outcome = favoriteOutcome,
            )
        }
        val totalCost = chosenMeals.map { it.cookingCost }.sum()
        reduceFoodBy(
            actors = camping.getActorsCarryingFood(game),
            foodItems = getCompendiumFoodItems(),
            foodAmount = totalCost,
        )
    }
}