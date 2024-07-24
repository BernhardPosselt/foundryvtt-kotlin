package com.foundryvtt.pf2e.item

import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.DatabaseGetOperation
import com.foundryvtt.core.Item
import js.objects.jso
import kotlin.js.Promise

open external class PF2EItem : Item {
    companion object : DocumentStatic<Item>

    override fun delete(operation: DatabaseGetOperation): Promise<PF2EItem>
    override fun update(data: AnyObject, operation: DatabaseGetOperation): Promise<PF2EItem>
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE", "UNCHECKED_CAST")
fun PF2EItem.update(data: PF2EItem, operation: DatabaseGetOperation = jso()): Promise<PF2EItem> =
    update(data as AnyObject, operation)