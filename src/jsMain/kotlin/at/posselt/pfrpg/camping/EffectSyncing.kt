package at.posselt.pfrpg.camping

import at.posselt.pfrpg.Config
import at.posselt.pfrpg.camping.dialogs.ActivityEffectTarget
import at.posselt.pfrpg.data.checks.DegreeOfSuccess
import at.posselt.pfrpg.fromCamelCase
import at.posselt.pfrpg.takeIfInstance
import at.posselt.pfrpg.utils.fromUuidTypeSafe
import com.foundryvtt.core.Actor
import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.utils.getProperty
import com.foundryvtt.pf2e.actor.PF2EActor
import com.foundryvtt.pf2e.actor.PF2ENpc
import com.foundryvtt.pf2e.item.PF2EEffect
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

sealed interface CampingCommand {
    class ClearActivities() : CampingCommand
    class SkipActivities(val rollRandomEncounter: Boolean) : CampingCommand
    class SyncActivities(val rollRandomEncounter: Boolean) : CampingCommand
    class DoNothing() : CampingCommand
}

private data class ChangeEvent(
    val previous: CampingActivity? = null,
    val new: CampingActivity,
    val resultChanged: Boolean,
    val actorChanged: Boolean,
    val data: CampingActivityData,
    val rollRandomEncounter: Boolean,
)

fun checkPreActorUpdate(actor: Actor, update: AnyObject): CampingCommand =
    actor.takeIfInstance<PF2ENpc>()
        ?.getCamping()
        ?.let { camping ->
            val activities = getProperty(update, "flags.${Config.moduleId}.camping-sheet.campingActivities")
                ?.unsafeCast<Array<CampingActivity>>()
            if (activities == null) return CampingCommand.DoNothing()
            val activitiesByName = camping.campingActivities.associateBy { it.activity }
            val activityStateChanged = activities.mapNotNull { new ->
                val data = camping.getAllActivities().find { it.name == new.activity }
                val previous = activitiesByName[new.activity]
                val hasDifferentResult = new.result != previous?.result
                val hasDifferentActor = new.actorUuid != previous?.actorUuid
                if (data != null && (hasDifferentActor || hasDifferentResult)) {
                    val rollRandomEncounter = new.parseResult()
                        ?.let { data.getOutcome(it) }
                        ?.checkRandomEncounter == true
                    ChangeEvent(
                        previous = previous,
                        new = new,
                        data = data,
                        resultChanged = hasDifferentResult,
                        actorChanged = hasDifferentActor,
                        rollRandomEncounter = hasDifferentResult && rollRandomEncounter
                    )
                } else {
                    null
                }
            }
            val needsSync = activityStateChanged.isNotEmpty() || camping.campingActivities.size != activities.size
            val rollRandomEncounter = activityStateChanged.any { it.resultChanged && it.rollRandomEncounter }

            val prepareCampsite = activityStateChanged
                .map { it.new }
                .find { it.isPrepareCamp() }
            val clearActivities = prepareCampsite != null && prepareCampsite.result == null
            val skipActivities = prepareCampsite?.parseResult() == DegreeOfSuccess.CRITICAL_FAILURE

            return if (needsSync && clearActivities) {
                CampingCommand.ClearActivities()
            } else if (needsSync && skipActivities) {
                CampingCommand.SkipActivities(rollRandomEncounter)
            } else if (needsSync) {
                CampingCommand.SyncActivities(rollRandomEncounter)
            } else {
                CampingCommand.DoNothing()
            }
        } ?: CampingCommand.DoNothing()

private fun getCampingEffectUuids(effectUuids: Array<ActivityEffect>?): Sequence<Pair<String, String>> =
    effectUuids?.let {
        it.asSequence().map { it.uuid to (it.target ?: "all") }
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

private suspend fun CampingData.syncCampingEffects() = coroutineScope {
    val actors = getActorsInCamp()
    val effectsThatShouldBePresent = null
    // TODO:
    //  * check which effects should be active on each actor (also check alwaysPerformActivities)
    //  * check which effects are present on each actor
    //  * remove effects that are not present on active actors
    //  * add effects which are not present on actors
}

private suspend fun CampingData.clearCampingEffects() = coroutineScope {
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