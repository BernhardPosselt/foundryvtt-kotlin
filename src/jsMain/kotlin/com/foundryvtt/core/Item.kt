package com.foundryvtt.core

open external class Item : Document {
    companion object : DocumentStatic<Any>

    val name: String?
}