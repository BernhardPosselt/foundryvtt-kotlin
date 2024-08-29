package at.posselt.kingmaker.camping

import at.posselt.kingmaker.Config
import at.posselt.kingmaker.data.checks.DegreeOfSuccess
import at.posselt.kingmaker.takeIfInstance
import at.posselt.kingmaker.utils.fromUuidTypeSafe
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

private fun getCampingEffectUuids(effectUuids: Array<ActivityEffect>?): Sequence<String> =
    effectUuids?.let {
        it.asSequence().map { it.uuid }
    } ?: emptySequence()

private suspend fun CampingData.getCampingEffectItems(): List<PF2EEffect> = coroutineScope {
    getAllActivities().asSequence()
        .flatMap {
            getCampingEffectUuids(it.effectUuids) +
                    getCampingEffectUuids(it.criticalSuccess?.effectUuids) +
                    getCampingEffectUuids(it.success?.effectUuids) +
                    getCampingEffectUuids(it.failure?.effectUuids) +
                    getCampingEffectUuids(it.criticalFailure?.effectUuids)
        }
        .map { async { fromUuidTypeSafe<PF2EEffect>(it) } }
        .toList()
        .awaitAll()
        .filterNotNull()
}

private fun PF2EActor.findCampingEffectsInInventory(compendiumItems: List<PF2EEffect>): List<PF2EEffect> {
    val slugs = compendiumItems.map { it.slug }.toSet()
    return itemTypes.effect.filter { it.slug in slugs }
}

// TODO: effects have a target and need to be filtered by actor
private fun CampingData.findActiveEffects(
    activities: List<CampingActivity>,
    compendiumItems: List<PF2EEffect>,
): List<PF2EEffect> {
    val activityDataByName = getAllActivities().associateBy { it.name }
    val itemsByUuid = compendiumItems.associateBy { it.uuid }
    return activities
        .asSequence()
        .mapNotNull {
            activityDataByName[it.activity]?.let { data ->
                val effectUuids = data.effectUuids?.map { it.uuid }?.toTypedArray() ?: emptyArray()
                val resultUuids = if (data.requiresACheck()) {
                    it.parseResult()
                        ?.let { data.getOutcome(it) }
                        ?.effectUuids
                        ?.map { it.uuid }
                        ?.toTypedArray()
                        ?: emptyArray()
                } else {
                    emptyArray()
                }
                resultUuids + effectUuids
            }
        }
        .flatMap { it.asSequence() }
        .mapNotNull { itemsByUuid[it] }
        .toList()
}