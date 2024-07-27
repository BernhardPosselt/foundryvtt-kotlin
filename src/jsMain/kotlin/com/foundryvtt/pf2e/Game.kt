package com.foundryvtt.pf2e

import com.foundryvtt.core.Game
import com.foundryvtt.core.utils.Collection
import com.foundryvtt.luxon.DateTime
import com.foundryvtt.pf2e.actor.PF2ECharacter
import kotlinx.js.JsPlainObject
import kotlin.js.Promise

@JsPlainObject
external interface PF2EActionMacroUseOptions {
    val actors: Array<PF2ECharacter>
}

external class PF2EActionMacro {
    fun use(options: PF2EActionMacroUseOptions): Promise<Any?>
}

@JsPlainObject
external interface RestForTheNightOptions {
    val actors: Array<PF2ECharacter>
    val skilDialog: Boolean?
}

@JsPlainObject
external interface SubsistOptions {
    val actors: Array<PF2ECharacter>
    val skill: String
    val difficultyClass: Dc?
}

external class PF2EActionMacros : Collection<PF2EActionMacro> {
    fun restForTheNight(options: RestForTheNightOptions): Promise<Unit>
    fun subsist(options: SubsistOptions)
}

@JsPlainObject
external interface PF2EWorldClock {
    val worldTime: DateTime
    val month: String
}

@JsPlainObject
external interface PF2EGame {
    val actions: PF2EActionMacros
    val worldClock: PF2EWorldClock
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
inline val Game.pf2e: PF2EGame
    get() = asDynamic().pf2e as PF2EGame