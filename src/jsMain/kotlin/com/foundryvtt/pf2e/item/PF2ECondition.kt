package com.foundryvtt.pf2e.item

import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.abstract.DatabaseGetOperation
import js.objects.jso
import kotlinx.js.JsPlainObject
import kotlin.js.Promise

@JsPlainObject
external interface PF2EConditionData

// required to make instance of work, but since the classes are not registered here
// at page load, we can't use @file:JsQualifier
@JsName("CONFIG.PF2E.Item.documentClasses.condition")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
external class PF2ECondition : PF2EItem {
    companion object : DocumentStatic<PF2ECondition>

    override fun delete(operation: DatabaseGetOperation): Promise<PF2ECondition>
    override fun update(data: AnyObject, operation: DatabaseGetOperation): Promise<PF2ECondition>

    val system: PF2EConditionData
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE", "UNCHECKED_CAST")
fun PF2ECondition.update(data: PF2ECondition, operation: DatabaseGetOperation = jso()): Promise<PF2ECondition> =
    update(data as AnyObject, operation)