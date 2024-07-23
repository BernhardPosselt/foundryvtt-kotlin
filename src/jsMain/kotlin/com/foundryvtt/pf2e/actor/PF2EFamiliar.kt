package com.foundryvtt.pf2e.actor

import com.foundryvtt.core.DatabaseGetOperation
import js.objects.Record
import kotlinx.js.JsPlainObject
import kotlin.js.Promise

@JsPlainObject
external interface PF2EFamiliarData

@JsName("CONFIG.PF2E.Actor.documentClasses.familiar")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
external class PF2EFamiliar : PF2EActor {
    companion object : DocumentStatic<PF2EFamiliar>

    override fun delete(operation: DatabaseGetOperation): Promise<PF2EFamiliar>
    override fun update(data: Any, operation: DatabaseGetOperation): Promise<PF2EFamiliar>

    val skills: Record<String, PF2EAttribute>
    val system: PF2EFamiliarData
}