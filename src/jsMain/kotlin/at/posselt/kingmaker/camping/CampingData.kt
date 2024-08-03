package at.posselt.kingmaker.camping

import kotlinx.js.JsPlainObject


@JsPlainObject
external interface ActorMeal {
    val actorUuid: String
    val favoriteMeal: String?
    val chosenMeal: String
}

@JsPlainObject
external interface Cooking {
    val knownRecipes: Array<String>
    val subsistenceAmount: Int
    val magicalSubsistenceAmount: Int
    val chosenMeal: String
    val cookingSkill: String
    val actorMeals: Array<ActorMeal>
    val homebrewMeals: Array<RecipeData>
    val degreeOfSuccess: String?
}

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
}

@JsPlainObject
external interface CampingActivityData {
    val name: String
    val journalUuid: String?
    val skillRequirements: Array<SkillRequirement>
    val dc: Any?
    val skills: Array<String>
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

@JsPlainObject
external interface CampingActivity {
    val activity: String
    val actorUuid: String?
    val result: String?
    val selectedSkill: String?
}

@JsPlainObject
external interface CampingData {
    val currentRegion: String
    val actorUuids: Array<String>
    val campingActivities: Array<CampingActivity>
    val homebrewCampingActivities: Array<CampingActivityData>
    val lockedActivities: Array<String>
    val cooking: Cooking
    val watchSecondsRemaining: Int
    val gunsToClean: Int
    val dailyPrepsAtTime: Int
    val encounterModifier: Int
    val restRollMode: String
    val increaseWatchActorNumber: Int
    val actorUuidsNotKeepingWatch: Array<String>
    val huntAndGatherTargetActorUuid: String?
    val ignoreSkillRequirements: Boolean
}