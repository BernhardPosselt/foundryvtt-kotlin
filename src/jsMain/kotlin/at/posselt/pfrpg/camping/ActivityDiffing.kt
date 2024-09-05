package at.posselt.pfrpg.camping

import at.posselt.pfrpg.Config
import at.posselt.pfrpg.actions.ActionDispatcher
import at.posselt.pfrpg.actions.ActionMessage
import at.posselt.pfrpg.actions.handlers.SyncActivitiesAction
import at.posselt.pfrpg.data.checks.DegreeOfSuccess
import at.posselt.pfrpg.takeIfInstance
import at.posselt.pfrpg.toCamelCase
import at.posselt.pfrpg.utils.asAnyObjectList
import at.posselt.pfrpg.utils.buildPromise
import com.foundryvtt.core.Actor
import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.Hooks
import com.foundryvtt.core.onPreUpdateActor
import com.foundryvtt.core.utils.diffObject
import com.foundryvtt.core.utils.getProperty
import com.foundryvtt.core.utils.setProperty
import com.foundryvtt.pf2e.actor.PF2ENpc
import js.objects.Object


class SyncActivities(
    val rollRandomEncounter: Boolean,
    val activities: Array<CampingActivity>,
)

private data class ActivityChange(
    val previous: CampingActivity? = null,
    val new: CampingActivity,
    val resultChanged: Boolean,
    val data: CampingActivityData,
    val rollRandomEncounter: Boolean,
)

private const val homebrewPath = "flags.${Config.moduleId}.camping-sheet.homebrewCampingActivities"
private const val alwaysPerformPath = "flags.${Config.moduleId}.camping-sheet.alwaysPerformActivities"
private const val campingActivitiesPath = "flags.${Config.moduleId}.camping-sheet.campingActivities"

private val settingAttributes = setOf(
    homebrewPath,
    alwaysPerformPath,
)

private val listenForAttributeChanges = mapOf(
    campingActivitiesPath to ::campingActivitiesChanged,
    homebrewPath to ::homebrewCampingActivitiesChanged,
    alwaysPerformPath to ::alwaysPerformActivitiesChanged,
)

private fun doObjectArraysDiffer(source: List<AnyObject>, target: List<AnyObject>): Boolean {
    return source.size != target.size ||
            (source.asSequence() zip target.asSequence())
                .any { (first, second) ->
                    Object.keys(diffObject(first, second)).isNotEmpty()
                }
}

private fun homebrewCampingActivitiesChanged(camping: CampingData, update: Any): Boolean {
    val current = camping.homebrewCampingActivities.sortedBy { it.name }
    val updateList = update.unsafeCast<Array<CampingActivityData>>().sortedBy { it.name }
    return doObjectArraysDiffer(current.asAnyObjectList(), updateList.asAnyObjectList())
}

private fun campingActivitiesChanged(camping: CampingData, update: Any): Boolean {
    val current = camping.campingActivities.sortedBy { it.activity }
    val updateList = update.unsafeCast<Array<CampingActivity>>().sortedBy { it.activity }
    return doObjectArraysDiffer(current.asAnyObjectList(), updateList.asAnyObjectList())
}

private fun alwaysPerformActivitiesChanged(camping: CampingData, update: Any): Boolean {
    val current = camping.alwaysPerformActivities.sorted()
    val updateList = update.unsafeCast<Array<String>>().sorted()
    return current != updateList
}

private fun relevantUpdate(camping: CampingData, update: AnyObject): Set<String> {
    return listenForAttributeChanges
        .mapNotNull { (key, entry) ->
            val updatedProperty = getProperty(update, key)
            updatedProperty?.let {
                if (entry(camping, updatedProperty)) {
                    key
                } else {
                    null
                }
            }
        }.toSet()
}

fun checkPreActorUpdate(actor: Actor, update: AnyObject): SyncActivities? {
    val camping = actor.takeIfInstance<PF2ENpc>()?.getCamping() ?: return null
    console.log("Received camping update", update)
    val updates = relevantUpdate(camping, update)
    if (updates.isEmpty()) return null
    val settingsChanged = updates.intersect(settingAttributes).isNotEmpty()
    val activities = getProperty(update, campingActivitiesPath)
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

    val prepareCampsiteResult = checkPrepareCampsiteResult(activityStateChanged)
    setSection(prepareCampsiteResult, camping, update)
    return SyncActivities(
        rollRandomEncounter = activityStateChanged.any { it.resultChanged && it.rollRandomEncounter },
        activities = getActivitiesToSync(prepareCampsiteResult, camping, activities),
    )
}

private fun getActivitiesToSync(
    prepareCampsiteResult: PrepareCampsiteResult?,
    camping: CampingData,
    activities: Array<CampingActivity>
) = if (prepareCampsiteResult == PrepareCampsiteResult.SKIP_CAMPING) {
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

private fun setSection(
    prepareCampsiteResult: PrepareCampsiteResult?,
    camping: CampingData,
    update: AnyObject
) {
    val section = when (prepareCampsiteResult) {
        PrepareCampsiteResult.SKIP_CAMPING -> CampingSheetSection.PREPARE_CAMPSITE.toCamelCase()
        PrepareCampsiteResult.CAMPING_ACTIVITIES -> CampingSheetSection.CAMPING_ACTIVITIES.toCamelCase()
        null -> camping.section
    }
    setProperty(
        update,
        "flags.${Config.moduleId}.camping-sheet.section",
        section,
    )
}

private fun getActivityChanges(
    activities: Array<CampingActivity>,
    activityDataByName: Map<String, CampingActivityData>,
    activitiesByName: Map<String, CampingActivity>,
): List<ActivityChange> {
    return activities.mapNotNull { new ->
        val data = activityDataByName[new.activity]
        val previous = activitiesByName[new.activity]
        val hasDifferentResult = previous != null && (new.result != previous.result)
        val hasDifferentActor = previous != null && (new.actorUuid != previous.actorUuid)
        if (data != null && (hasDifferentActor || hasDifferentResult)) {
            val rollRandomEncounter = new.parseResult()
                ?.let { data.getOutcome(it) }
                ?.checkRandomEncounter == true
            ActivityChange(
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

private enum class PrepareCampsiteResult {
    SKIP_CAMPING,
    CAMPING_ACTIVITIES,
}

private fun checkPrepareCampsiteResult(activityStateChanged: List<ActivityChange>): PrepareCampsiteResult? {
    val prepareCampsite = activityStateChanged
        .map { it.new }
        .find { it.isPrepareCamp() } ?: return null
    val prepareCampsiteResult = prepareCampsite.parseResult()
    return if (prepareCampsiteResult == null || prepareCampsiteResult == DegreeOfSuccess.CRITICAL_FAILURE) {
        PrepareCampsiteResult.SKIP_CAMPING
    } else {
        PrepareCampsiteResult.CAMPING_ACTIVITIES
    }
}

fun registerActivityDiffingHooks(dispatcher: ActionDispatcher) {
    Hooks.onPreUpdateActor { actor, update, _, _ ->
        checkPreActorUpdate(actor, update)?.let {
            buildPromise {
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