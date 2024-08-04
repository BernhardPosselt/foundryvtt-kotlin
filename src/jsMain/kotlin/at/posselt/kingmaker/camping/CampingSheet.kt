package at.posselt.kingmaker.camping

import at.posselt.kingmaker.app.FormApp
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
    id = "km-camping-sheet",
    width = 1024,
) {
    override fun onParsedSubmit(value: CampingSheetFormData): Promise<Void> {
        TODO("Not yet implemented")
    }
}