package com.foundryvtt.pf2e.item

import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.abstract.DatabaseGetOperation
import kotlinx.js.JsPlainObject
import kotlin.js.Promise

@JsPlainObject
external interface PF2EActionData

// required to make instance of work, but since the classes are not registered here
// at page load, we can't use @file:JsQualifier
@JsName("CONFIG.PF2E.Item.documentClasses.action")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
external class PF2EAction : PF2EItem {
    companion object : DocumentStatic<PF2EAction>

    override fun delete(operation: DatabaseGetOperation): Promise<PF2EAction>
    override fun update(data: AnyObject, operation: DatabaseGetOperation): Promise<PF2EAction>

    val system: PF2EActionData
}
