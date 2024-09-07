package at.posselt.pfrpg.camping

import at.posselt.pfrpg.data.checks.DegreeOfSuccess
import at.posselt.pfrpg.toCamelCase
import at.posselt.pfrpg.utils.postChatTemplate
import com.foundryvtt.core.AnyObject
import kotlinx.js.JsPlainObject

@JsPlainObject
private external interface DiscoverSpecialMealChatContext {
    val degree: String
    val name: String
    val actorUuid: String
    val learnRecipe: Boolean
    val criticalFailure: Boolean
}

suspend fun postDiscoverSpecialMeal(
    actorUuid: String,
    recipe: RecipeData,
    degreeOfSuccess: DegreeOfSuccess
) {
    postChatTemplate(
        templatePath = "chatmessages/discover-special-meal.hbs",
        templateContext = DiscoverSpecialMealChatContext(
            name = recipe.name,
            actorUuid = actorUuid,
            degree = degreeOfSuccess.toCamelCase(),
            learnRecipe = degreeOfSuccess.succeeded(),
            criticalFailure = degreeOfSuccess == DegreeOfSuccess.CRITICAL_FAILURE,
        ).unsafeCast<AnyObject>(),
    )
}
