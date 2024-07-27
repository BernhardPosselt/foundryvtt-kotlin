package com.foundryvtt.core.documents

import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.abstract.DatabaseDeleteOperation
import com.foundryvtt.core.abstract.DatabaseUpdateOperation
import com.foundryvtt.core.abstract.Document
import js.objects.jso
import kotlin.js.Promise

open external class Item : Document {
    companion object : DocumentStatic<Item>

    override fun delete(operation: DatabaseDeleteOperation): Promise<Item>
    override fun update(data: AnyObject, operation: DatabaseUpdateOperation): Promise<Item>

    val name: String?
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE", "UNCHECKED_CAST")
fun Item.update(data: Item, operation: DatabaseUpdateOperation = jso()): Promise<Item> =
    update(data as AnyObject, operation)