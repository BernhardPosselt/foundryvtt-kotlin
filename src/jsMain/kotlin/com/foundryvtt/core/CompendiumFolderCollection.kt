package com.foundryvtt.core

open external class CompendiumFolderCollection(
    pack: Document,
    data: Array<AnyObject>
) : DocumentCollection<Folder> {
    val pack: Document
}