package at.posselt.pfrpg.camping

import at.posselt.pfrpg.Config
import at.posselt.pfrpg.camping.dialogs.Track
import at.posselt.pfrpg.camping.dialogs.RegionSetting
import at.posselt.pfrpg.camping.dialogs.RegionSettings
import at.posselt.pfrpg.data.checks.DegreeOfSuccess
import at.posselt.pfrpg.data.regions.Terrain
import at.posselt.pfrpg.fromCamelCase
import at.posselt.pfrpg.toCamelCase
import at.posselt.pfrpg.utils.getAppFlag
import at.posselt.pfrpg.utils.setAppFlag
import com.foundryvtt.core.Game
import com.foundryvtt.core.utils.deepClone
import com.foundryvtt.pf2e.actor.PF2EActor
import com.foundryvtt.pf2e.actor.PF2ENpc
import js.objects.Record
import js.objects.recordOf
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
    var results: Record<String, String?>
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
    var restingTrack: Track?
    var worldSceneId: String?
}

suspend fun CampingData.getActorsInCamp(
    campingActivityOnly: Boolean = false
): List<PF2EActor> = coroutineScope {
    actorUuids
        .map {
            async {
                if (campingActivityOnly) {
                    getCampingActivityActorByUuid(it)
                } else {
                    getCampingActorByUuid(it)
                }
            }
        }
        .awaitAll()
        .filterNotNull()
}

fun CampingActivity.parseResult() =
    result?.let { fromCamelCase<DegreeOfSuccess>(it) }

fun CampingActivity.checkPerformed() =
    result != null && actorUuid != null

fun CampingActivity.isPrepareCampsite() =
    activity == "Prepare Campsite"

