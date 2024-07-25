package com.foundryvtt.core


import js.core.Void
import js.objects.Record
import kotlinx.html.org.w3c.dom.events.Event
import kotlinx.js.JsPlainObject
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLFormElement
import org.w3c.dom.pointerevents.PointerEvent
import kotlin.js.Promise

typealias ApplicationClickAction = (event: PointerEvent, target: HTMLElement) -> Promise<Unit>

@JsPlainObject
external interface ApplicationHeaderControlsEntry {
    val icon: String
    val label: String
    val action: String
    val visible: Boolean

    // String or Number
    val ownership: Any
}

@JsPlainObject
external interface ApplicationPosition {
    val top: Int
    val left: Int

    // Int or "auto"
    val width: Any

    // Int or "auto"
    val height: Any
    val scale: Int
    val zIndex: Int
}

@JsPlainObject
external interface ApplicationConfiguration {
    val id: String?
    val uniqueId: String?
    val classes: Array<String>?
    val tag: String?
    val window: Window?
    val actions: Record<String, ApplicationClickAction>?
    val form: ApplicationFormConfiguration?
    val position: ApplicationPosition?
}

@JsPlainObject
external interface Window {
    val title: String
    val frame: Boolean?
    val positioned: Boolean?
    val icon: String?
    val controls: Array<ApplicationHeaderControlsEntry>?
    val minimizable: Boolean?
    val resizable: Boolean?
    val contentTag: String?
    val contentClasses: Array<String>?
}

@JsPlainObject
external interface ApplicationTabOptions {
    val event: Event
    val navElement: HTMLElement
    val force: Boolean?
    val updatePosition: Boolean?
}

@JsPlainObject
external interface RenderStates {
    val ERROR: Int
    val CLOSING: Int
    val CLOSED: Int
    val NONE: Int
    val RENDERING: Int
    val RENDERED: Int
}

@JsPlainObject
external interface ApplicationWindowRenderOptions {
    val title: String
    val icon: String
    val controls: Boolean
}

@JsPlainObject
external interface ApplicationRenderOptions {
    val force: Boolean?
    val position: ApplicationPosition?
    val window: ApplicationWindowRenderOptions?
    val parts: Array<String>?
    val isFirstRender: Boolean?
}

@JsPlainObject
external interface ApplicationClosingOptions {
    val animate: Boolean
    val closeKey: Boolean
}


@JsPlainObject
external interface ApplicationFormConfiguration {
    val handler: (event: Event, form: HTMLFormElement, formData: FormDataExtended<AnyObject>) -> Promise<Void>
    val submitOnChange: Boolean
    val closeOnSubmit: Boolean
}

typealias ApplicationRenderContext = Record<String, Any>
