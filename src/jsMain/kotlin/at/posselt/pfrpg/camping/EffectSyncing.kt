package at.posselt.pfrpg.camping

import at.posselt.pfrpg.camping.dialogs.ActivityEffectTarget
import at.posselt.pfrpg.data.checks.DegreeOfSuccess
import at.posselt.pfrpg.fromCamelCase
import at.posselt.pfrpg.utils.fromUuidTypeSafe
import com.foundryvtt.pf2e.actor.PF2EActor
import com.foundryvtt.pf2e.item.PF2EEffect
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope


private fun getCampingEffectUuids(effectUuids: Array<ActivityEffect>?): Sequence<Pair<String, String>> =
    effectUuids?.let { effects ->
        effects.asSequence().map { it.uuid to (it.target ?: "all") }
    } ?: emptySequence()

private data class EffectAndTarget(
    val effect: PF2EEffect,
    val target: ActivityEffectTarget,
)

private suspend fun CampingData.getAllCampingEffectItems() =
    (getCampingEffectItems(DegreeOfSuccess.CRITICAL_FAILURE) +
            getCampingEffectItems(DegreeOfSuccess.FAILURE) +
            getCampingEffectItems(DegreeOfSuccess.SUCCESS) +
            getCampingEffectItems(DegreeOfSuccess.CRITICAL_SUCCESS))
        .distinctBy { it.effect.slug }

private suspend fun CampingData.getCampingEffectItems(
    degreeOfSuccess: DegreeOfSuccess? = null
): List<EffectAndTarget> = coroutineScope {
    getAllActivities().asSequence()
        .flatMap {
            val defaultEffects = getCampingEffectUuids(it.effectUuids)
            val degreeEffects = when (degreeOfSuccess) {
                DegreeOfSuccess.CRITICAL_FAILURE -> getCampingEffectUuids(it.criticalFailure?.effectUuids)
                DegreeOfSuccess.FAILURE -> getCampingEffectUuids(it.failure?.effectUuids)
                DegreeOfSuccess.SUCCESS -> getCampingEffectUuids(it.success?.effectUuids)
                DegreeOfSuccess.CRITICAL_SUCCESS -> getCampingEffectUuids(it.criticalSuccess?.effectUuids)
                null -> emptySequence()
            }
            defaultEffects + degreeEffects
        }
        .map { async { fromUuidTypeSafe<PF2EEffect>(it.first) to fromCamelCase<ActivityEffectTarget>(it.second) } }
        .toList()
        .awaitAll()
        .mapNotNull {
            val effect = it.first
            val target = it.second
            if (effect == null || target == null) {
                null
            } else {
                EffectAndTarget(effect = effect, target = target)
            }
        }
}

private fun PF2EActor.findCampingEffectsInInventory(compendiumItems: List<PF2EEffect>): List<PF2EEffect> {
    val slugs = compendiumItems.map { it.slug }.toSet()
    return itemTypes.effect.filter { it.slug in slugs }
}

suspend fun CampingData.syncCampingEffects(activities: Array<CampingActivity>) = coroutineScope {
    val actors = getActorsInCamp()
    val effectsThatShouldBePresent = null
    console.log(activities)
    // TODO:
    //  * check which effects should be active on each actor (also check alwaysPerformActivities)
    //  * check which effects are present on each actor
    //  * remove effects that are not present on active actors
    //  * add effects which are not present on actors
}

suspend fun CampingData.clearCampingEffects() = coroutineScope {
    val actors = getActorsInCamp()
    val campingEffectSlugs = getCampingEffectItems().map { it.effect.slug }.toSet()
    actors
        .map { actor ->
            val idsToRemove = actor.itemTypes.effect
                .filter { it.slug in campingEffectSlugs }
                .mapNotNull { it.id }
                .toTypedArray()
            actor to idsToRemove
        }
        .map {
            async {
                it.first.deleteEmbeddedDocuments<PF2EEffect>("item", it.second)
            }
        }
        .awaitAll()
}