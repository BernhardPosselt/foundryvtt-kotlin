package at.posselt.kingmaker.camping

import at.posselt.kingmaker.actor.getLoreAttributes
import at.posselt.kingmaker.data.actor.Attribute
import at.posselt.kingmaker.data.actor.Perception
import at.posselt.kingmaker.data.actor.Proficiency
import at.posselt.kingmaker.data.actor.Skill
import at.posselt.kingmaker.data.checks.DegreeOfSuccess
import at.posselt.kingmaker.fromCamelCase
import com.foundryvtt.pf2e.actor.PF2ECreature
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

data class ProficiencyRequirement(
    val attribute: Attribute,
    val proficiency: Proficiency,
)

data class ActivityAndData(
    val data: CampingActivityData,
    val result: CampingActivity,
) {
    fun done(): Boolean {
        return (data.doesNotRequireACheck() && result.actorUuid != null) || result.checkPerformed()
    }

    fun getSkills(actor: PF2ECreature?): List<ProficiencyRequirement>? {
        val skillAndProficiency = data.skillRequirements.associate {
            it.skill to fromCamelCase<Proficiency>(it.proficiency)
        }
        val lores: List<Attribute> = actor?.getLoreAttributes()
            ?: data.getLoreSkills().map { Attribute.fromString(it) }
        val allSkills = (Skill.entries + lores + Perception).map {
            ProficiencyRequirement(
                attribute = it,
                proficiency = skillAndProficiency[it.value] ?: Proficiency.UNTRAINED
            )
        }
        return if (data.skills == "any") {
            allSkills
        } else {
            val sk = data.skills.unsafeCast<Array<String>>().toSet()
            if (sk.isEmpty()) {
                null
            } else {
                allSkills.filter { sk.contains(it.attribute.value) }
            }
        }
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

fun CampingActivityData.getLoreSkills(): List<String> {
    if (skills == "any") {
        return emptyList()
    } else {
        val commonSkills = (Skill.entries + Perception).map { it.value }.toSet()
        return skills.unsafeCast<Array<String>>().filter { !commonSkills.contains(it) }
    }
}

fun CampingActivityData.doesNotRequireACheck(): Boolean =
    !requiresACheck()

fun CampingActivityData.requiresACheck(): Boolean =
    skills == "any" || (skills as Array<*>).isNotEmpty()

@JsModule("./data/camping-activities.json")
external val campingActivityData: Array<CampingActivityData>