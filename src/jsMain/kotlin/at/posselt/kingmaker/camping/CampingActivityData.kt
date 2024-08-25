package at.posselt.kingmaker.camping

import at.posselt.kingmaker.actor.getLoreAttributes
import at.posselt.kingmaker.camping.getLoreSkills
import at.posselt.kingmaker.data.actor.*
import at.posselt.kingmaker.data.actor.Attribute
import at.posselt.kingmaker.data.checks.DegreeOfSuccess
import at.posselt.kingmaker.fromCamelCase
import com.foundryvtt.pf2e.actor.PF2ECreature
import kotlinx.js.JsPlainObject
import kotlin.collections.associateBy
import kotlin.collections.find


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
external interface ActivityEffect {
    val uuid: String
    val target: String?
    val doublesHealing: Boolean?
}

@JsPlainObject
external interface CampingSkill {
    val name: String
    val proficiency: String
    val dcType: String // zone, actorLevel , static, none
    val dc: Int?
    val validateOnly: Boolean?
    val required: Boolean?
}

@JsPlainObject
external interface CampingActivityData {
    val name: String
    val journalUuid: String?
    val skills: Array<CampingSkill>?
    val modifyRandomEncounterDc: ModifyEncounterDc?
    val isSecret: Boolean
    val isLocked: Boolean
    val effectUuids: Array<ActivityEffect>?
    val isHomebrew: Boolean
    val criticalSuccess: ActivityOutcome?
    val success: ActivityOutcome?
    val failure: ActivityOutcome?
    val criticalFailure: ActivityOutcome?
}

fun CampingActivityData.isPrepareCamp() =
    name == "Prepare Campsite"

fun CampingActivityData.isHuntAndGather() =
    name == "Hunt and Gather"

fun CampingActivityData.isDiscoverSpecialMeal() =
    name == "Discover Special Meal"

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

enum class DcType {
    ACTOR_LEVEL,
    ZONE,
    NONE,
    STATIC
}

data class ParsedCampingSkill(
    val attribute: Attribute,
    val proficiency: Proficiency,
    val dcType: DcType,
    val dc: Int?,
    val validateOnly: Boolean,
    val required: Boolean,
)

data class ActivityAndData(
    val data: CampingActivityData,
    val result: CampingActivity,
) {
    fun done(): Boolean {
        return (data.doesNotRequireACheck() && result.actorUuid != null) || result.checkPerformed()
    }

    fun isPrepareCamp() = data.isPrepareCamp()

    fun isNotPrepareCamp() = !isPrepareCamp()
}

fun CampingActivityData.getCampingSkills(actor: PF2ECreature? = null): List<ParsedCampingSkill>? {
    val theSkills = skills
    if (theSkills == null) return null
    // if an actor exists, fetch all lore skills for the dropdown, otherwise go
    // with the one on the activity
    val lores: List<Attribute> = actor?.getLoreAttributes()
        ?: getLoreSkills()
    val allAttributes = Skill.entries + lores + Perception
    val anySkill = theSkills.find { it.name == "any" }
    return if (anySkill != null) {
        allAttributes.map {
            ParsedCampingSkill(
                attribute = it,
                proficiency = fromCamelCase<Proficiency>(anySkill.proficiency) ?: Proficiency.UNTRAINED,
                dcType = fromCamelCase<DcType>(anySkill.dcType) ?: DcType.NONE,
                dc = anySkill.dc,
                validateOnly = anySkill.validateOnly == true,
                required = false,
            )
        }
    } else {
        val activitySkills = theSkills.associateBy { Attribute.fromString(it.name) }
        allAttributes.mapNotNull { attribute ->
            activitySkills[attribute]?.let { skill ->
                ParsedCampingSkill(
                    attribute = attribute,
                    proficiency = fromCamelCase<Proficiency>(skill.proficiency) ?: Proficiency.UNTRAINED,
                    dcType = fromCamelCase<DcType>(skill.dcType) ?: DcType.NONE,
                    dc = skill.dc,
                    validateOnly = skill.validateOnly == true,
                    required = skill.required == true,
                )
            }
        }
    }
}

fun CampingData.groupActivities(): List<ActivityAndData> {
    val activitiesByName = campingActivities.associateBy { it.activity }
    return getAllActivities().map { data ->
        val activity = activitiesByName[data.name] ?: CampingActivity(
            activity = data.name,
            actorUuid = null,
            result = null,
            selectedSkill = null,
        )
        ActivityAndData(data = data, result = activity)
    }
}

fun CampingActivityData.getLoreSkills(): List<Lore> =
    if (skills?.any { it.name == "any" } != false) {
        emptyList()
    } else {
        skills
            ?.map { Attribute.fromString(it.name) }
            ?.filterIsInstance<Lore>()
            ?: emptyList()
    }

fun CampingActivityData.doesNotRequireACheck(): Boolean =
    !requiresACheck()

fun CampingActivityData.requiresACheck(): Boolean =
    skills?.filter { it.validateOnly != true }?.isNotEmpty() == true

@JsModule("./data/camping-activities.json")
external val campingActivityData: Array<CampingActivityData>