package at.posselt.pfrpg.camping

import at.posselt.pfrpg.camping.dialogs.RegionSettings
import at.posselt.pfrpg.data.checks.DegreeOfSuccess
import at.posselt.pfrpg.fromCamelCase
import com.foundryvtt.pf2e.actor.PF2EActor
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.js.JsPlainObject

@JsPlainObject
external interface ActorMeal {
    var actorUuid: String
    var favoriteMeal: String?
    var chosenMeal: String
}

@JsPlainObject
external interface Cooking {
    var knownRecipes: Array<String>
    var cookingSkill: String
    var actorMeals: Array<ActorMeal>
    var homebrewMeals: Array<RecipeData>
    var degreeOfSuccess: String?
    var minimumSubsistence: Int
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
    var alwaysPerformActivities: Array<String>
    var huntAndGatherTargetActorUuid: String?
    var proxyRandomEncounterTableUuid: String?
    var randomEncounterRollMode: String
    var ignoreSkillRequirements: Boolean
    var minimumTravelSpeed: Int?
    var regionSettings: RegionSettings
    var section: String
    var restingPlaylistUuid: String?
    var restingPlaylistSoundUuid: String?
}

suspend fun CampingData.getActorsInCamp(): List<PF2EActor> = coroutineScope {
    actorUuids
        .map { async { getCampingActorByUuid(it) } }
        .awaitAll()
        .filterNotNull()
}

fun CampingActivity.parseResult() =
    result?.let { fromCamelCase<DegreeOfSuccess>(it) }

fun CampingActivity.checkPerformed() =
    result != null && actorUuid != null

fun CampingActivity.isPrepareCamp() =
    activity == "Prepare Campsite"