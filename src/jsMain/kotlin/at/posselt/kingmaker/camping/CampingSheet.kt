package at.posselt.kingmaker.camping

import at.posselt.kingmaker.app.FormApp
import at.posselt.kingmaker.app.MenuControl
import at.posselt.kingmaker.utils.buildPromise
import com.foundryvtt.core.applications.api.HandlebarsRenderOptions
import js.core.Void
import kotlinx.js.JsPlainObject
import kotlin.js.Promise

@JsPlainObject
external interface CampingSheetActor {
    val name: String
    val uuid: String
    val image: String
}

@JsPlainObject
external interface CampingSheetContext {
    val actors: Array<CampingSheetActor>
}

@JsPlainObject
external interface CampingSheetFormData


@OptIn(ExperimentalJsExport::class)
@JsExport
class CampingSheet : FormApp<CampingSheetContext, CampingSheetFormData>(
    title = "Camping",
    template = "applications/camping/camping-sheet.hbs",
    width = 970,
    resizable = true,
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
        CampingSheetContext(
            actors = arrayOf(
                CampingSheetActor(
                    image = "/systems/pf2e/icons/iconics/AmiriFull.webp",
                    uuid = "uuid",
                    name = "Amiri",
                ),
                CampingSheetActor(
                    image = "/Portrait%20-%20Tar.webp",
                    uuid = "uuid",
                    name = "Tar"
                ),
                CampingSheetActor(
                    image = "/systems/pf2e/icons/iconics/LiniFull.webp",
                    name = "Lini",
                    uuid = "uuid"
                ),
                CampingSheetActor(
                    image = "/systems/pf2e/icons/iconics/SeelahFull.webp",
                    name = "Seelah",
                    uuid = "uuid"
                ),
                CampingSheetActor(
                    image = "/systems/pf2e/icons/iconics/EzrenFull.webp",
                    name = "Very Long Nameforezrenthatiswaytoolong",
                    uuid = "uuid"
                ),
                CampingSheetActor(
                    image = "/systems/pf2e/icons/iconics/FumbusFull.webp",
                    name = "john",
                    uuid = "uuid"
                ),
                CampingSheetActor(
                    image = "/systems/pf2e/icons/iconics/LemFull.webp",
                    name = "john",
                    uuid = "uuid"
                ),
                CampingSheetActor(
                    image = "/systems/pf2e/icons/iconics/QuinnFull.webp",
                    name = "john",
                    uuid = "uuid"
                ),
                CampingSheetActor(
                    image = "/systems/pf2e/icons/iconics/ValerosFull.webp",
                    name = "john",
                    uuid = "uuid"
                ),
                CampingSheetActor(
                    image = "/systems/pf2e/icons/iconics/ValerosFull.webp",
                    name = "john",
                    uuid = "uuid"
                ),
                CampingSheetActor(
                    image = "/systems/pf2e/icons/iconics/ValerosFull.webp",
                    name = "john",
                    uuid = "uuid"
                ),
//                CampingSheetActor(
//                    image = "/systems/pf2e/icons/iconics/ValerosFull.webp",
//                    name = "john",
//                    uuid = "uuid"
//                ),
//                CampingSheetActor(
//                    image = "/systems/pf2e/icons/iconics/ValerosFull.webp",
//                    name = "john",
//                    uuid = "uuid"
//                ),
//                CampingSheetActor(
//                    image = "/systems/pf2e/icons/iconics/ValerosFull.webp",
//                    name = "john",
//                    uuid = "uuid"
//                ),
//                CampingSheetActor(
//                    image = "/systems/pf2e/icons/iconics/ValerosFull.webp",
//                    name = "john",
//                    uuid = "uuid"
//                ),
//                CampingSheetActor(
//                    image = "/systems/pf2e/icons/iconics/ValerosFull.webp",
//                    name = "john",
//                    uuid = "uuid"
//                ),
            )
        )
    }

    override fun onParsedSubmit(value: CampingSheetFormData): Promise<Void> {
        TODO("Not yet implemented")
    }
}