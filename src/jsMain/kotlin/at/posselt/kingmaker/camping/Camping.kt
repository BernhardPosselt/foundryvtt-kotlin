package at.posselt.kingmaker.camping

import at.posselt.kingmaker.Config
import at.posselt.kingmaker.camping.dialogs.*
import at.posselt.kingmaker.data.actor.Attribute
import at.posselt.kingmaker.data.checks.DegreeOfSuccess
import at.posselt.kingmaker.data.regions.Terrain
import at.posselt.kingmaker.toCamelCase
import at.posselt.kingmaker.utils.*
import com.foundryvtt.core.Game
import com.foundryvtt.core.utils.deepClone
import com.foundryvtt.pf2e.actor.PF2ECreature
import com.foundryvtt.pf2e.actor.PF2ENpc

private val playlistUuid = "Playlist.7CiwVus60FiuKFhK"
private val capital = "Playlist.7CiwVus60FiuKFhK.PlaylistSound.vGP8BFAN2DaZcpri"
private val firstWorld = "Playlist.7CiwVus60FiuKFhK.PlaylistSound.3iH2HLuQaSIlm0F7"
private val glenebon = "Playlist.7CiwVus60FiuKFhK.PlaylistSound.O656E1AtGDTH3EU1"
private val narlmarches = "Playlist.7CiwVus60FiuKFhK.PlaylistSound.b5LKh5J7ZTrfBXVz"
private val shrikeHills = "Playlist.7CiwVus60FiuKFhK.PlaylistSound.mKDBa58aNaiJA1Fp"
private val dunsward = "Playlist.7CiwVus60FiuKFhK.PlaylistSound.CrIyaBio4BycqrwO"

