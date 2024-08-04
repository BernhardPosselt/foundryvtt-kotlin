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