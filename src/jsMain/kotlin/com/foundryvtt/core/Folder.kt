package com.foundryvtt.core

import kotlinx.js.JsPlainObject
import kotlin.js.Promise

@JsPlainObject
external interface ExportCompendiumOptions {
    val updateByName: Boolean?
    val keepId: Boolean?
    val keepFolder: Boolean?
    val folder: String?
}

external class Folder(
    data: AnyObject = definedExternally,
    options: DocumentConstructionContext = definedExternally
) : Document {
    val name: String
    val description: String
    val folder: Folder
    val sorting: String
    val sort: Int
    val depth: Int
    val children: Array<Folder>
    val displayed: Boolean
    var contents: Array<Any>
    val expanded: Boolean
    val ancestors: Array<Folder>
    fun exportToCompendium(
        pack: DocumentCollection<Document>,
        options: ExportCompendiumOptions = definedExternally
    ): Promise<CompendiumCollection<Folder>>

    fun exportDialog(pack: String, options: PromptOptions = definedExternally): Promise<Unit>
    fun getSubFolders(recursive: Boolean = definedExternally): Array<Folder>
    fun getParentFolders(): Array<Folder>
}