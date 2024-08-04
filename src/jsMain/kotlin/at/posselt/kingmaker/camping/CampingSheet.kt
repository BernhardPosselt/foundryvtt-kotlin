package at.posselt.kingmaker.camping

import at.posselt.kingmaker.app.FormApp
import at.posselt.kingmaker.app.MenuControl
import js.core.Void
import kotlinx.js.JsPlainObject
import kotlin.js.Promise

@JsPlainObject
external interface CampingSheetContext

@JsPlainObject
external interface CampingSheetFormData


@OptIn(ExperimentalJsExport::class)
@JsExport
class CampingSheet : FormApp<CampingSheetContext, CampingSheetFormData>(
    title = "Camping",
    template = "applications/camping/camping-sheet.hbs",
    width = 960,
    classes = arrayOf("km-camping-sheet"),
    controls = arrayOf(
        MenuControl(label = "Show Players", action = "show-players"),
        MenuControl(label = "Activities", action = "activities"),
        MenuControl(label = "Recipes", action = "recipes"),
        MenuControl(label = "Settings", action = "settings"),
        MenuControl(label = "Help", action = "help"),
    )
) {
    override fun onParsedSubmit(value: CampingSheetFormData): Promise<Void> {
        TODO("Not yet implemented")
    }
}