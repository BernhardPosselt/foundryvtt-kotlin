package at.posselt.kingmaker.actor

import at.posselt.kingmaker.kingdom.StructureData
import at.posselt.kingmaker.utils.getAppFlag
import at.posselt.kingmaker.utils.setAppFlag
import at.posselt.kingmaker.utils.unsetAppFlag
import com.foundryvtt.pf2e.actor.PF2ENpc


fun PF2ENpc.getStructureData(): StructureData? =
    getAppFlag("structureData")

suspend fun PF2ENpc.setStructureData(data: StructureData) {
    setAppFlag("structureData", data)
}

suspend fun PF2ENpc.unsetStructureData() {
    unsetAppFlag("structureData")
}