private val rolltableUuids = arrayOf(
    "RollTable.44cfq5QJS2O5tn0K",
    "RollTable.Kb4D0vcAcx6EPEFf",
    "RollTable.DMNH6Bn5BfJLV5cB",
    "RollTable.xYVMEePFnF9KoeVO",
    "RollTable.TtuhcaPJyGQkvOQW",
    "RollTable.Oc45l0EFS1EeECYH",
    "RollTable.p3XuOWVRbg0UcSqf",
    "RollTable.Tt4bNzG2wcPOE8vo",
    "RollTable.1OJqyDO2Ws0fQ77v",
    "RollTable.8UtL2oSZjJCcdIHz",
    "RollTable.3Dcalfi2p4jbQxwf",
    "RollTable.cFG8I1fCtU3bhOD9",
    "RollTable.2BPSdXXHvIHrWbL3",
    "RollTable.hhNaJ7HkSIQVYLsq",
    "RollTable.kjXrJUlapDYo9QaJ",
    "RollTable.7brKA7efwUFA5ef0",
    "RollTable.7QW9OYWDh3MfnSF3",
    "RollTable.6Q5WgEnwgfO39ZMz",
    "RollTable.zhxH34Hz1ixX7l4n"
)

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
        section = "prepareCamp",
        regionSettings = RegionSettings(
            regions = arrayOf(
                RegionSetting(
                    name = "Zone 00",
                    zoneDc = 14,
                    encounterDc = 12,
                    level = 0,
                    combatTrack = CombatTrack(playlistUuid = playlistUuid, trackUuid = shrikeHills),
                    terrain = Terrain.HILLS.toCamelCase(),
                ),
                RegionSetting(
                    name = "Zone 01",
                    zoneDc = 15,
                    encounterDc = 12,
                    level = 1,
                    combatTrack = CombatTrack(playlistUuid = playlistUuid, trackUuid = shrikeHills),
                    terrain = Terrain.PLAINS.toCamelCase(),
                    rollTableUuid = rolltableUuids[0],
                ),
                RegionSetting(
                    name = "Zone 02",
                    zoneDc = 16,
                    encounterDc = 14,
                    level = 2,
                    combatTrack = CombatTrack(playlistUuid = playlistUuid, trackUuid = shrikeHills),
                    terrain = Terrain.FOREST.toCamelCase(),
                    rollTableUuid = rolltableUuids[1],
                ),
                RegionSetting(
                    name = "Zone 03",
                    zoneDc = 18,
                    encounterDc = 12,
                    level = 3,
                    combatTrack = CombatTrack(playlistUuid = playlistUuid, trackUuid = glenebon),
                    terrain = Terrain.FOREST.toCamelCase(),
                    rollTableUuid = rolltableUuids[2],
                ),
                RegionSetting(
                    name = "Zone 04",
                    zoneDc = 19,
                    encounterDc = 12,
                    level = 4,
                    combatTrack = CombatTrack(playlistUuid = playlistUuid, trackUuid = glenebon),
                    terrain = Terrain.HILLS.toCamelCase(),
                    rollTableUuid = rolltableUuids[3],
                ),
                RegionSetting(
                    name = "Zone 05",
                    zoneDc = 20,
                    encounterDc = 14,
                    level = 5,
                    combatTrack = CombatTrack(playlistUuid = playlistUuid, trackUuid = narlmarches),
                    terrain = Terrain.FOREST.toCamelCase(),
                    rollTableUuid = rolltableUuids[4],
                ),
                RegionSetting(
                    name = "Zone 06",
                    zoneDc = 20,
                    encounterDc = 12,
                    level = 6,
                    combatTrack = CombatTrack(playlistUuid = playlistUuid, trackUuid = glenebon),
                    terrain = Terrain.HILLS.toCamelCase(),
                    rollTableUuid = rolltableUuids[5],
                ),
                RegionSetting(
                    name = "Zone 07",
                    zoneDc = 18,
                    encounterDc = 12,
                    level = 7,
                    combatTrack = CombatTrack(playlistUuid = playlistUuid, trackUuid = dunsward),
                    terrain = Terrain.PLAINS.toCamelCase(),
                    rollTableUuid = rolltableUuids[6],
                ),
                RegionSetting(
                    name = "Zone 08",
                    zoneDc = 24,
                    encounterDc = 12,
                    level = 8,
                    combatTrack = CombatTrack(playlistUuid = playlistUuid, trackUuid = dunsward),
                    terrain = Terrain.HILLS.toCamelCase(),
                    rollTableUuid = rolltableUuids[7],
                ),
                RegionSetting(
                    name = "Zone 09",
                    zoneDc = 28,
                    encounterDc = 16,
                    level = 9,
                    combatTrack = CombatTrack(playlistUuid = playlistUuid, trackUuid = dunsward),
                    terrain = Terrain.MOUNTAIN.toCamelCase(),
                    rollTableUuid = rolltableUuids[8],
                ),
                RegionSetting(
                    name = "Zone 10",
                    zoneDc = 32,
                    encounterDc = 14,
                    level = 10,
                    combatTrack = CombatTrack(playlistUuid = playlistUuid, trackUuid = narlmarches),
                    terrain = Terrain.SWAMP.toCamelCase(),
                    rollTableUuid = rolltableUuids[9],
                ),
                RegionSetting(
                    name = "Zone 11",
                    zoneDc = 28,
                    encounterDc = 12,
                    level = 11,
                    combatTrack = CombatTrack(playlistUuid = playlistUuid, trackUuid = glenebon),
                    terrain = Terrain.PLAINS.toCamelCase(),
                    rollTableUuid = rolltableUuids[10],
                ),
                RegionSetting(
                    name = "Zone 12",
                    zoneDc = 28,
                    encounterDc = 12,
                    level = 12,
                    combatTrack = CombatTrack(playlistUuid = playlistUuid, trackUuid = glenebon),
                    terrain = Terrain.HILLS.toCamelCase(),
                    rollTableUuid = rolltableUuids[11],
                ),
                RegionSetting(
                    name = "Zone 13",
                    zoneDc = 26,
                    encounterDc = 12,
                    level = 13,
                    combatTrack = CombatTrack(playlistUuid = playlistUuid, trackUuid = shrikeHills),
                    terrain = Terrain.PLAINS.toCamelCase(),
                    rollTableUuid = rolltableUuids[12],
                ),
                RegionSetting(
                    name = "Zone 14",
                    zoneDc = 30,
                    encounterDc = 12,
                    level = 14,
                    combatTrack = CombatTrack(playlistUuid = playlistUuid, trackUuid = glenebon),
                    terrain = Terrain.PLAINS.toCamelCase(),
                    rollTableUuid = rolltableUuids[13],
                ),
                RegionSetting(
                    name = "Zone 15",
                    zoneDc = 29,
                    encounterDc = 12,
                    level = 15,
                    combatTrack = CombatTrack(playlistUuid = playlistUuid, trackUuid = capital),
                    terrain = Terrain.PLAINS.toCamelCase(),
                    rollTableUuid = rolltableUuids[14],
                ),
                RegionSetting(
                    name = "Zone 16",
                    zoneDc = 35,
                    encounterDc = 12,
                    level = 16,
                    combatTrack = CombatTrack(playlistUuid = playlistUuid, trackUuid = glenebon),
                    terrain = Terrain.HILLS.toCamelCase(),
                    rollTableUuid = rolltableUuids[15],
                ),
                RegionSetting(
                    name = "Zone 17",
                    zoneDc = 36,
                    encounterDc = 12,
                    level = 17,
                    combatTrack = CombatTrack(playlistUuid = playlistUuid, trackUuid = glenebon),
                    terrain = Terrain.HILLS.toCamelCase(),
                    rollTableUuid = rolltableUuids[16],
                ),
                RegionSetting(
                    name = "Zone 18",
                    zoneDc = 43,
                    encounterDc = 14,
                    level = 18,
                    combatTrack = CombatTrack(playlistUuid = playlistUuid, trackUuid = firstWorld),
                    terrain = Terrain.FOREST.toCamelCase(),
                    rollTableUuid = rolltableUuids[17],
                ),
                RegionSetting(
                    name = "Zone 19",
                    zoneDc = 41,
                    encounterDc = 16,
                    level = 19,
                    combatTrack = CombatTrack(playlistUuid = playlistUuid, trackUuid = glenebon),
                    terrain = Terrain.MOUNTAIN.toCamelCase(),
                    rollTableUuid = rolltableUuids[18],
                ),
            ),
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

fun CampingData.canPerformActivities() =
    campingActivities.find { it.isPrepareCamp() }?.result != DegreeOfSuccess.CRITICAL_FAILURE.toCamelCase()

fun CampingData.findCurrentRegion(): RegionSetting? =
    regionSettings.regions.find { it.name == currentRegion }

suspend fun openCampingSheet(game: Game) {
    // TODO: create camping actor if not present
    game.getCampingActor()
        ?.let { actor -> CampingSheet(game, actor) }
        ?.launch()
}