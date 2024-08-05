package at.posselt.kingmaker.camping

import at.posselt.kingmaker.app.FormApp
import at.posselt.kingmaker.app.MenuControl
import at.posselt.kingmaker.utils.buildPromise
import com.foundryvtt.core.applications.api.HandlebarsRenderOptions
import js.core.Void
import kotlinx.js.JsPlainObject
import kotlin.js.Promise

@JsPlainObject
external interface CampingSheetContext {
    val size: Array<Int>
}

@JsPlainObject
external interface CampingSheetFormData


@OptIn(ExperimentalJsExport::class)
@JsExport
class CampingSheet : FormApp<CampingSheetContext, CampingSheetFormData>(
    title = "Camping",
    template = "applications/camping/camping-sheet.hbs",
    width = 970,
    classes = arrayOf("km-camping-sheet"),
    controls = arrayOf(
        MenuControl(label = "Show Players", action = "show-players"),
        MenuControl(label = "Activities", action = "activities"),
        MenuControl(label = "Recipes", action = "recipes"),
        MenuControl(label = "Settings", action = "settings"),
        MenuControl(label = "Help", action = "help"),
    )
) {
    override fun _preparePartContext(
        partId: String,
        context: CampingSheetContext,
        options: HandlebarsRenderOptions
    ): Promise<CampingSheetContext> = buildPromise {
        CampingSheetContext(size = arrayOf(1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6))
    }

    override fun onParsedSubmit(value: CampingSheetFormData): Promise<Void> {
        TODO("Not yet implemented")
    }
}