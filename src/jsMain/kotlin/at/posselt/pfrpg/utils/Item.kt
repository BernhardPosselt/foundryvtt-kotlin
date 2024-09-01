package at.posselt.pfrpg.utils

import com.foundryvtt.pf2e.item.itemFromUuid

suspend fun openItem(uuid: String) {
    itemFromUuid(uuid)?.sheet?.launch()
}