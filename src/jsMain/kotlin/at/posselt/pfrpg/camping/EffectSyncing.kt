package at.posselt.pfrpg.camping

import at.posselt.pfrpg.Config
import at.posselt.pfrpg.actions.ActionDispatcher
import at.posselt.pfrpg.actions.ActionMessage
import at.posselt.pfrpg.actions.handlers.SyncActivitiesAction
import at.posselt.pfrpg.camping.dialogs.ActivityEffectTarget
import at.posselt.pfrpg.data.checks.DegreeOfSuccess
import at.posselt.pfrpg.fromCamelCase
import at.posselt.pfrpg.takeIfInstance
import at.posselt.pfrpg.toCamelCase
import at.posselt.pfrpg.utils.buildPromise
import at.posselt.pfrpg.utils.fromUuidTypeSafe
import com.foundryvtt.core.Actor
import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.Hooks
import com.foundryvtt.core.onPreUpdateActor
import com.foundryvtt.core.utils.getProperty
import com.foundryvtt.core.utils.setProperty
import com.foundryvtt.pf2e.actor.PF2EActor
import com.foundryvtt.pf2e.actor.PF2ENpc
import com.foundryvtt.pf2e.item.PF2EEffect
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class SyncActivities(
    val rollRandomEncounter: Boolean,
    val activities: Array<CampingActivity>,
)

private data class ChangeEvent(
    val previous: CampingActivity? = null,
    val new: CampingActivity,
    val resultChanged: Boolean,
    val data: CampingActivityData,
    val rollRandomEncounter: Boolean,
)

private val listenForAttributeChanges = setOf(
    "flags.${Config.moduleId}.camping-sheet.campingActivities",
    "flags.${Config.moduleId}.camping-sheet.homebrewCampingActivities",
    "flags.${Config.moduleId}.camping-sheet.alwaysPerformActivities",
)

private fun relevantUpdate(actor: Actor, update: AnyObject): Boolean {
//    val changes = diffObject(camping.unsafeCast<AnyObject>(), update)
    return true;
}

fun checkPreActorUpdate(actor: Actor, update: AnyObject): SyncActivities? {
    val camping = actor.takeIfInstance<PF2ENpc>()?.getCamping() ?: return null
    console.log("Received camping update", update)
    if (!relevantUpdate(actor, update)) return null
    val settingsChanged = false // TODO: fix this
    val activities = getProperty(update, "flags.${Config.moduleId}.camping-sheet.campingActivities")
        ?.unsafeCast<Array<CampingActivity>>()
        ?: camping.campingActivities
    val activitiesByName = camping.campingActivities.associateBy { it.activity }
    val activityDataByName = camping.getAllActivities().associateBy { it.name }
    val activityStateChanged = getActivityChanges(
        activities,
        activityDataByName,
        activitiesByName
    )
    val needsSync = settingsChanged
            || activityStateChanged.isNotEmpty()
            || camping.campingActivities.size != activities.size
    if (!needsSync) return null

    val rollRandomEncounter = activityStateChanged.any { it.resultChanged && it.rollRandomEncounter }

    val skipCamping = skipCamping(activityStateChanged)
    val activitiesToSync = if (skipCamping) {
        emptyArray()
    } else {
        camping.alwaysPerformActivities
            .map {
                CampingActivity(
                    activity = it,
                    actorUuid = null
                )
            }
            .toTypedArray() + activities
    }

    if (skipCamping) {
        setProperty(
            update,
            "flags.${Config.moduleId}.camping-sheet.section",
            CampingSheetSection.PREPARE_CAMPSITE.toCamelCase()
        )
    }

    return SyncActivities(
        rollRandomEncounter = rollRandomEncounter,
        activities = activitiesToSync,
    )
}

private fun getActivityChanges(
    activities: Array<CampingActivity>,
    activityDataByName: Map<String, CampingActivityData>,
    activitiesByName: Map<String, CampingActivity>,
): List<ChangeEvent> {
    return activities.mapNotNull { new ->
        val data = activityDataByName[new.activity]
        val previous = activitiesByName[new.activity]
        val hasDifferentResult = previous != null && (new.result != previous.result)
        val hasDifferentActor = previous != null && (new.actorUuid != previous.actorUuid)
        if (data != null && (hasDifferentActor || hasDifferentResult)) {
            val rollRandomEncounter = new.parseResult()
                ?.let { data.getOutcome(it) }
                ?.checkRandomEncounter == true
            ChangeEvent(
                previous = previous,
                new = new,
                data = data,
                resultChanged = hasDifferentResult,
                rollRandomEncounter = hasDifferentResult && rollRandomEncounter
            )
        } else {
            null
        }
    }
}

private fun skipCamping(activityStateChanged: List<ChangeEvent>): Boolean {
    val prepareCampsite = activityStateChanged
        .map { it.new }
        .find { it.isPrepareCamp() }
    val prepareCampsiteResult = prepareCampsite?.parseResult()
    return prepareCampsiteResult == null || prepareCampsiteResult == DegreeOfSuccess.CRITICAL_FAILURE
}

fun registerEffectSyncingHooks(dispatcher: ActionDispatcher) {
    Hooks.onPreUpdateActor { actor, update, _, _ ->
        buildPromise {
            checkPreActorUpdate(actor, update)?.let {
                dispatcher.dispatch(
                    ActionMessage(
                        action = "syncActivities",
                        data = SyncActivitiesAction(
                            rollRandomEncounter = it.rollRandomEncounter,
                            activities = it.activities,
                        ).unsafeCast<AnyObject>()
                    )
                )
            }
        }
    }
}

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