package at.posselt.kingmaker.camping

import at.posselt.kingmaker.utils.awaitAll
import at.posselt.kingmaker.utils.buildPromise
import at.posselt.kingmaker.utils.formatSeconds
import com.foundryvtt.pf2e.actor.PF2EActor
import com.foundryvtt.pf2e.actor.PF2ECharacter

private const val EIGHT_HOURS_SECONDS = 8 * 60 * 60

suspend fun getRestSecondsPerPlayer(
    players: List<PF2EActor>,
    recipes: List<RecipeData>,
    increaseActorsKeepingWatch: Int = 0,
): List<Int> {
    val mealEffects = mealEffectsChangingRestDuration(recipes)
    val durationPerPlayer = players.asSequence()
        .map {
            buildPromise {
                EIGHT_HOURS_SECONDS + it.getAppliedMealEffects(mealEffects)
                    .mapNotNull(MealEffect::changeRestDurationSeconds)
                    .sum()
            }
        }
        .toList()
        .awaitAll()
    val additionalWatchers = generateSequence { EIGHT_HOURS_SECONDS }
        .take(increaseActorsKeepingWatch)
        .toList()
    return durationPerPlayer + additionalWatchers
}

suspend fun getFullRestSeconds(
    watchers: List<PF2EActor>,
    recipes: List<RecipeData>,
    gunsToClean: Int,
    increaseActorsKeepingWatch: Int,
): Int = calculateRestDurationSeconds(getRestSecondsPerPlayer(watchers, recipes, increaseActorsKeepingWatch)) +
        calculateDailyPreparationSeconds(gunsToClean)

suspend fun getFullRestDuration(
    watchers: List<PF2EActor>,
    recipes: List<RecipeData>,
    gunsToClean: Int,
    increaseActorsKeepingWatch: Int = 0,
) = formatSeconds(
    getFullRestSeconds(
        watchers = watchers,
        recipes = recipes,
        gunsToClean = gunsToClean,
        increaseActorsKeepingWatch = increaseActorsKeepingWatch
    )
)

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

