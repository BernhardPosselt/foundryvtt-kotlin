package com.foundryvtt.core.fields

import kotlinx.js.JsPlainObject

@JsPlainObject
external interface DataFieldOptions<T> {
    val required: Boolean?
    val nullable: Boolean?
    val initial: T?
    val readonly: Boolean?
    val gmOnly: Boolean?
    val label: String?
    val hint: String?
    val validationError: String?
}