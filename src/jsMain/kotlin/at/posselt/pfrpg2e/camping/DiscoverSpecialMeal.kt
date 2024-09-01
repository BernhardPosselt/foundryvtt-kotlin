package at.posselt.pfrpg2e.camping

import at.posselt.pfrpg2e.data.checks.DegreeOfSuccess
import at.posselt.pfrpg2e.utils.postChatTemplate
import com.foundryvtt.core.AnyObject
import com.foundryvtt.pf2e.actor.PF2ENpc
import kotlinx.js.JsPlainObject

@JsPlainObject
private external interface DiscoverSpecialMealChatContext {
    val learnRecipe: Boolean
    val recoverHalf: Boolean
    val applyCriticalFailure: Boolean
    val recipeName: String
    val actorUuid: String
}

suspend fun postDiscoverSpecialMeal(
    actorUuid: String,
    recipe: RecipeData,
    degreeOfSuccess: DegreeOfSuccess
) {
    postChatTemplate(
        templatePath = "chatmessages/discover-special-meal.hbs",
        templateContext = DiscoverSpecialMealChatContext(
            recipeName = recipe.name,
            actorUuid = actorUuid,
            learnRecipe = degreeOfSuccess.succeeded(),
            recoverHalf = degreeOfSuccess == DegreeOfSuccess.CRITICAL_SUCCESS,
            applyCriticalFailure = degreeOfSuccess == DegreeOfSuccess.CRITICAL_FAILURE,
        ).unsafeCast<AnyObject>(),
    )
}

suspend fun CampingData.learnSpecialMeal(
    campingActor: PF2ENpc,
    actorUuid: String,
    recipeName: String,
    isRecoverHalf: Boolean,
    isSuccess: Boolean,
    isCriticalFailure: Boolean,
) {
    val camping = campingActor.getCamping()
    val actors = camping?.getActorsInCamp()
    val actor = getCampingActorByUuid(actorUuid)
    val recipe = getAllRecipes().find { it.name == recipeName }
    if (recipe != null && actor != null && actors != null) {
        val cost = if (isRecoverHalf) {
            recipe.cookingCost()
        } else {
            recipe.discoverCost()
        }
        if (isCriticalFailure) {
            actor.applyMealEffects(listOf(recipe.criticalFailure))
        }
        val leftOver = reduceFoodBy(actors, foodAmount = cost, foodItems = getCompendiumFoodItems())
        if (isSuccess) {
            camping.cooking.knownRecipes = (camping.cooking.knownRecipes + recipeName).distinct().toTypedArray()
            campingActor.setCamping(camping)
        }
        // TODO: message to chat for removed food
    }
}