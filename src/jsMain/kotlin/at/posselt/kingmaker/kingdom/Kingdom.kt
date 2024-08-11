package at.posselt.kingmaker.kingdom

import at.posselt.kingmaker.utils.getAppFlag
import at.posselt.kingmaker.utils.setAppFlag
import com.foundryvtt.core.utils.deepClone
import com.foundryvtt.pf2e.actor.PF2ENpc


fun PF2ENpc.getKingdom(): KingdomData? =
    getAppFlag<PF2ENpc, KingdomData?>("kingdom-sheet")
        ?.let(::deepClone)

suspend fun PF2ENpc.setKingdom(data: KingdomData) {
    setAppFlag("kingdom-sheet", data)
}