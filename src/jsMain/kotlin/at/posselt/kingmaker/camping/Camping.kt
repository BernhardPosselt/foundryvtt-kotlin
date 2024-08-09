package at.posselt.kingmaker.camping

import at.posselt.kingmaker.Config
import at.posselt.kingmaker.data.regions.stolenLandsZones
import at.posselt.kingmaker.settings.RegionSetting
import at.posselt.kingmaker.settings.kingmakerTools
import at.posselt.kingmaker.toCamelCase
import at.posselt.kingmaker.utils.findRollTableWithCompendiumFallback
import at.posselt.kingmaker.utils.getAppFlag
import at.posselt.kingmaker.utils.getCombatOverrideTrack
import at.posselt.kingmaker.utils.getKingmakerCombatTrack
import com.foundryvtt.core.Game
import com.foundryvtt.core.utils.deepClone
import com.foundryvtt.pf2e.actor.PF2ENpc


fun getDefaultCamping(game: Game): CampingData {
    return CampingData(
        currentRegion = "Rostland Hinterlands",
        actorUuids = emptyArray(),
        campingActivities = emptyArray(),
        homebrewCampingActivities = emptyArray(),
        lockedActivities = campingActivityData
            .filter(CampingActivityData::isLocked)
            .map(CampingActivityData::name)
            .toTypedArray(),
        cooking = Cooking(
            chosenMeal = "Basic Meal",
            actorMeals = emptyArray(),
            magicalSubsistenceAmount = 0,
            subsistenceAmount = 0,
            knownRecipes = arrayOf("asic Meal", "Hearty Meal"),
            homebrewMeals = emptyArray(),
            cookingSkill = "survival",
            degreeOfSuccess = null,
        ),
        watchSecondsRemaining = 0,
        gunsToClean = 0,
        dailyPrepsAtTime = game.time.worldTime,
        encounterModifier = 0,
        restRollMode = "one",
        increaseWatchActorNumber = 0,
        actorUuidsNotKeepingWatch = emptyArray(),
        ignoreSkillRequirements = false,
    )
}

fun PF2ENpc.getCamping(): CampingData? =
    getAppFlag<PF2ENpc, CampingData?>("camping-sheet")
        ?.let(::deepClone)

fun Game.getCampingActor(): PF2ENpc? =
    actors.contents
        .filterIsInstance<PF2ENpc>()
        .find { it.name == Config.camping.actorName }

fun Game.getCurrentRegionName() =
    getCampingActor()
        ?.getCamping()
        ?.currentRegion


fun CampingData.getAllActivities(): Array<CampingActivityData> =
    campingActivityData + homebrewCampingActivities

fun CampingData.getAllRecipes(): Array<RecipeData> =
    recipes + cooking.homebrewMeals

suspend fun Game.findCurrentRegion(): RegionSetting? {
    val regionName = getCurrentRegionName()
    val regionSettings = settings.kingmakerTools.getRegionSettings()
    return if (regionSettings.useStolenLands) {
        stolenLandsZones
            .find { it.name == regionName }
            ?.let {
                val rolltableUuid = findRollTableWithCompendiumFallback(
                    tableName = "Zone ${it.level.toString().padStart(2, '0')}: ${it.name}",
                    fallbackName = it.name,
                )?.uuid
                val combatTrack = (playlists.getCombatOverrideTrack(it.combatTrackName)
                    ?: playlists.getKingmakerCombatTrack(it.combatTrackName))
                RegionSetting(
                    name = it.name,
                    zoneDc = it.zoneDc,
                    encounterDc = it.encounterDc,
                    level = it.level,
                    rollTableUuid = rolltableUuid,
                    combatTrack = combatTrack,
                    terrain = it.terrain.toCamelCase(),
                )
            }
    } else {
        regionSettings.regions
            .find { it.name == regionName }
    }
}