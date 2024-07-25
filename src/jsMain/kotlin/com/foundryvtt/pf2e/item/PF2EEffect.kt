package com.foundryvtt.pf2e.item

import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.abstract.DatabaseGetOperation
import js.objects.jso
import kotlinx.js.JsPlainObject
import kotlin.js.Promise

@JsPlainObject
external interface PF2EEffectData

@JsName("CONFIG.PF2E.Item.documentClasses.effect")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
external class PF2EEffect : PF2EItem {
    companion object : DocumentStatic<PF2EEffect>

    override fun delete(operation: DatabaseGetOperation): Promise<PF2EEffect>
    override fun update(data: AnyObject, operation: DatabaseGetOperation): Promise<PF2EEffect>

    val system: PF2EEffectData
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE", "UNCHECKED_CAST")
fun PF2EEffect.update(data: PF2EEffect, operation: DatabaseGetOperation = jso()): Promise<PF2EEffect> =
    update(data as AnyObject, operation)