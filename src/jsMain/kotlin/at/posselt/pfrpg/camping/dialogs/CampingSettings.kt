package at.posselt.pfrpg.camping.dialogs

import at.posselt.pfrpg.actor.party
import at.posselt.pfrpg.app.*
import at.posselt.pfrpg.app.FormApp
import at.posselt.pfrpg.app.forms.CheckboxInput
import at.posselt.pfrpg.app.forms.Menu
import at.posselt.pfrpg.app.forms.NumberInput
import at.posselt.pfrpg.app.forms.Section
import at.posselt.pfrpg.app.forms.SectionsContext
import at.posselt.pfrpg.app.forms.Select
import at.posselt.pfrpg.app.forms.SelectOption
import at.posselt.pfrpg.app.forms.formContext
import at.posselt.pfrpg.app.forms.toOption
import at.posselt.pfrpg.camping.*
import at.posselt.pfrpg.data.checks.RollMode
import at.posselt.pfrpg.fromCamelCase
import at.posselt.pfrpg.toCamelCase
import at.posselt.pfrpg.utils.asSequence
import at.posselt.pfrpg.utils.buildPromise
import at.posselt.pfrpg.utils.fromUuidTypeSafe
import at.posselt.pfrpg.utils.fromUuidsOfTypes
import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.Game
import com.foundryvtt.core.abstract.DataModel
import com.foundryvtt.core.applications.api.HandlebarsRenderOptions
import com.foundryvtt.core.data.dsl.buildSchema
import com.foundryvtt.core.documents.Playlist
import com.foundryvtt.core.documents.PlaylistSound
import com.foundryvtt.core.utils.flattenObject
import com.foundryvtt.pf2e.actor.PF2ECharacter
import com.foundryvtt.pf2e.actor.PF2ELoot
import com.foundryvtt.pf2e.actor.PF2ENpc
import com.foundryvtt.pf2e.actor.PF2EVehicle
import js.array.toTypedArray
import js.core.Void
import js.objects.Record
import js.objects.jso
import kotlinx.coroutines.await
import kotlinx.js.JsPlainObject
import org.w3c.dom.HTMLElement
import org.w3c.dom.get
import org.w3c.dom.pointerevents.PointerEvent
import kotlin.js.Promise


@JsPlainObject
external interface CampingSettings {
    val gunsToClean: Int
    val restRollMode: String
    val increaseWatchActorNumber: Int
    val actorUuidsNotKeepingWatch: Array<String>
    val huntAndGatherTargetActorUuid: String?
    val proxyRandomEncounterTableUuid: String?
    val randomEncounterRollMode: String
    val ignoreSkillRequirements: Boolean
    val minimumTravelSpeed: Int?
    val minimumSubsistence: Int
    val alwaysPerformActivities: Array<String>
    val restingPlaylistUuid: String?
    val restingPlaylistSoundUuid: String?
    val worldSceneId: String?
}

@OptIn(ExperimentalJsExport::class)
@JsExport
class CampingSettingsDataModel(value: AnyObject) : DataModel(value) {
    companion object {
        @Suppress("unused")
        @OptIn(ExperimentalJsStatic::class)
        @JsStatic
        fun defineSchema() = buildSchema {
            int("gunsToClean")
            string("restRollMode") {
                choices = RestRollMode.entries.map { it.toCamelCase() }.toTypedArray()
            }
            string("worldSceneId", nullable = true)
            int("increaseWatchActorNumber")
            stringArray("actorUuidsNotKeepingWatch")
            stringArray("alwaysPerformActivities")
            string("huntAndGatherTargetActorUuid", nullable = true)
            string("proxyRandomEncounterTableUuid", nullable = true)
            string("randomEncounterRollMode")
            string("restingPlaylistUuid", nullable = true)
            string("restingPlaylistSoundUuid", nullable = true)
            boolean("ignoreSkillRequirements")
            int("minimumTravelSpeed")
            int("minimumSubsistence")
        }
    }
}

@JsPlainObject
external interface CampingSettingsContext : HandlebarsRenderContext, SectionsContext {
    val isFormValid: Boolean
}

enum class RestRollMode {
    NONE,
    ONE,
    ONE_EVERY_FOUR_HOURS,
}

private val companionActivities = setOf(
    "Blend Into The Night",
    "Bolster Confidence",
    "Bolster Confidence",
    "Enhance Weapons",
    "Healer's Blessing",
    "Intimidating Posture",
    "Maintain Armor",
    "Set Alarms",
    "Set Traps",
    "Undead Guardians",
    "Water Hazards",
    "Wilderness Survival",
)

