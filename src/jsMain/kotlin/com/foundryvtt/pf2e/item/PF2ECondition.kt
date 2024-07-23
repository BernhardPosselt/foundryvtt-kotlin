package com.foundryvtt.pf2e.item

import com.foundryvtt.core.DatabaseGetOperation
import kotlinx.js.JsPlainObject
import kotlin.js.Promise

@JsPlainObject
external interface PF2EConditionData

@JsName("CONFIG.PF2E.Item.documentClasses.condition")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
external class PF2ECondition : PF2EItem {
    companion object : DocumentStatic<PF2ECondition>

    override fun delete(operation: DatabaseGetOperation): Promise<PF2ECondition>
    override fun update(data: Any, operation: DatabaseGetOperation): Promise<PF2ECondition>

    val system: PF2EConditionData
}