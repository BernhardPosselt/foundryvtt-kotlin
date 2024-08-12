package at.posselt.kingmaker.app

import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.FormDataExtended
import com.foundryvtt.core.applications.api.*
import js.core.Void
import js.objects.Record
import kotlinx.html.org.w3c.dom.events.Event
import kotlinx.js.JsPlainObject
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLFormElement
import kotlin.js.Promise

@JsPlainObject
external interface HandlebarsFormApplicationOptions : ApplicationConfiguration {
    val parts: Record<String, HandlebarsTemplatePart>?
}

@JsPlainObject
external interface HandlebarsRenderContext {
    val partId: String
}


@JsName("SaneHandlebarsApplicationV2(foundry.applications.api.ApplicationV2)")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
open external class HandlebarsApp<T : HandlebarsRenderContext>(
    options: HandlebarsFormApplicationOptions,
) : ApplicationV2 {
    protected open fun _preparePartContext(
        partId: String,
        context: HandlebarsRenderContext,
        options: HandlebarsRenderOptions
    ): Promise<T>

    protected open fun _preSyncPartState(
        partId: String,
        newElement: HTMLElement,
        priorElement: HTMLElement,
        state: Record<String, Any>
    )

    protected open fun _syncPartState(
        partId: String,
        newElement: HTMLElement,
        priorElement: HTMLElement,
        state: Record<String, Any>
    )

    protected open fun _attachPartListeners(
        partId: String,
        htmlElement: HTMLElement,
        options: ApplicationRenderOptions
    )

    protected open fun onSubmit(
        event: Event,
        form: HTMLFormElement,
        formData: FormDataExtended<AnyObject>
    ): Promise<Void>
}