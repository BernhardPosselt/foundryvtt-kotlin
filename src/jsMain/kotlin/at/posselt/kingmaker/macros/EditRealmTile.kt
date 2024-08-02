package at.posselt.kingmaker.macros

import at.posselt.kingmaker.app.Select
import at.posselt.kingmaker.app.SelectOption
import at.posselt.kingmaker.app.formContext
import at.posselt.kingmaker.app.prompt
import at.posselt.kingmaker.deCamelCase
import at.posselt.kingmaker.utils.RealmTileData
import at.posselt.kingmaker.utils.getRealmTileData
import at.posselt.kingmaker.utils.setRealmTileData
import at.posselt.kingmaker.utils.unsetRealmTileData
import com.foundryvtt.core.Game
import com.foundryvtt.core.ui
import js.objects.recordOf
import kotlinx.js.JsPlainObject

@JsPlainObject
external interface EditRealmTileData {
    val type: String?
}

private val options = listOf(
    "mine",
    "ore",
    "lumber",
    "lumberCamp",
    "quarry",
    "stone",
    "luxury",
    "luxuryWorksite",
    "farmland",
    "food",
    "claimed",
).map {
    SelectOption(label = it.deCamelCase(), value = it)
}

suspend fun editRealmTileMacro(game: Game) {
    val drawings = game.canvas.drawings.controlled.map { it.document }
    val tiles = game.canvas.tiles.controlled.map { it.document }
    if (drawings.isEmpty() || tiles.isEmpty()) {
        ui.notifications.error("Please select drawings or tiles")
        return
    }
    val data = drawings.firstOrNull()?.getRealmTileData()
        ?: tiles.firstOrNull()?.getRealmTileData()

    prompt<EditRealmTileData, Unit>(
        title = "Edit Selected Realm Tiles/Drawings",
        templatePath = "components/forms/form.hbs",
        templateContext = recordOf(
            "formRows" to formContext(
                Select(
                    name = "type",
                    label = "Realm Tile/Drawing Type",
                    options = options,
                    value = data?.type,
                    required = false,
                )
            )
        )
    ) {
        val type = it.type
        if (type == null) {
            drawings.forEach { drawing -> drawing.unsetRealmTileData() }
            tiles.forEach { drawing -> drawing.unsetRealmTileData() }
        } else {
            drawings.forEach { drawing -> drawing.setRealmTileData(RealmTileData(type = type)) }
            tiles.forEach { drawing -> drawing.setRealmTileData(RealmTileData(type = type)) }
        }
    }
}