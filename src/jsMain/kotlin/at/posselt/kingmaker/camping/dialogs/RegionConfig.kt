package at.posselt.kingmaker.camping.dialogs

import at.posselt.kingmaker.Config
import at.posselt.kingmaker.app.*
import at.posselt.kingmaker.camping.getCamping
import at.posselt.kingmaker.camping.setCamping
import at.posselt.kingmaker.data.regions.Terrain
import at.posselt.kingmaker.fromCamelCase
import at.posselt.kingmaker.toCamelCase
import at.posselt.kingmaker.utils.buildPromise
import com.foundryvtt.core.*
import com.foundryvtt.core.applications.api.*
import com.foundryvtt.pf2e.actor.PF2ENpc
import js.array.push
import kotlinx.coroutines.await
import kotlinx.js.JsPlainObject
import org.w3c.dom.HTMLElement
import org.w3c.dom.get
import org.w3c.dom.pointerevents.PointerEvent
import kotlin.js.Promise

@JsPlainObject
external interface CombatTrack {
    var playlistUuid: String
    var trackUuid: String?
}

@JsPlainObject
external interface RegionSetting {
    var name: String
    var zoneDc: Int
    var encounterDc: Int
    var level: Int
    var terrain: String
    var rollTableUuid: String?
    var combatTrack: CombatTrack?
}

@JsPlainObject
external interface RegionSettings {
    var useStolenLands: Boolean
    var regions: Array<RegionSetting>
}

@JsPlainObject
external interface TableHead {
    var label: String
    var classes: Array<String>?
}

@JsPlainObject
external interface RegionSettingsContext : HandlebarsRenderContext {
    var useStolenLands: FormElementContext
    var heading: Array<TableHead>
    var formRows: Array<Array<FormElementContext>>
    var isValid: Boolean
    var allowDelete: Boolean
}

@OptIn(ExperimentalJsExport::class)
@JsExport
@JsName("RegionConfig")
class RegionConfig(
    private val actor: PF2ENpc,
) : FormApp<RegionSettingsContext, RegionSettings>(
    title = "Regions",
    width = 1200,
    template = "applications/settings/configure-regions.hbs",
) {
    private var currentSettings = actor.getCamping()!!.regionSettings

    override fun _onClickAction(event: PointerEvent, target: HTMLElement) {
        when (val action = target.dataset["action"]) {
            "save" -> {
                buildPromise {
                    actor.getCamping()?.let { camping ->
                        camping.regionSettings = currentSettings
                        if (currentSettings.useStolenLands) {
                            camping.currentRegion = Config.regions.defaultRegion
                        } else {
                            camping.currentRegion = currentSettings.regions.first().name
                        }
                        actor.setCamping(camping)
                    }
                    close()
                }
            }

            "add" -> {
                addDefaultRegion()
                render()
            }

            "delete" -> {
                target.dataset["index"]?.toInt()?.let {
                    currentSettings.regions = currentSettings.regions
                        .filterIndexed { index, _ -> index != it }
                        .toTypedArray()
                    render()
                }
            }

            else -> console.log(action)
        }
    }

    override fun _preparePartContext(
        partId: String,
        context: HandlebarsRenderContext,
        options: HandlebarsRenderOptions
    ): Promise<RegionSettingsContext> = buildPromise {
        val parent = super._preparePartContext(partId, context, options).await()
        val playlistOptions = game.playlists.contents
            .mapNotNull { it.toOption(useUuid = true) }
            .sortedBy { it.label }
        val rolltableOptions = game.tables.contents
            .mapNotNull { it.toOption(useUuid = true) }
            .sortedBy { it.label }
        RegionSettingsContext(
            partId = parent.partId,
            isValid = isFormValid,
            useStolenLands = CheckboxInput(
                value = currentSettings.useStolenLands,
                name = "useStolenLands",
                label = "Use Stolen Lands",
            ).toContext(),
            heading = arrayOf(
                TableHead("Name"),
                TableHead("Level", arrayOf("number-select-heading")),
                TableHead("Terrain"),
                TableHead("Zone DC", arrayOf("number-select-heading")),
                TableHead("Encounter DC", arrayOf("number-select-heading")),
                TableHead("Roll Table"),
                TableHead("Combat Playlist"),
                TableHead("Combat Track"),
                TableHead("Remove", arrayOf("small-heading"))
            ),
            allowDelete = currentSettings.regions.size > 1,
            formRows = currentSettings.regions.mapIndexed { index, row ->
                val trackOptions = row.combatTrack?.playlistUuid?.let { uuid ->
                    game.playlists.find { it.uuid == uuid }?.sounds?.contents?.mapNotNull { it.toOption(useUuid = true) }
                        ?: emptyList()
                } ?: emptyList()
                arrayOf(
                    TextInput(
                        name = "regions.$index.name",
                        label = "Name",
                        value = row.name,
                        hideLabel = true
                    ).toContext(),
                    Select.level(
                        name = "regions.$index.level",
                        label = "Level",
                        value = row.level,
                        hideLabel = true
                    ).toContext(),
                    Select.fromEnum<Terrain>(
                        name = "regions.$index.terrain",
                        label = "Terrain",
                        value = fromCamelCase<Terrain>(row.terrain),
                        hideLabel = true
                    ).toContext(),
                    Select.dc(
                        name = "regions.$index.zoneDc",
                        label = "Zone DC",
                        value = row.zoneDc,
                        hideLabel = true
                    ).toContext(),
                    Select.flatCheck(
                        name = "regions.$index.encounterDc",
                        label = "Encounter DC",
                        value = row.encounterDc,
                        hideLabel = true,
                    ).toContext(),
                    Select(
                        name = "regions.$index.rollTableUuid",
                        label = "Roll Table",
                        value = row.rollTableUuid,
                        required = false,
                        hideLabel = true,
                        options = rolltableOptions,
                    ).toContext(),
                    Select(
                        name = "regions.$index.combatTrack.playlistUuid",
                        label = "Combat Playlist",
                        value = row.combatTrack?.playlistUuid,
                        required = false,
                        hideLabel = true,
                        options = playlistOptions
                    ).toContext(),
                    Select(
                        name = "regions.$index.combatTrack.trackUuid",
                        label = "Combat Track",
                        value = row.combatTrack?.trackUuid,
                        required = false,
                        hideLabel = true,
                        options = trackOptions
                    ).toContext(),
                )
            }.toTypedArray()
        )
    }

    override fun onParsedSubmit(value: RegionSettings) = buildPromise {
        currentSettings = value
        if (!currentSettings.useStolenLands && currentSettings.regions.isEmpty()) {
            addDefaultRegion()
        }
        null
    }

    override fun fixObject(value: dynamic) {
        value["regions"] = (value["regions"] as Array<RegionSetting>?) ?: emptyArray<RegionSetting>()
    }

    private fun addDefaultRegion() {
        currentSettings.regions.push(
            RegionSetting(
                name = "New Region",
                zoneDc = 15,
                encounterDc = 12,
                level = 1,
                rollTableUuid = null,
                combatTrack = null,
                terrain = Terrain.PLAINS.toCamelCase(),
            )
        )
    }

}