@OptIn(ExperimentalJsExport::class)
@JsExport
class CampingSettingsApplication(
    private val game: Game,
    private val campingActor: PF2ENpc,
) : FormApp<CampingSettingsContext, CampingSettings>(
    title = "Camping Settings",
    template = "components/forms/application-form.hbs",
    debug = true,
    dataModel = CampingSettingsDataModel::class.js,
) {
    var settings: CampingSettings

    init {
        val camping = campingActor.getCamping()!!
        settings = CampingSettings(
            gunsToClean = camping.gunsToClean,
            restRollMode = camping.restRollMode,
            increaseWatchActorNumber = camping.increaseWatchActorNumber,
            actorUuidsNotKeepingWatch = camping.actorUuidsNotKeepingWatch,
            huntAndGatherTargetActorUuid = camping.huntAndGatherTargetActorUuid,
            proxyRandomEncounterTableUuid = camping.proxyRandomEncounterTableUuid,
            randomEncounterRollMode = camping.randomEncounterRollMode,
            ignoreSkillRequirements = camping.ignoreSkillRequirements,
            minimumTravelSpeed = camping.minimumTravelSpeed,
            minimumSubsistence = camping.cooking.minimumSubsistence,
            alwaysPerformActivities = camping.alwaysPerformActivities,
            restingPlaylistUuid = camping.restingPlaylistUuid,
            restingPlaylistSoundUuid = camping.restingPlaylistSoundUuid,
            worldSceneId = camping.worldSceneId,
        )
    }

    override fun _preparePartContext(
        partId: String,
        context: HandlebarsRenderContext,
        options: HandlebarsRenderOptions
    ): Promise<CampingSettingsContext> = buildPromise {
        val parent = super._preparePartContext(partId, context, options).await()
        val camping = campingActor.getCamping()!!
        val actors = fromUuidsOfTypes(
            camping.actorUuids,
            PF2ENpc::class,
            PF2ECharacter::class,
            PF2EVehicle::class,
            PF2ELoot::class,
        )
        val huntAndGatherUuids = (actors + listOfNotNull(game.party()))
            .mapNotNull { it.toOption(useUuid = true) }
        val uuidsNotKeepingWatch = setOf(*settings.actorUuidsNotKeepingWatch)
        val playlist = settings.restingPlaylistUuid?.let { fromUuidTypeSafe<Playlist>(it) }
        val playlistSound = settings.restingPlaylistSoundUuid?.let { fromUuidTypeSafe<PlaylistSound>(it) }
        val hexScenes = game.scenes
            .filter { it.grid.type == 2 }
            .mapNotNull { it.id?.let { id -> SelectOption(label = it.name, value = id) } }
        CampingSettingsContext(
            partId = parent.partId,
            isFormValid = isFormValid,
            sections = formContext(
                Section(
                    legend = "Exploration",
                    formRows = listOf(
                        Select(
                            label = "Hexploration Scene",
                            name = "worldSceneId",
                            help = "Store party actor token positions on this map each time you Prepare Campsite to reuse camp locations",
                            options = hexScenes,
                            required = false,
                            value = settings.worldSceneId,
                            stacked = false,
                        ),
                        Menu(
                            label = "Camping Positions",
                            name = "Reset",
                            value = "reset-camping-positions",
                            disabled = settings.worldSceneId == null,
                        ),
                        NumberInput(
                            name = "minimumTravelSpeed",
                            label = "Minimum Travel Speed",
                            value = settings.minimumTravelSpeed ?: 0,
                            help = "If PCs use horses, use 40",
                            stacked = false,
                        ),
                        Select.fromEnum<RollMode>(
                            name = "randomEncounterRollMode",
                            label = "Random Encounter Roll Mode",
                            value = fromCamelCase<RollMode>(settings.randomEncounterRollMode),
                            labelFunction = { it.label },
                            stacked = false,
                        ),
                        Select(
                            name = "proxyRandomEncounterTableUuid",
                            value = settings.proxyRandomEncounterTableUuid,
                            label = "Proxy Random Encounter Table",
                            required = false,
                            options = game.tables.contents.mapNotNull { it.toOption(useUuid = true) },
                            help = "Custom Roll Table; use 'Creature' text result to roll on the default random encounter table",
                            stacked = false,
                        ),
                    )
                ),
                Section(
                    legend = "Activities",
                    formRows = listOf(
                        CheckboxInput(
                            name = "ignoreSkillRequirements",
                            label = "Do not validate activity skill proficiency",
                            value = settings.ignoreSkillRequirements,
                        ),
                        Select(
                            name = "huntAndGatherTargetActorUuid",
                            value = settings.huntAndGatherTargetActorUuid,
                            label = "Add Ingredients from Hunt and Gather to",
                            required = false,
                            options = huntAndGatherUuids,
                            help = "Default is the actor performing the activity",
                            stacked = false,
                        ),
                    )
                ),
                Section(
                    legend = "Always Performed Activities",
                    formRows = companionActivities.map {
                        CheckboxInput(
                            label = it,
                            name = "alwaysPerformActivities.$it",
                            value = settings.alwaysPerformActivities.contains(it),
                            stacked = false,
                            help = "Activity will be hidden from list of activities and will be automatically enabled"
                        )
                    }
                ),
                Section(
                    legend = "Cooking",
                    formRows = listOf(
                        NumberInput(
                            name = "minimumSubsistence",
                            label = "Minimum Subsistence",
                            help = "Reduce ration meal cost by this amount",
                            value = settings.minimumSubsistence,
                            stacked = false,
                        ),
                    )
                ),
                Section(
                    legend = "Resting",
                    formRows = listOf(
                        Select(
                            "Playlist",
                            name = "restingPlaylistUuid",
                            value = playlist?.uuid,
                            required = false,
                            stacked = false,
                            help = "Played when resting. Make sure to change the Playback Mode to Soundboard Only to only play it once.",
                            options = game.playlists.contents.mapNotNull { it.toOption(useUuid = true) },
                        ),
                        Select(
                            "Playlist Track",
                            name = "restingPlaylistSoundUuid",
                            value = playlistSound?.uuid,
                            required = false,
                            stacked = false,
                            options = playlist?.sounds?.contents?.mapNotNull { it.toOption(useUuid = true) }
                                ?: emptyList(),
                        ),
                        NumberInput(
                            name = "gunsToClean",
                            label = "Guns To Clean",
                            value = settings.gunsToClean,
                            stacked = false,
                        ),
                        NumberInput(
                            name = "increaseWatchActorNumber",
                            label = "Increase Actors Keeping Watch",
                            value = settings.increaseWatchActorNumber,
                            stacked = false,
                        ),
                        Select.fromEnum<RestRollMode>(
                            name = "restRollMode",
                            label = "Roll Random Encounter During Rest",
                            value = fromCamelCase<RestRollMode>(settings.restRollMode),
                            stacked = false,
                        ),
                        *actors.mapIndexed { index, actor ->
                            CheckboxInput(
                                name = "actorUuidsNotKeepingWatch.${actor.uuid}",
                                label = "Skip Watch: ${actor.name}",
                                value = uuidsNotKeepingWatch.contains(actor.uuid),
                            )
                        }.toTypedArray()
                    )
                ),
            )
        )
    }

    override fun fixObject(value: dynamic) {
        @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
        val actors = (value["actorUuidsNotKeepingWatch"] ?: jso()) as Record<String, Boolean>
        value["actorUuidsNotKeepingWatch"] = flattenObject(actors).asSequence()
            .filter { it.component2() == true }
            .map { it.component1() }
            .toTypedArray()
        val activities = (value["alwaysPerformActivities"] ?: jso()) as Record<String, Boolean>
        value["alwaysPerformActivities"] = flattenObject(activities).asSequence()
            .filter { it.component2() == true }
            .map { it.component1() }
            .toTypedArray()
    }

    override fun onParsedSubmit(value: CampingSettings): Promise<Void> = buildPromise {
        settings = value
        undefined
    }

    override fun _onClickAction(event: PointerEvent, target: HTMLElement) {
        when (val action = target.dataset["action"]) {
            "save" -> {
                buildPromise {
                    campingActor.getCamping()?.let { camping ->
                        val alwaysPerformNames = settings.alwaysPerformActivities.toSet()
                        camping.gunsToClean = settings.gunsToClean
                        camping.restRollMode = settings.restRollMode
                        camping.increaseWatchActorNumber = settings.increaseWatchActorNumber
                        camping.actorUuidsNotKeepingWatch = settings.actorUuidsNotKeepingWatch
                        camping.huntAndGatherTargetActorUuid = settings.huntAndGatherTargetActorUuid
                        camping.proxyRandomEncounterTableUuid = settings.proxyRandomEncounterTableUuid
                        camping.randomEncounterRollMode = settings.randomEncounterRollMode
                        camping.ignoreSkillRequirements = settings.ignoreSkillRequirements
                        camping.minimumTravelSpeed = settings.minimumTravelSpeed
                        camping.cooking.minimumSubsistence = settings.minimumSubsistence
                        camping.alwaysPerformActivities = settings.alwaysPerformActivities
                        camping.restingPlaylistSoundUuid = settings.restingPlaylistSoundUuid
                        camping.restingPlaylistUuid = settings.restingPlaylistUuid
                        camping.worldSceneId = settings.worldSceneId
                        camping.campingActivities = camping.campingActivities
                            .filter { it.activity !in alwaysPerformNames }
                            .toTypedArray()
                        campingActor.setCamping(camping)
                    }
                    close()
                }
            }

            "reset-camping-positions" -> buildPromise {
                settings.worldSceneId
                    ?.let { worldSceneId -> game.scenes.get(worldSceneId) }
                    ?.let { scene ->
                        if (confirm("${scene.name}: Reset all Camping Positions?")) {
                            scene.resetCampsites()
                        }
                    }
            }

            else -> console.log(action)
        }
    }
}