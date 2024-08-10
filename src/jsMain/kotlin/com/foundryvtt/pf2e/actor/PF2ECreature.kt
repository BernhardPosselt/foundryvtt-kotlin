package com.foundryvtt.pf2e.actor

import js.objects.Record

external interface PF2ECreature {
    val skills: Record<String, PF2EAttribute>
    val level: Int
    val itemTypes: ItemTypes
    val perception: PF2EAttribute
}