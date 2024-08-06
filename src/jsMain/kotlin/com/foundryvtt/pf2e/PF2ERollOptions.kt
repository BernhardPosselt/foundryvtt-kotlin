package com.foundryvtt.pf2e

import kotlinx.js.JsPlainObject

@JsPlainObject
external interface Dc {
    val value: Int
}

@JsPlainObject
external interface PF2ERollOptions {
    val rollMode: String?
    val dc: Dc?
    val extraRollOptions: Array<String>?
}