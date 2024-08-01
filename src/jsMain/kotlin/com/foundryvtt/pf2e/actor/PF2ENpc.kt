package com.foundryvtt.pf2e.actor

import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.abstract.DatabaseDeleteOperation
import com.foundryvtt.core.abstract.DatabaseUpdateOperation
import js.objects.jso
import kotlinx.js.JsPlainObject
import kotlin.js.Promise

@JsPlainObject
external interface PF2ENpcData

// required to make instance of work, but since the classes are not registered here
// at page load, we can't use @file:JsQualifier
@JsName("CONFIG.PF2E.Actor.documentClasses.npc")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
external class PF2ENpc : PF2EActor {
    companion object : DocumentStatic<PF2ENpc>

    override fun delete(operation: DatabaseDeleteOperation): Promise<PF2ENpc>
    override fun update(data: AnyObject, operation: DatabaseUpdateOperation): Promise<PF2ENpc?>

    val system: PF2ENpcData
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE", "UNCHECKED_CAST")
fun PF2ENpc.update(data: PF2ENpc, operation: DatabaseUpdateOperation = jso()): Promise<PF2ENpc?> =
    update(data as AnyObject, operation)