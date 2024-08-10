package at.posselt.kingmaker.app

import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.FormDataExtended
import com.foundryvtt.core.applications.api.ApplicationConfiguration
import com.foundryvtt.core.applications.api.ApplicationRenderOptions
import com.foundryvtt.core.applications.api.ApplicationV2
import com.foundryvtt.core.applications.api.HandlebarsRenderOptions
import js.core.Void
import js.objects.Record
import kotlinx.html.org.w3c.dom.events.Event
import kotlinx.js.JsPlainObject
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLFormElement
import kotlin.js.Promise

@JsPlainObject
external interface HandlebarsFormApplicationOptions : ApplicationConfiguration {
    val templatePath: String
    val scrollable: Array<String>
}


@JsName("HandlebarsFormApplication(foundry.applications.api.ApplicationV2)")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
open external class HandlebarsFormApplication<T>(
    options: HandlebarsFormApplicationOptions,
) : ApplicationV2 {
    protected open fun _preparePartContext(
        partId: String,
        context: T,
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

    protected open fun _attachPartListeners(partId: String, htmlElement: HTMLElement, options: ApplicationRenderOptions)

    protected open fun onSubmit(
        event: Event,
        form: HTMLFormElement,
        formData: FormDataExtended<AnyObject>
    ): Promise<Void>
}