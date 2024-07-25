package at.posselt.kingmaker.settings

import at.posselt.kingmaker.dialog.App
import at.posselt.kingmaker.dialog.AppArguments
import at.posselt.kingmaker.dialog.CheckboxInput
import at.posselt.kingmaker.dialog.expandObjectAnd
import at.posselt.kingmaker.utils.buildPromise
import at.posselt.kingmaker.utils.resolveTemplatePath
import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.FormDataExtended
import com.foundryvtt.core.Settings
import com.foundryvtt.core.fields.DataFieldOptions
import com.foundryvtt.core.fields.ObjectField
import js.objects.recordOf
import kotlinx.coroutines.await
import kotlinx.html.org.w3c.dom.events.Event
import kotlinx.js.JsPlainObject
import kotlin.js.Promise

@JsPlainObject
external interface RegionSetting

@JsPlainObject
external interface RegionSettings {
    val useStolenLands: Boolean
    val regions: Array<RegionSetting>
}

class RegionConfiguration(
    val settings: Settings
) : App<AnyObject, AnyObject>(
    AppArguments(
        title = "Regions",
        templatePath = resolveTemplatePath("applications/settings/configure-regions.hbs"),
        classes = arrayOf("km-dialog-form"),
        submitOnChange = true,
        actions = arrayOf("save", "delete")
    )
) {
    var currentSettings = settings.getObject<RegionSettings>("regionSettings")
    override fun getTemplateContext(): Promise<AnyObject> = buildPromise {
        recordOf(
            "useStolenLands" to CheckboxInput(
                value = currentSettings.useStolenLands,
                name = "useStolenLands",
                label = "Use Stolen Lands",
            ).toContext(),
            "heading" to arrayOf(
                "Name", "Level", "Zone DC", "Encounter DC", "Roll Table", "Combat Playlist", "Combat Track", "Remove"
            ),
            "formRows" to emptyArray<Any>()
        )
    }

    override fun onSubmit(data: FormDataExtended<AnyObject>): Promise<Unit> = buildPromise {
        val obj = expandObjectAnd<RegionSettings>(data.`object`) {
            it["regions"] = it["regions"] ?: emptyArray<RegionSetting>()
        }
        currentSettings = obj
        console.log(data, currentSettings)
    }

    override fun onAction(action: String, event: Event) {
        buildPromise {
            when (action) {
                "save" -> {
                    settings.setObject("regionSettings", currentSettings)
                    close().await()
                }

                else -> console.log(action)
            }
        }
    }
}

fun registerRegionSettings(settings: Settings) {
    settings.registerField(
        key = "regionSettings",
        name = "Region Settings",
        type = ObjectField(DataFieldOptions(initial = RegionSettings(useStolenLands = true, regions = emptyArray()))),
    )
    settings.createMenu(
        key = "regionsMenu",
        label = "Configure Regions",
        name = "Region Configuration",
        app = RegionConfiguration(settings).app
    )
}