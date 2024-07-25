package com.foundryvtt.core.collections

import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.abstract.DatabaseGetOperation
import com.foundryvtt.core.abstract.Document
import com.foundryvtt.core.utils.Collection
import kotlinx.js.JsPlainObject
import kotlin.js.Promise

@JsPlainObject
external interface FromCompendiumOptions {
    val clearFolder: Boolean?
    val clearSort: Boolean?
    val clearOwnership: Boolean?
    val keepId: Boolean?
}

open external class WorldCollection<T>(
    data: Array<AnyObject>
) : Collection<T> {
    val folders: Collection<Folders>
    val instance: WorldCollection<T>?

    fun importFromCompendium(
        pack: CompendiumCollection<Document>,
        id: String,
        updateData: AnyObject = definedExternally,
        options: DatabaseGetOperation = definedExternally
    ): Promise<Document>

    fun fromCompendium(document: Document, options: FromCompendiumOptions = definedExternally): Promise<AnyObject>
}