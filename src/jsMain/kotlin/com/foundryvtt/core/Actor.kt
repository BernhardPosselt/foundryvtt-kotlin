package com.foundryvtt.core

import kotlin.js.Promise


open external class Actor : Document {
    companion object : DocumentStatic<Actor>

    override fun delete(operation: DatabaseGetOperation): Promise<Actor>
    override fun update(data: Any, operation: DatabaseGetOperation): Promise<Actor>

    val name: String?
    val type: String
    val items: EmbeddedCollection<Item>
    val hasPlayerOwner: Boolean
}

