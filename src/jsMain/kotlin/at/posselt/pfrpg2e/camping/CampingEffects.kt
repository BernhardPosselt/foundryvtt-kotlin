package at.posselt.pfrpg2e.camping

import at.posselt.pfrpg2e.utils.awaitAll
import at.posselt.pfrpg2e.utils.buildPromise
import at.posselt.pfrpg2e.utils.fromUuidTypeSafe
import com.foundryvtt.pf2e.actor.PF2EActor
import com.foundryvtt.pf2e.item.PF2EEffect


fun PF2EActor.getEffectNames() =
    itemTypes.effect.mapNotNull { it.name }.toSet()

/**
 * Given a list of meal effects, get those that are have been applied to a player
 */
suspend fun PF2EActor.getAppliedMealEffects(mealEffects: List<MealEffect>): List<MealEffect> {
    val effectNames = getEffectNames()
    return mealEffects
        .map { buildPromise { fromUuidTypeSafe<PF2EEffect>(it.uuid)?.name to it } }
        .awaitAll()
        .filter { it.first != null && it.first in effectNames }
        .map { it.second }
}

suspend fun PF2EActor.getAppliedCampingEffects(campingData: List<ActivityEffect>): List<ActivityEffect> {
    val effectNames = getEffectNames()
    return campingData
        .map { buildPromise { fromUuidTypeSafe<PF2EEffect>(it.uuid)?.name to it } }
        .awaitAll()
        .filter { it.first != null && it.first in effectNames }
        .map { it.second }
}

/**
 * Only checks if the top level effect doubles healing
 */
fun campingEffectsDoublingHealing(campingData: List<CampingActivityData>): List<ActivityEffect> =
    campingEffectsHaving(campingData) {
        it.doublesHealing != null
    }


/**
 * From a list of recipes, get the meal effects that change rest duration
 */
fun mealEffectsChangingRestDuration(recipes: List<RecipeData>): List<MealEffect> =
    mealEffectsHaving(recipes) {
        it.changeRestDurationSeconds != null
    }

/**
 * From a list of recipes, get the meal effects that double healing
 */
fun mealEffectsDoublingHealing(recipes: List<RecipeData>): List<MealEffect> =
    mealEffectsHaving(recipes) {
        it.doublesHealing != null
    }

private fun mealEffectsHaving(recipes: List<RecipeData>, predicate: (MealEffect) -> Boolean): List<MealEffect> =
    recipes.asSequence()
        .flatMap {
            sequenceOf(
                it.criticalFailure.effects,
                it.success.effects,
                it.criticalSuccess.effects,
                it.favoriteMeal?.effects,
            ).filterNotNull()
                .flatMap(Array<MealEffect>::asSequence)
                .filter(predicate)
        }
        .toList()


private fun campingEffectsHaving(
    data: List<CampingActivityData>,
    predicate: (ActivityEffect) -> Boolean
): List<ActivityEffect> =
    data.asSequence()
        .flatMap {
            sequenceOf(
                it.effectUuids,
                it.success?.effectUuids,
                it.criticalSuccess?.effectUuids,
                it.failure?.effectUuids,
                it.criticalFailure?.effectUuids,
            ).filterNotNull()
                .flatMap(Array<ActivityEffect>::asSequence)
                .filter(predicate)
        }
        .toList()
