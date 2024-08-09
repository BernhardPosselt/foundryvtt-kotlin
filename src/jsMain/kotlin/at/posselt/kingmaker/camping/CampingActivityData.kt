package at.posselt.kingmaker.camping

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

@JsModule("./data/camping-activities.json")
external val campingActivityData: Array<CampingActivityData>