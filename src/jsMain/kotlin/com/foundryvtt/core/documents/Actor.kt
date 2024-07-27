package com.foundryvtt.core

import com.foundryvtt.core.abstract.DatabaseDeleteOperation
import com.foundryvtt.core.abstract.DatabaseUpdateOperation
import com.foundryvtt.core.abstract.Document
import com.foundryvtt.core.collections.EmbeddedCollection
import com.foundryvtt.core.documents.Folder
import com.foundryvtt.core.documents.Item
import com.foundryvtt.core.documents.Ownership
import com.foundryvtt.core.documents.Token
import js.objects.jso
import kotlin.js.Promise


open external class Actor : Document {
    companion object : DocumentStatic<Actor>

    override fun delete(operation: DatabaseDeleteOperation): Promise<Actor>
    override fun update(data: AnyObject, operation: DatabaseUpdateOperation): Promise<Actor>

    val hasPlayerOwner: Boolean

    // schema
    var name: String
    var img: String?
    var type: String
    var prototypeToken: Token
    var items: EmbeddedCollection<Item>

    // var effects: EmbeddedCollection<Effect>
    var folder: Folder?
    var sort: Int
    var ownership: Ownership
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE", "UNCHECKED_CAST")
fun Actor.update(data: Actor, operation: DatabaseUpdateOperation = jso()): Promise<Actor> =
    update(data as AnyObject, operation)
