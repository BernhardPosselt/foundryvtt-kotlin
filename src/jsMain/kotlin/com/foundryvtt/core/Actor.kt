package com.foundryvtt.core


open external class Actor : Document {
    companion object : DocumentStatic<Any>

    val name: String?
    val type: String
    val items: EmbeddedCollection<Item>
    val hasPlayerOwner: Boolean
}

