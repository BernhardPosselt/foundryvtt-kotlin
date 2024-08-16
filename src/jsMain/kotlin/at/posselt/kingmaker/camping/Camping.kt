package at.posselt.kingmaker.camping

import at.posselt.kingmaker.Config
import at.posselt.kingmaker.camping.dialogs.RegionSetting
import at.posselt.kingmaker.camping.dialogs.RegionSettings
import at.posselt.kingmaker.data.regions.stolenLandsZones
import at.posselt.kingmaker.toCamelCase
import at.posselt.kingmaker.utils.*
import com.foundryvtt.core.Game
import com.foundryvtt.core.utils.deepClone
import com.foundryvtt.pf2e.actor.PF2ENpc


fun getDefaultCamping(game: Game): CampingData {
    return CampingData(
        currentRegion = Config.regions.defaultRegion,
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
        randomEncounterRollMode = "gmroll",
        regionSettings = RegionSettings(
            useStolenLands = true,
            regions = emptyArray()
        )
    )
}

fun PF2ENpc.getCamping(): CampingData? =
    getAppFlag<PF2ENpc, CampingData?>("camping-sheet")
        ?.let(::deepClone)

suspend fun PF2ENpc.setCamping(data: CampingData) {
    setAppFlag("camping-sheet", data)
}

fun Game.getCampingActor(): PF2ENpc? =
    actors.contents
        .filterIsInstance<PF2ENpc>()
        .find { it.getCamping() != null }

fun CampingData.getAllActivities(): Array<CampingActivityData> {
    val homebrewNames = homebrewCampingActivities.map { it.name }.toSet()
    return campingActivityData
        .filter { it.name !in homebrewNames }
        .toTypedArray() + homebrewCampingActivities
}

fun CampingData.getAllRecipes(): Array<RecipeData> {
    val homebrewNames = cooking.homebrewMeals.map { it.name }.toSet()
    return recipes
        .filter { it.name !in homebrewNames }
        .toTypedArray() + cooking.homebrewMeals
}

suspend fun CampingData.getRegions(game: Game): List<RegionSetting> {
    return if (regionSettings.useStolenLands) {
        stolenLandsZones
            .map {
                buildPromise {
                    val tableName = "Zone ${it.level.toString().padStart(2, '0')}: ${it.name}"
                    val rolltableUuid = game.findRollTableWithCompendiumFallback(
                        tableName = tableName,
                        fallbackName = it.name,
                    )?.uuid
                    val combatTrack = (game.playlists.getCombatOverrideTrack(it.combatTrackName)
                        ?: game.playlists.getKingmakerCombatTrack(it.combatTrackName))
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
            }
            .awaitAll()
    } else {
        regionSettings.regions.toList()
    }
}

suspend fun CampingData.findCurrentRegion(game: Game): RegionSetting? =
    getRegions(game).find { it.name == currentRegion }

suspend fun openCampingSheet(game: Game) {
    // TODO: create camping actor if not present
    game.getCampingActor()
        ?.let { actor -> CampingSheet(actor) }
        ?.launch()
}