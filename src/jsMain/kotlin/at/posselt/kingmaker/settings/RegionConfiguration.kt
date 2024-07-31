package at.posselt.kingmaker.settings

import at.posselt.kingmaker.app.*
import at.posselt.kingmaker.utils.buildPromise
import com.foundryvtt.core.*
import com.foundryvtt.core.applications.api.*
import js.array.push
import kotlinx.js.JsPlainObject
import org.w3c.dom.HTMLElement
import org.w3c.dom.get
import org.w3c.dom.pointerevents.PointerEvent
import kotlin.js.Promise

@JsPlainObject
external interface CombatTrack {
    var playlistId: String
    var trackId: String?
}

@JsPlainObject
external interface RegionSetting {
    var name: String
    var zoneDc: Int
    var encounterDc: Int
    var level: Int
    var rollTableId: String?
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
external interface RegionSettingsContext {
    var useStolenLands: FormElementContext
    var heading: Array<TableHead>
    var formRows: Array<Array<FormElementContext>>
    var isValid: Boolean
}

@OptIn(ExperimentalJsExport::class)
@JsExport
class RegionConfiguration : FormApp<RegionSettingsContext, RegionSettings>(
    title = "Regions",
    width = 1024,
    template = "applications/settings/configure-regions.hbs",
) {
    private var currentSettings = game.settings.kingmakerTools.getRegionSettings()

    override fun _onClickAction(event: PointerEvent, target: HTMLElement) {
        when (val action = target.dataset["action"]) {
            "save" -> {
                buildPromise {
                    game.settings.kingmakerTools.setRegionSettings(currentSettings)
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
        context: RegionSettingsContext,
        options: HandlebarsRenderOptions
    ): Promise<RegionSettingsContext> = buildPromise {
        val playlistOptions = game.playlists.contents
            .mapNotNull { it.toOption() }
            .sortedBy { it.label }
        val rolltableOptions = game.tables.contents
            .mapNotNull { it.toOption() }
            .sortedBy { it.label }
        RegionSettingsContext(
            isValid = isFormValid,
            useStolenLands = CheckboxInput(
                value = currentSettings.useStolenLands,
                name = "useStolenLands",
                label = "Use Stolen Lands",
            ).toContext(),
            heading = arrayOf(
                TableHead("Name"),
                TableHead("Level", arrayOf("small-heading")),
                TableHead("Zone DC", arrayOf("small-heading")),
                TableHead("Encounter DC", arrayOf("small-heading")),
                TableHead("Roll Table"),
                TableHead("Combat Playlist"),
                TableHead("Combat Track"),
                TableHead("Remove", arrayOf("small-heading"))
            ),
            formRows = currentSettings.regions.mapIndexed { index, row ->
                val trackOptions = row.combatTrack?.playlistId?.let {
                    game.playlists.get(it)?.sounds?.contents?.mapNotNull { it.toOption() } ?: emptyList()
                } ?: emptyList()
                arrayOf(
                    TextInput(
                        name = "regions.$index.name",
                        label = "Name",
                        value = row.name,
                        hideLabel = true
                    ).toContext(),
                    NumberInput(
                        name = "regions.$index.level",
                        label = "Level",
                        value = row.level,
                        hideLabel = true
                    ).toContext(),
                    NumberInput(
                        name = "regions.$index.zoneDc",
                        label = "Zone DC",
                        value = row.zoneDc,
                        hideLabel = true
                    ).toContext(),
                    NumberInput(
                        name = "regions.$index.encounterDc",
                        label = "Encounter DC",
                        value = row.encounterDc,
                        hideLabel = true,
                    ).toContext(),
                    Select(
                        name = "regions.$index.rollTableId",
                        label = "Roll Table",
                        value = row.rollTableId,
                        required = false,
                        hideLabel = true,
                        options = rolltableOptions,
                    ).toContext(),
                    Select(
                        name = "regions.$index.combatTrack.playlistId",
                        label = "Combat Playlist",
                        value = row.combatTrack?.playlistId,
                        required = false,
                        hideLabel = true,
                        options = playlistOptions
                    ).toContext(),
                    Select(
                        name = "regions.$index.combatTrack.trackId",
                        label = "Combat Track",
                        value = row.combatTrack?.trackId,
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
                rollTableId = null,
                combatTrack = null,
            )
        )
    }

}