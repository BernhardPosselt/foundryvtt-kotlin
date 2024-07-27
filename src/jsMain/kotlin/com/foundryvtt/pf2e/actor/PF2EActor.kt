package com.foundryvtt.pf2e.actor

import com.foundryvtt.core.Actor
import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.abstract.DatabaseGetOperation
import com.foundryvtt.pf2e.PF2ERollData
import com.foundryvtt.pf2e.item.*
import js.objects.jso
import kotlinx.js.JsPlainObject
import kotlin.js.Promise

@JsPlainObject
external interface RollResult {
    val degreeOfSuccess: Int
}

@JsPlainObject
external interface ItemTypes {
    val consumable: Array<PF2EConsumable>
    val effect: Array<PF2EEffect>
    val equipment: Array<PF2EEquipment>
    val action: Array<PF2EAction>
    val condition: Array<PF2ECondition>
}

@JsPlainObject
external interface PF2EActorData

external class PF2EAttribute {
    val rank: Int
    fun roll(data: PF2ERollData): Promise<RollResult>
}

/**
 * Generic superclass that bundles functionality but can not be checked at runtime
 * because the class is not exposed n the scope
 */
@JsName("CONFIG.Actor.documentClass")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
open external class PF2EActor : Actor {
    companion object : DocumentStatic<Actor>

    override fun delete(operation: DatabaseGetOperation): Promise<PF2EActor>
    override fun update(data: AnyObject, operation: DatabaseGetOperation): Promise<PF2EActor>

    val perception: PF2EAttribute
    val level: Int
    val itemTypes: ItemTypes
}

// required to make instance of work, but since the classes are not registered here
// at page load, we can't use @file:JsQualifier
@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE", "UNCHECKED_CAST")
fun PF2EActor.update(data: PF2EActor, operation: DatabaseGetOperation = jso()): Promise<PF2EActor> =
    update(data as AnyObject, operation)