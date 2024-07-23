package com.foundryvtt.core

import kotlinx.js.JsPlainObject
import kotlin.js.Promise

external fun renderTemplate(path: String, data: Any): Promise<String>

@JsPlainObject
external interface HandlebarOptions {
    // if needed, check handlebars RuntimeOptions
}

typealias HandlebarsTemplateDelegate = (Any, HandlebarOptions) -> Promise<String>

external fun loadTemplates(paths: Array<String>): Promise<Array<HandlebarsTemplateDelegate>>