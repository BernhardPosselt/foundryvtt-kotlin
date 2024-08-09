package at.posselt.kingmaker.camping

import at.posselt.kingmaker.utils.awaitAll
import at.posselt.kingmaker.utils.buildPromise
import com.foundryvtt.pf2e.actor.PF2ECharacter

private const val EIGHT_HOURS_SECONDS = 8 * 60 * 60

suspend fun getRestSeconds(players: List<PF2ECharacter>, recipes: List<RecipeData>): List<Int> {
    val mealEffects = mealEffectsChangingRestDuration(recipes)
    return players.asSequence()
        .map {
            buildPromise {
                EIGHT_HOURS_SECONDS + it.getAppliedMealEffects(mealEffects)
                    .mapNotNull(MealEffect::changeRestDurationSeconds)
                    .sum()
            }
        }
        .toList()
        .awaitAll()
}

suspend fun healsDoubleHp(
    actor: PF2ECharacter,
    recipes: List<RecipeData>,
    campingData: List<CampingActivityData>,
): Boolean {
    val mealEffects = mealEffectsDoublingHealing(recipes)
    val campingEffects = campingEffectsDoublingHealing(campingData)
    return actor.getAppliedMealEffects(mealEffects).isNotEmpty()
            || actor.getAppliedCampingEffects(campingEffects).isNotEmpty()
}

