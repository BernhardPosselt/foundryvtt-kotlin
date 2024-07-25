package at.posselt.kingmaker.dialog

import com.foundryvtt.core.ApplicationV2
import kotlinx.js.JsPlainObject
import org.w3c.dom.HTMLElement
import kotlin.js.Promise

@JsPlainObject
external interface AppArguments {
    val title: String
    val templatePath: String
    val submitOnChange: Boolean?
    val closeOnSubmit: Boolean?
    val isForm: Boolean?
}

open external class App(arguments: AppArguments) {
    val app: JsClass<ApplicationV2>
    val instance: ApplicationV2?
    open fun bindEventListeners(element: HTMLElement)
    open fun onInit()
    open fun beforeClose(): Promise<Unit>
    open fun onSubmit(): Promise<Unit>
    open fun close(): Promise<Unit>
    open fun reRender(): Promise<Unit>
    open fun launch(): Promise<Unit>
}