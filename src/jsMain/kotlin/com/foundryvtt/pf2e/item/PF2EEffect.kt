package com.foundryvtt.pf2e.item

import com.foundryvtt.core.DatabaseGetOperation
import kotlinx.js.JsPlainObject
import kotlin.js.Promise

@JsPlainObject
external interface PF2EEffectData

@JsName("CONFIG.PF2E.Item.documentClasses.effect")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
external class PF2EEffect : PF2EItem {
    companion object : DocumentStatic<PF2EEffect>

    override fun delete(operation: DatabaseGetOperation): Promise<PF2EEffect>
    override fun update(data: Any, operation: DatabaseGetOperation): Promise<PF2EEffect>

    val system: PF2EEffectData
}