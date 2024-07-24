package com.foundryvtt.kingmaker

import com.foundryvtt.core.Module
import js.objects.Record
import kotlinx.js.JsPlainObject

@JsPlainObject
external interface HexState {
    val commodity: Array<String>?
    val camp: Array<String>?
    val features: Array<String>?
    val claimed: Boolean?
}


@JsPlainObject
external interface KingmakerState {
    val hexes: Record<Int, HexState>
}

external class KingmakerModule : Module {
    val state: KingmakerState
}

external val kingmaker: KingmakerModule