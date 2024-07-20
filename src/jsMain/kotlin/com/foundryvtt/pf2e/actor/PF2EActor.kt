package com.foundryvtt.pf2e.actor

import com.foundryvtt.core.Actor
import com.foundryvtt.pf2e.RollData
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

external class PF2EActorSkill {
    val rank: Int
    fun roll(data: RollData): Promise<RollResult?>
}


open external class PF2EActor<D> : Actor<D> {
    val perception: PF2EActorSkill
    val level: Int
    val itemTypes: ItemTypes
}