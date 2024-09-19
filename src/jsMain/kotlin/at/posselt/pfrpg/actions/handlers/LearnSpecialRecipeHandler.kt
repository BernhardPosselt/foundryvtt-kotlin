package at.posselt.pfrpg.actions.handlers

import at.posselt.pfrpg.actions.ActionDispatcher
import at.posselt.pfrpg.actions.ActionMessage
import at.posselt.pfrpg.camping.applyConsumptionMealEffects
import at.posselt.pfrpg.camping.cookingCost
import at.posselt.pfrpg.camping.discoverCost
import at.posselt.pfrpg.camping.getActorsCarryingFood
import at.posselt.pfrpg.camping.getActorsInCamp
import at.posselt.pfrpg.camping.getAllRecipes
import at.posselt.pfrpg.camping.getCamping
import at.posselt.pfrpg.camping.getCampingActor
import at.posselt.pfrpg.camping.getCampingActorByUuid
import at.posselt.pfrpg.camping.getCompendiumFoodItems
import at.posselt.pfrpg.camping.reduceFoodBy
import at.posselt.pfrpg.camping.setCamping
import at.posselt.pfrpg.data.checks.DegreeOfSuccess
import at.posselt.pfrpg.fromCamelCase
import at.posselt.pfrpg.utils.postChatTemplate
import com.foundryvtt.core.Game
import com.foundryvtt.pf2e.actor.PF2ECharacter
import kotlinx.js.JsPlainObject

@JsPlainObject
external interface LearnSpecialRecipeData {
    val actorUuid: String
    val name: String
    val degree: String
}

class LearnSpecialRecipeHandler(
    private val game: Game,
) : ActionHandler("learnSpecialRecipe") {
    override suspend fun execute(action: ActionMessage, dispatcher: ActionDispatcher) {
        val campingActor = game.getCampingActor() ?: return
        val camping = campingActor.getCamping() ?: return
        val data = action.data.unsafeCast<LearnSpecialRecipeData>()
        val degreeOfSuccess = fromCamelCase<DegreeOfSuccess>(data.degree) ?: return
        val recipeName = data.name
        val actor = getCampingActorByUuid(data.actorUuid)
        val recipe = camping.getAllRecipes().find { it.name == recipeName }
        if (recipe != null && actor != null) {
            val cost = if (degreeOfSuccess == DegreeOfSuccess.CRITICAL_SUCCESS) {
                recipe.cookingCost()
            } else {
                recipe.discoverCost()
            }
            if (degreeOfSuccess == DegreeOfSuccess.CRITICAL_FAILURE && actor is PF2ECharacter) {
                actor.applyConsumptionMealEffects(recipe.criticalFailure)
            }
            reduceFoodBy(
                actors = camping.getActorsCarryingFood(game),
                foodAmount = cost,
                foodItems = getCompendiumFoodItems(),
            )
            if (degreeOfSuccess.succeeded()) {
                camping.cooking.knownRecipes = (camping.cooking.knownRecipes + recipeName).distinct().toTypedArray()
                campingActor.setCamping(camping)
            }
            postChatTemplate("Learned recipe $recipeName")
        }
    }
}