package at.posselt.kingmaker.settings

import at.posselt.kingmaker.app.*
import at.posselt.kingmaker.camping.dialogs.TableHead
import at.posselt.kingmaker.data.regions.Month
import at.posselt.kingmaker.data.regions.Season
import at.posselt.kingmaker.toCamelCase
import at.posselt.kingmaker.toLabel
import at.posselt.kingmaker.utils.buildPromise
import com.foundryvtt.core.*
import com.foundryvtt.core.abstract.DataModel
import com.foundryvtt.core.abstract.DocumentConstructionContext
import com.foundryvtt.core.applications.api.*
import com.foundryvtt.core.data.dsl.buildSchema
import kotlinx.coroutines.await
import kotlinx.js.JsPlainObject
import org.w3c.dom.HTMLElement
import org.w3c.dom.get
import org.w3c.dom.pointerevents.PointerEvent
import kotlin.js.Promise

fun getDefaultMonths(): Array<ClimateSetting> = arrayOf(
    ClimateSetting(season = Season.WINTER.toCamelCase()),
    ClimateSetting(season = Season.WINTER.toCamelCase()),
    ClimateSetting(season = Season.SPRING.toCamelCase()),
    ClimateSetting(season = Season.SPRING.toCamelCase()),
    ClimateSetting(season = Season.SPRING.toCamelCase()),
    ClimateSetting(season = Season.SUMMER.toCamelCase()),
    ClimateSetting(season = Season.SUMMER.toCamelCase()),
    ClimateSetting(season = Season.SUMMER.toCamelCase()),
    ClimateSetting(season = Season.FALL.toCamelCase()),
    ClimateSetting(season = Season.FALL.toCamelCase()),
    ClimateSetting(season = Season.FALL.toCamelCase()),
    ClimateSetting(season = Season.WINTER.toCamelCase()),
)

@JsPlainObject
external interface ClimateSetting {
    val coldDc: Int?
    val precipitationDc: Int?
    val season: String
    val weatherEventDc: Int?
}

@JsPlainObject
external interface ClimateSettings {
    var useStolenLands: Boolean
    var months: Array<ClimateSetting>
}

@JsPlainObject
external interface ClimateSettingsContext : HandlebarsRenderContext {
    var useStolenLands: FormElementContext
    var heading: Array<TableHead>
    var formRows: Array<Array<Any>>
    var isValid: Boolean
}

@OptIn(ExperimentalJsExport::class)
@JsExport
class ClimateConfigurationDataModel(
    value: AnyObject? = undefined,
    context: DocumentConstructionContext? = undefined,
) : DataModel(value, context) {
    companion object {
        @Suppress("unused")
        @OptIn(ExperimentalJsStatic::class)
        @JsStatic
        fun defineSchema() = buildSchema {
            boolean("useStolenLands") {
                initial = true
            }
            array<ClimateSetting>("months") {
                options {
                    initial = getDefaultMonths()
                }
                schema {
                    int("coldDc", nullable = true)
                    int("precipitationDc", nullable = true)
                    int("weatherEventDc", nullable = true)
                    string("season")
                }
            }
        }
    }
}

@OptIn(ExperimentalJsExport::class)
@JsExport
class ClimateConfiguration : FormApp<ClimateSettingsContext, ClimateSettings>(
    title = "Climate",
    width = 1024,
    template = "applications/settings/configure-climate.hbs",
    debug = true,
    dataModel = ClimateConfigurationDataModel::class.js,
) {
    private var currentSettings = game.settings.kingmakerTools.getClimateSettings()

    override fun _onClickAction(event: PointerEvent, target: HTMLElement) {
        when (val action = target.dataset["action"]) {
            "save" -> {
                buildPromise {
                    game.settings.kingmakerTools.setClimateSettings(currentSettings)
                    close()
                }
            }

            else -> console.log(action)
        }
    }

    override fun _preparePartContext(
        partId: String,
        context: HandlebarsRenderContext,
        options: HandlebarsRenderOptions
    ): Promise<ClimateSettingsContext> = buildPromise {
        val parent = super._preparePartContext(partId, context, options).await()
        ClimateSettingsContext(
            partId = parent.partId,
            isValid = isFormValid,
            useStolenLands = CheckboxInput(
                value = currentSettings.useStolenLands,
                name = "useStolenLands",
                label = "Use Stolen Lands",
            ).toContext(),
            heading = arrayOf(
                TableHead("Month"),
                TableHead("Season"),
                TableHead("Cold DC", arrayOf("number-select-heading")),
                TableHead("Precipitation DC", arrayOf("number-select-heading")),
                TableHead("Weather Event DC", arrayOf("number-select-heading")),
            ),
            formRows = currentSettings.months.mapIndexed { index, row ->
                val month = Month.entries[index]
                arrayOf(
                    month.toLabel(),
                    Select.fromEnum<Season>(
                        name = "months.$index.season",
                        label = "Season",
                        value = Season.entries.find { it.toCamelCase() == row.season },
                        hideLabel = true
                    ).toContext(),
                    Select.flatCheck(
                        name = "months.$index.coldDc",
                        label = "Cold DC",
                        value = row.coldDc,
                        hideLabel = true,
                        required = false,
                    ).toContext(),
                    Select.flatCheck(
                        name = "months.$index.precipitationDc",
                        label = "Precipitation DC",
                        value = row.precipitationDc,
                        hideLabel = true,
                        required = false,
                    ).toContext(),
                    Select.flatCheck(
                        name = "months.$index.weatherEventDc",
                        label = "Precipitation DC",
                        value = row.weatherEventDc,
                        hideLabel = true,
                        required = false,
                    ).toContext(),
                )
            }.toTypedArray()
        )
    }

    override fun fixObject(value: dynamic) {
        value["months"] = (value["months"] as Array<ClimateSetting>?) ?: getDefaultMonths()
    }

    override fun onParsedSubmit(value: ClimateSettings) = buildPromise {
        currentSettings = value
        null
    }
}