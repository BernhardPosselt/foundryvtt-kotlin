package com.foundryvtt.pf2e.item

import com.foundryvtt.core.DatabaseGetOperation
import kotlinx.js.JsPlainObject
import kotlin.js.Promise

@JsPlainObject
external interface PF2EConsumableData

@JsName("CONFIG.PF2E.Item.documentClasses.consumable")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
external class PF2EConsumable : PF2EItem {
    companion object : DocumentStatic<PF2EConsumable>

    override fun delete(operation: DatabaseGetOperation): Promise<PF2EConsumable>
    override fun update(data: Any, operation: DatabaseGetOperation): Promise<PF2EConsumable>

    val system: PF2EConsumableData
}