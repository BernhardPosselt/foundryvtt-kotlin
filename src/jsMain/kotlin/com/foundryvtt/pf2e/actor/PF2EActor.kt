package com.foundryvtt.pf2e.actor

import com.foundryvtt.core.Actor
import com.foundryvtt.pf2e.PF2ERollData
import com.foundryvtt.pf2e.item.*
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
    fun roll(data: PF2ERollData): Promise<RollResult?>
}

/**
 * Generic superclass that bundles functionality but can not be checked at runtime
 * because the class is not exposed n the scope
 */
@JsName("CONFIG.Actor.documentClass")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
open external class PF2EActor : Actor {
    companion object : DocumentStatic<Any>

    val perception: PF2EAttribute
    val level: Int
    val itemTypes: ItemTypes
}