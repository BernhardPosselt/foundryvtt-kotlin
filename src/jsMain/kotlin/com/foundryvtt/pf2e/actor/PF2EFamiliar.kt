package com.foundryvtt.pf2e.actor

import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.abstract.DatabaseGetOperation
import js.objects.Record
import js.objects.jso
import kotlinx.js.JsPlainObject
import kotlin.js.Promise

@JsPlainObject
external interface PF2EFamiliarData

@JsName("CONFIG.PF2E.Actor.documentClasses.familiar")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
external class PF2EFamiliar : PF2EActor {
    companion object : DocumentStatic<PF2EFamiliar>

    override fun delete(operation: DatabaseGetOperation): Promise<PF2EFamiliar>
    override fun update(data: AnyObject, operation: DatabaseGetOperation): Promise<PF2EFamiliar>

    val skills: Record<String, PF2EAttribute>
    val system: PF2EFamiliarData
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE", "UNCHECKED_CAST")
fun PF2EFamiliar.update(data: PF2EFamiliar, operation: DatabaseGetOperation = jso()): Promise<PF2EFamiliar> =
    update(data as AnyObject, operation)