package com.foundryvtt.core

import com.foundryvtt.core.abstract.DatabaseGetOperation
import com.foundryvtt.core.abstract.Document
import com.foundryvtt.core.collections.EmbeddedCollection
import com.foundryvtt.core.documents.Item
import js.objects.jso
import kotlin.js.Promise


open external class Actor : Document {
    companion object : DocumentStatic<Actor>

    override fun delete(operation: DatabaseGetOperation): Promise<Actor>
    override fun update(data: AnyObject, operation: DatabaseGetOperation): Promise<Actor>

    var name: String?
    val type: String
    val items: EmbeddedCollection<Item>
    val hasPlayerOwner: Boolean
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE", "UNCHECKED_CAST")
fun Actor.update(data: Actor, operation: DatabaseGetOperation = jso()): Promise<Actor> =
    update(data as AnyObject, operation)
