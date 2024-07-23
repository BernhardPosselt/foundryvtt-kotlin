package com.foundryvtt.core

import kotlin.js.Promise

open external class Item : Document {
    companion object : DocumentStatic<Item>

    override fun delete(operation: DatabaseGetOperation): Promise<Item>
    override fun update(data: Any, operation: DatabaseGetOperation): Promise<Item>

    val name: String?
}