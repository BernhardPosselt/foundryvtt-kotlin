package com.foundryvtt.pf2e.item

import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.abstract.DatabaseDeleteOperation
import com.foundryvtt.core.abstract.DatabaseUpdateOperation
import com.foundryvtt.core.documents.Item
import js.objects.jso
import kotlin.js.Promise

open external class PF2EItem : Item {
    companion object : DocumentStatic<Item>

    override fun delete(operation: DatabaseDeleteOperation): Promise<PF2EItem>
    override fun update(data: AnyObject, operation: DatabaseUpdateOperation): Promise<PF2EItem>
    val sourceId: String
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE", "UNCHECKED_CAST")
fun PF2EItem.update(data: PF2EItem, operation: DatabaseUpdateOperation = jso()): Promise<PF2EItem> =
    update(data as AnyObject, operation)