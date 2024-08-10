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
    var knownRecipes: Array<String>
    var subsistenceAmount: Int
    var magicalSubsistenceAmount: Int
    var chosenMeal: String
    var cookingSkill: String
    var actorMeals: Array<ActorMeal>
    var homebrewMeals: Array<RecipeData>
    var degreeOfSuccess: String?
}

@JsPlainObject
external interface CampingActivity {
    var activity: String
    var actorUuid: String?
    var result: String?
    var selectedSkill: String?
}

@JsPlainObject
external interface CampingData {
    var currentRegion: String
    var actorUuids: Array<String>
    var campingActivities: Array<CampingActivity>
    var homebrewCampingActivities: Array<CampingActivityData>
    var lockedActivities: Array<String>
    var cooking: Cooking
    var watchSecondsRemaining: Int
    var gunsToClean: Int
    var dailyPrepsAtTime: Int
    var encounterModifier: Int
    var restRollMode: String
    var increaseWatchActorNumber: Int
    var actorUuidsNotKeepingWatch: Array<String>
    var huntAndGatherTargetActorUuid: String?
    var proxyRandomEncounterTableUuid: String?
    var randomEncounterRollMode: String?
    var ignoreSkillRequirements: Boolean
    var increaseTravelSpeedByFeet: Int?
}