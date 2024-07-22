package com.foundryvtt.pf2e

import com.foundryvtt.core.Game
import com.foundryvtt.pf2e.actor.PF2ECharacter
import js.collections.JsMap
import kotlinx.js.JsPlainObject
import kotlin.js.Promise

@JsPlainObject
external interface PF2EActionMacroUseOptions {
    val actors: Array<PF2ECharacter>
}

external interface PF2EActionMacro {
    fun use(options: PF2EActionMacroUseOptions): Promise<Any?>
}

external interface PF2EGame {
    val actions: JsMap<String, PF2EActionMacro>
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
inline val Game.pf2e: PF2EGame
    get() = asDynamic().pf2e as PF2EGame