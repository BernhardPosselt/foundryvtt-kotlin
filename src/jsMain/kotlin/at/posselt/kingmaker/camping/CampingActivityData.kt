package at.posselt.kingmaker.camping

import at.posselt.kingmaker.data.checks.DegreeOfSuccess
import kotlinx.js.JsPlainObject


@JsPlainObject
external interface ActivityOutcome {
    val message: String
    val effectUuids: Array<ActivityEffect>?
    val modifyRandomEncounterDc: ModifyEncounterDc?
    val checkRandomEncounter: Boolean?
}

@JsPlainObject
external interface ModifyEncounterDc {
    val day: Int
    val night: Int
}

@JsPlainObject
external interface SkillRequirement {
    val skill: String
    val proficiency: String
}

@JsPlainObject
external interface ActivityEffect {
    val uuid: String
    val target: String?
    val doublesHealing: Boolean?
}

@JsPlainObject
external interface CampingActivityData {
    val name: String
    val journalUuid: String?
    val skillRequirements: Array<SkillRequirement>
    val dc: Any?  // zone, actorLevel or a number
    val skills: Any // array of strings or any
    val modifyRandomEncounterDc: ModifyEncounterDc?
    val isSecret: Boolean
    val isLocked: Boolean
    val effectUuids: Array<ActivityEffect>?
    val isHomebrew: Boolean?
    val criticalSuccess: ActivityOutcome?
    val success: ActivityOutcome?
    val failure: ActivityOutcome?
    val criticalFailure: ActivityOutcome?
}

fun CampingActivityData.isPrepareCamp() =
    name == "Prepare Camp"

fun CampingActivityData.getOutcome(degreeOfSuccess: DegreeOfSuccess) =
    when (degreeOfSuccess) {
        DegreeOfSuccess.CRITICAL_FAILURE -> criticalFailure
        DegreeOfSuccess.FAILURE -> failure
        DegreeOfSuccess.SUCCESS -> success
        DegreeOfSuccess.CRITICAL_SUCCESS -> criticalSuccess
    }

fun ModifyEncounterDc.atTime(isDay: Boolean) =
    if (isDay) {
        day
    } else {
        night
    }

data class ActivityAndData(
    val data: CampingActivityData,
    val result: CampingActivity,
) {
    fun done(): Boolean {
        return (data.doesNotRequireACheck() && result.actorUuid != null) || result.checkPerformed()
    }
}

fun CampingData.groupActivities(): List<ActivityAndData> {
    val activitiesByName = getAllActivities().associateBy { it.name }
    return campingActivities.mapNotNull { activity ->
        val data = activitiesByName[activity.activity]
        if (data == null) {
            null
        } else {
            ActivityAndData(data = data, result = activity)
        }
    }
}

fun CampingActivityData.doesNotRequireACheck(): Boolean =
    !(skills == "any" || (skills as Array<*>).isNotEmpty())

@JsModule("./data/camping-activities.json")
external val campingActivityData: Array<CampingActivityData>