package com.foundryvtt.core

import js.objects.Record
import kotlinx.js.JsPlainObject
import kotlin.js.Promise

external fun renderTemplate(path: String, data: Any): Promise<String>

@JsPlainObject
external interface HandlebarOptions {
    // if needed, check handlebars RuntimeOptions
}

typealias HandlebarsTemplateDelegate = (Any, HandlebarOptions) -> Promise<String>

external fun loadTemplates(paths: Array<String>): Promise<Array<HandlebarsTemplateDelegate>>
external fun loadTemplates(paths: Record<String, String>): Promise<Array<HandlebarsTemplateDelegate>>