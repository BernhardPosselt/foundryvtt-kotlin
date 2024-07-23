package com.foundryvtt.pf2e.item

import com.foundryvtt.core.DatabaseGetOperation
import com.foundryvtt.core.Item
import kotlin.js.Promise

open external class PF2EItem : Item {
    companion object : DocumentStatic<Item>

    override fun delete(operation: DatabaseGetOperation): Promise<PF2EItem>
    override fun update(data: Any, operation: DatabaseGetOperation): Promise<PF2EItem>
}