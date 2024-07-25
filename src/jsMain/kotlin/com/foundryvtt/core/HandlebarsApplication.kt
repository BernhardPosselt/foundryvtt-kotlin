package com.foundryvtt.core

import js.objects.Record
import kotlinx.js.JsPlainObject
import org.w3c.dom.HTMLElement
import kotlin.js.Promise

@JsPlainObject
external interface HandlebarsTemplatePart {
    val template: String
    val id: String?
    val classes: Array<String>?
    val templates: Array<String>?
    val scrollable: Array<String>?
    val forms: Record<String, ApplicationFormConfiguration>?
}

@JsPlainObject
external interface HandlebarsRenderOptions {
    val parts: Array<String>
}


@JsName("foundry.applications.api.HandlebarsApplicationMixin(foundry.applications.api.ApplicationV2)")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
open external class HandlebarsApplication(
    options: ApplicationConfiguration = definedExternally
) : ApplicationV2 {
    @OptIn(ExperimentalStdlibApi::class)
    @JsExternalInheritorsOnly
    open class HandlebarsApplicationStatic : ApplicationV2Static {
        open var PARTS: Record<String, HandlebarsTemplatePart>
    }

    companion object : HandlebarsApplicationStatic

    open val parts: Record<String, HandlebarsTemplatePart>

    protected fun _preparePartContext(
        partId: String,
        context: ApplicationRenderContext,
        options: HandlebarsRenderOptions
    ): Promise<ApplicationRenderContext>

    protected fun _preSyncPartState(
        partId: String,
        newElement: HTMLElement,
        priorElement: HTMLElement,
        state: Record<String, Any>
    )

    protected fun _syncPartState(
        partId: String,
        newElement: HTMLElement,
        priorElement: HTMLElement,
        state: Record<String, Any>
    )

    protected fun _attachPartListeners(partId: String, htmlElement: HTMLElement, options: ApplicationRenderOptions)
}