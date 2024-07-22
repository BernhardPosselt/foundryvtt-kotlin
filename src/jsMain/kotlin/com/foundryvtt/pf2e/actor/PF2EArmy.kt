package com.foundryvtt.pf2e.actor

import kotlinx.js.JsPlainObject

@JsPlainObject
external interface PF2EArmyTraits {
    val type: String // 'skirmisher' | 'cavalry' | 'siege' | 'infantry'
}

@JsPlainObject
external interface PF2EArmyData {
    val recruitmentDC: Int
    val consumption: Int
    val scouting: Int
    val traits: PF2EArmyTraits
}

@JsName("CONFIG.PF2E.Actor.documentClasses.army")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
external class PF2EArmy : PF2EActor {
    companion object : DocumentStatic<Any>

    val system: PF2EArmyData
}