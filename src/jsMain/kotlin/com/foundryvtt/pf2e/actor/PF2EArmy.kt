package com.foundryvtt.pf2e.actor

import com.foundryvtt.core.DatabaseGetOperation
import kotlinx.js.JsPlainObject
import kotlin.js.Promise

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
    companion object : DocumentStatic<PF2EArmy>

    override fun delete(operation: DatabaseGetOperation): Promise<PF2EArmy>
    override fun update(data: Any, operation: DatabaseGetOperation): Promise<PF2EArmy>

    val system: PF2EArmyData
}