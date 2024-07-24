package com.foundryvtt.core

import js.objects.jso
import kotlin.js.Promise

open external class Item : Document {
    companion object : DocumentStatic<Item>

    override fun delete(operation: DatabaseGetOperation): Promise<Item>
    override fun update(data: AnyObject, operation: DatabaseGetOperation): Promise<Item>

    val name: String?
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE", "UNCHECKED_CAST")
fun Item.update(data: Item, operation: DatabaseGetOperation = jso()): Promise<Item> =
    update(data as AnyObject, operation)