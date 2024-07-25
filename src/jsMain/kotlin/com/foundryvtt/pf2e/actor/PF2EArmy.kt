package com.foundryvtt.pf2e.actor

import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.abstract.DatabaseGetOperation
import js.objects.jso
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
    override fun update(data: AnyObject, operation: DatabaseGetOperation): Promise<PF2EArmy>

    val system: PF2EArmyData
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE", "UNCHECKED_CAST")
fun PF2EArmy.update(data: PF2EArmy, operation: DatabaseGetOperation = jso()): Promise<PF2EArmy> =
    update(data as AnyObject, operation)