enum class CampingSheetSection {
    PREPARE_CAMPSITE,
    CAMPING_ACTIVITIES,
    EATING,
}

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
            actorMeals = emptyArray(),
            knownRecipes = arrayOf("Basic Meal", "Hearty Meal"),
            homebrewMeals = emptyArray(),
            cookingSkill = "survival",
            results = recordOf(),
            minimumSubsistence = 0,
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
        section = "prepareCampsite",
        alwaysPerformActivities = emptyArray(),
        restingTrack = null,
        regionSettings = RegionSettings(
            regions = arrayOf(
                RegionSetting(
                    name = "Zone 00",
                    zoneDc = 14,
                    encounterDc = 12,
                    level = 0,
                    combatTrack = Track(playlistUuid = playlistUuid, trackUuid = shrikeHills),
                    terrain = Terrain.HILLS.toCamelCase(),
                ),
                RegionSetting(
                    name = "Zone 01",
                    zoneDc = 15,
                    encounterDc = 12,
                    level = 1,
                    combatTrack = Track(playlistUuid = playlistUuid, trackUuid = shrikeHills),
                    terrain = Terrain.PLAINS.toCamelCase(),
                    rollTableUuid = rolltableUuids[0],
                ),
                RegionSetting(
                    name = "Zone 02",
                    zoneDc = 16,
                    encounterDc = 14,
                    level = 2,
                    combatTrack = Track(playlistUuid = playlistUuid, trackUuid = shrikeHills),
                    terrain = Terrain.FOREST.toCamelCase(),
                    rollTableUuid = rolltableUuids[1],
                ),
                RegionSetting(
                    name = "Zone 03",
                    zoneDc = 18,
                    encounterDc = 12,
                    level = 3,
                    combatTrack = Track(playlistUuid = playlistUuid, trackUuid = glenebon),
                    terrain = Terrain.FOREST.toCamelCase(),
                    rollTableUuid = rolltableUuids[2],
                ),
                RegionSetting(
                    name = "Zone 04",
                    zoneDc = 19,
                    encounterDc = 12,
                    level = 4,
                    combatTrack = Track(playlistUuid = playlistUuid, trackUuid = glenebon),
                    terrain = Terrain.HILLS.toCamelCase(),
                    rollTableUuid = rolltableUuids[3],
                ),
                RegionSetting(
                    name = "Zone 05",
                    zoneDc = 20,
                    encounterDc = 14,
                    level = 5,
                    combatTrack = Track(playlistUuid = playlistUuid, trackUuid = narlmarches),
                    terrain = Terrain.FOREST.toCamelCase(),
                    rollTableUuid = rolltableUuids[4],
                ),
                RegionSetting(
                    name = "Zone 06",
                    zoneDc = 20,
                    encounterDc = 12,
                    level = 6,
                    combatTrack = Track(playlistUuid = playlistUuid, trackUuid = glenebon),
                    terrain = Terrain.HILLS.toCamelCase(),
                    rollTableUuid = rolltableUuids[5],
                ),
                RegionSetting(
                    name = "Zone 07",
                    zoneDc = 18,
                    encounterDc = 12,
                    level = 7,
                    combatTrack = Track(playlistUuid = playlistUuid, trackUuid = dunsward),
                    terrain = Terrain.PLAINS.toCamelCase(),
                    rollTableUuid = rolltableUuids[6],
                ),
                RegionSetting(
                    name = "Zone 08",
                    zoneDc = 24,
                    encounterDc = 12,
                    level = 8,
                    combatTrack = Track(playlistUuid = playlistUuid, trackUuid = dunsward),
                    terrain = Terrain.HILLS.toCamelCase(),
                    rollTableUuid = rolltableUuids[7],
                ),
                RegionSetting(
                    name = "Zone 09",
                    zoneDc = 28,
                    encounterDc = 16,
                    level = 9,
                    combatTrack = Track(playlistUuid = playlistUuid, trackUuid = dunsward),
                    terrain = Terrain.MOUNTAIN.toCamelCase(),
                    rollTableUuid = rolltableUuids[8],
                ),
                RegionSetting(
                    name = "Zone 10",
                    zoneDc = 32,
                    encounterDc = 14,
                    level = 10,
                    combatTrack = Track(playlistUuid = playlistUuid, trackUuid = narlmarches),
                    terrain = Terrain.SWAMP.toCamelCase(),
                    rollTableUuid = rolltableUuids[9],
                ),
                RegionSetting(
                    name = "Zone 11",
                    zoneDc = 28,
                    encounterDc = 12,
                    level = 11,
                    combatTrack = Track(playlistUuid = playlistUuid, trackUuid = glenebon),
                    terrain = Terrain.PLAINS.toCamelCase(),
                    rollTableUuid = rolltableUuids[10],
                ),
                RegionSetting(
                    name = "Zone 12",
                    zoneDc = 28,
                    encounterDc = 12,
                    level = 12,
                    combatTrack = Track(playlistUuid = playlistUuid, trackUuid = glenebon),
                    terrain = Terrain.HILLS.toCamelCase(),
                    rollTableUuid = rolltableUuids[11],
                ),
                RegionSetting(
                    name = "Zone 13",
                    zoneDc = 26,
                    encounterDc = 12,
                    level = 13,
                    combatTrack = Track(playlistUuid = playlistUuid, trackUuid = shrikeHills),
                    terrain = Terrain.PLAINS.toCamelCase(),
                    rollTableUuid = rolltableUuids[12],
                ),
                RegionSetting(
                    name = "Zone 14",
                    zoneDc = 30,
                    encounterDc = 12,
                    level = 14,
                    combatTrack = Track(playlistUuid = playlistUuid, trackUuid = glenebon),
                    terrain = Terrain.PLAINS.toCamelCase(),
                    rollTableUuid = rolltableUuids[13],
                ),
                RegionSetting(
                    name = "Zone 15",
                    zoneDc = 29,
                    encounterDc = 12,
                    level = 15,
                    combatTrack = Track(playlistUuid = playlistUuid, trackUuid = capital),
                    terrain = Terrain.PLAINS.toCamelCase(),
                    rollTableUuid = rolltableUuids[14],
                ),
                RegionSetting(
                    name = "Zone 16",
                    zoneDc = 35,
                    encounterDc = 12,
                    level = 16,
                    combatTrack = Track(playlistUuid = playlistUuid, trackUuid = glenebon),
                    terrain = Terrain.HILLS.toCamelCase(),
                    rollTableUuid = rolltableUuids[15],
                ),
                RegionSetting(
                    name = "Zone 17",
                    zoneDc = 36,
                    encounterDc = 12,
                    level = 17,
                    combatTrack = Track(playlistUuid = playlistUuid, trackUuid = glenebon),
                    terrain = Terrain.HILLS.toCamelCase(),
                    rollTableUuid = rolltableUuids[16],
                ),
                RegionSetting(
                    name = "Zone 18",
                    zoneDc = 43,
                    encounterDc = 14,
                    level = 18,
                    combatTrack = Track(playlistUuid = playlistUuid, trackUuid = firstWorld),
                    terrain = Terrain.FOREST.toCamelCase(),
                    rollTableUuid = rolltableUuids[17],
                ),
                RegionSetting(
                    name = "Zone 19",
                    zoneDc = 41,
                    encounterDc = 16,
                    level = 19,
                    combatTrack = Track(playlistUuid = playlistUuid, trackUuid = glenebon),
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

fun CampingData.canPerformActivities(): Boolean {
    val prepareCampResult = campingActivities
        .find { it.isPrepareCampsite() }
        ?.result
        ?.let { fromCamelCase<DegreeOfSuccess>(it) }
    return prepareCampResult != null && prepareCampResult != DegreeOfSuccess.CRITICAL_FAILURE
}

fun CampingData.findCurrentRegion(): RegionSetting? =
    regionSettings.regions.find { it.name == currentRegion }
