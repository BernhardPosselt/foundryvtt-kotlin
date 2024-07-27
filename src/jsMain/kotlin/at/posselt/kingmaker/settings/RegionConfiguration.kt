package at.posselt.kingmaker.settings

import at.posselt.kingmaker.app.*
import at.posselt.kingmaker.utils.buildPromise
import at.posselt.kingmaker.utils.resolveTemplatePath
import com.foundryvtt.core.*
import com.foundryvtt.core.applications.api.*
import com.foundryvtt.core.data.fields.DataFieldOptions
import com.foundryvtt.core.data.fields.ObjectField
import js.array.push
import js.core.Void
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.html.org.w3c.dom.events.Event
import kotlinx.js.JsPlainObject
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLFormElement
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
}

@OptIn(ExperimentalJsExport::class)
@JsExport
class RegionConfiguration : App<RegionSettingsContext>(
    HandlebarsFormApplicationOptions(
        window = Window(
            title = "Regions",
            controls = arrayOf(ApplicationHeaderControlsEntry(label = "hi", action = "boo"))
        ),
        position = ApplicationPosition(
            width = 900,
        ),
        templatePath = resolveTemplatePath("applications/settings/configure-regions.hbs"),
        classes = arrayOf("km-dialog-form"),
        tag = "form",
        form = ApplicationFormConfiguration(
            submitOnChange = true,
            closeOnSubmit = false,
        )
    )
) {
    init {
        appHook.onUpdateWorldTime { it, _, _, _ -> console.log(it) }
    }

    var currentSettings = game.settings.getObject<RegionSettings>("regionSettings")

    override fun _onClickAction(event: PointerEvent, target: HTMLElement) {
        when (val action = target.dataset["action"]) {
            "save" -> {
                buildPromise {
                    game.settings.setObject("regionSettings", currentSettings).await()
                    close()
                }
            }

            "add" -> {
                addDefaultRegion()
                render()
            }

            "delete" -> {
                target.dataset["index"]?.toInt()?.let {
                    console.log(it)
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
                console.log(row.combatTrack?.playlistId, row.combatTrack?.trackId)
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
                        allowsEmpty = true,
                        hideLabel = true,
                        options = rolltableOptions,
                    ).toContext(),
                    Select(
                        name = "regions.$index.combatTrack.playListId",
                        label = "Combat Playlist",
                        value = row.combatTrack?.playlistId,
                        allowsEmpty = true,
                        hideLabel = true,
                        options = playlistOptions
                    ).toContext(),
                    Select(
                        name = "regions.$index.combatTrack.trackId",
                        label = "Combat Track",
                        value = row.combatTrack?.trackId,
                        allowsEmpty = true,
                        hideLabel = true,
                        options = trackOptions
                    ).toContext(),
                )
            }.toTypedArray()
        )
    }

    override fun onSubmit(event: Event, form: HTMLFormElement, formData: FormDataExtended<AnyObject>): Promise<Void> =
        buildPromise {
            val obj = parseFormData<RegionSettings>(formData.`object`) {
                it["regions"] = (it["regions"] as Array<RegionSetting>?) ?: emptyArray<RegionSetting>()
                console.log(it)
            }
            currentSettings = obj
            if (currentSettings.useStolenLands && currentSettings.regions.isEmpty()) {
                addDefaultRegion()
            }
            console.log(formData, currentSettings)
            render()
            null
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

fun registerRegionSettings(game: Game) {
    game.settings.registerField(
        key = "regionSettings",
        name = "Region Settings",
        type = ObjectField(DataFieldOptions(initial = RegionSettings(useStolenLands = true, regions = emptyArray()))),
    )
    game.settings.createMenu(
        key = "regionsMenu",
        label = "Customize",
        name = "Regions",
        app = RegionConfiguration::class.js,
    )
}