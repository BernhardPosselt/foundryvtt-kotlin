package at.posselt.kingmaker.settings

import at.posselt.kingmaker.dialog.*
import at.posselt.kingmaker.utils.buildPromise
import at.posselt.kingmaker.utils.resolveTemplatePath
import com.foundryvtt.core.*
import com.foundryvtt.core.data.fields.DataFieldOptions
import com.foundryvtt.core.data.fields.ObjectField
import kotlinx.coroutines.await
import kotlinx.html.org.w3c.dom.events.Event
import kotlinx.js.JsPlainObject
import kotlin.js.Promise

@JsPlainObject
external interface CombatTrack {
    val playlistId: String
    val trackId: String?
}

@JsPlainObject
external interface RegionSetting {
    val name: String
    val zoneDc: Int
    val encounterDc: Int
    val level: Int
    val rollTableId: String
    val combatTrack: CombatTrack?
}

@JsPlainObject
external interface RegionSettings {
    val useStolenLands: Boolean
    val regions: Array<RegionSetting>
}

@JsPlainObject
external interface RegionSettingsContext {
    val useStolenLands: FormElementContext
    val heading: Array<String>
    val formRows: Array<Array<FormElementContext>>
}


class RegionConfiguration(
    val game: Game,
) : App<AnyObject, RegionSettingsContext>(
    AppArguments(
        title = "Regions",
        templatePath = resolveTemplatePath("applications/settings/configure-regions.hbs"),
        classes = arrayOf("km-dialog-form"),
        submitOnChange = true,
        actions = arrayOf("save", "delete")
    )
) {
    var currentSettings = game.settings.getObject<RegionSettings>("regionSettings")
    override fun getTemplateContext(): Promise<RegionSettingsContext> = buildPromise {
        val playlistOptions = game.playlists?.contents
            ?.mapNotNull { it.toOption() }
            ?.sortedBy { it.label }
            ?: emptyList()
        val rolltableOptions = game.tables?.contents
            ?.mapNotNull { it.toOption() }
            ?.sortedBy { it.label }
            ?: emptyList()
        RegionSettingsContext(
            useStolenLands = CheckboxInput(
                value = currentSettings.useStolenLands,
                name = "useStolenLands",
                label = "Use Stolen Lands",
            ).toContext(),
            heading = arrayOf(
                "Name", "Level", "Zone DC", "Encounter DC", "Roll Table", "Combat Playlist", "Combat Track", "Remove"
            ),
            formRows = currentSettings.regions.mapIndexed { index, row ->
                val trackOptions = row.combatTrack?.playlistId?.let {
                    game.playlists?.get(it)?.sounds?.contents?.mapNotNull { it.toOption() } ?: emptyList()
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
                        name = "regions.$index.combatTrack.playList",
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

    override fun onSubmit(data: FormDataExtended<AnyObject>): Promise<Unit> = buildPromise {
        val obj = expandObjectAnd<RegionSettings>(data.`object`) {
            it["regions"] = it["regions"] ?: emptyArray<RegionSetting>()
        }
        currentSettings = obj
        console.log(data, currentSettings)
    }

    override fun onAction(action: String, event: Event) = buildPromise {
        when (action) {
            "save" -> {
                game.settings.setObject("regionSettings", currentSettings)
                close().await()
            }

            else -> console.log(action)
        }
    }

    override fun onInit() {
        appHooks.onUpdateWorldTime { it, _, _, _ -> console.log(it) }
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
        app = RegionConfiguration(game).app
    )
}