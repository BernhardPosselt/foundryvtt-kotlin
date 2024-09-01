package at.posselt.pfrpg.utils

import com.foundryvtt.core.documents.DrawingDocument

fun DrawingDocument.getRealmTileData(): RealmTileData? =
    getAppFlag("realmTile")

suspend fun DrawingDocument.setRealmTileData(data: RealmTileData) {
    setAppFlag("realmTile", data)
}

suspend fun DrawingDocument.unsetRealmTileData() {
    unsetAppFlag("realmTile")
}