package at.posselt.kingmaker.rolltables

import at.posselt.kingmaker.Config
import at.posselt.kingmaker.data.checks.RollMode
import at.posselt.kingmaker.toCamelCase
import com.foundryvtt.core.Game
import com.foundryvtt.core.documents.DrawOptions
import com.foundryvtt.core.documents.RollTable
import com.foundryvtt.core.documents.RollTableDraw
import kotlinx.coroutines.await

data class TableAndDraw(val table: RollTable, val draw: RollTableDraw)

suspend fun Game.rollWithCompendiumFallback(
    tableName: String,
    rollMode: RollMode,
    displayChat: Boolean? = true,
    fallbackName: String? = null,
    compendium: String = Config.rollTables.compendium,
): TableAndDraw? {
    val table = tables.getName(tableName)
        ?: packs.get(compendium)
            ?.getDocuments()
            ?.await()
            ?.filterIsInstance<RollTable>()
            ?.find { it.name == (fallbackName ?: tableName) }
    return table?.let {
        val roll = it.draw(DrawOptions(rollMode = rollMode.toCamelCase(), displayChat = displayChat)).await()
        TableAndDraw(it, roll)
    }
}