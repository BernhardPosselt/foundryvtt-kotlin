package at.posselt.kingmaker.dialog

import com.foundryvtt.core.ApplicationHeaderControlsEntry
import com.foundryvtt.core.ApplicationV2
import com.foundryvtt.core.DataModel
import com.foundryvtt.core.FormDataExtended
import kotlinx.html.org.w3c.dom.events.Event
import kotlinx.js.JsPlainObject
import kotlin.js.Promise

@JsPlainObject
external interface Hook<T> {
    val key: String
    val callback: Function<T>
}

@JsPlainObject
external interface AppArguments {
    val title: String
    val templatePath: String
    val submitOnChange: Boolean?
    val closeOnSubmit: Boolean?
    val isForm: Boolean?
    val dataModel: DataModel?
    val menuButtons: Array<ApplicationHeaderControlsEntry>
}

/**
 * Sane wrapper around ApplicationV2. Note that you can't pass this class
 * to any Foundry API that requires an application, since it wraps an Application class
 * but does not extend one.
 */
open external class App<T : Any>(arguments: AppArguments) {
    protected val app: JsClass<ApplicationV2>
    protected val instance: ApplicationV2?

    /**
     * Callback that happens in the application constructor right before
     * hook callbacks are bound
     */
    protected open fun onInit()

    /**
     * Run before closing the app
     */
    protected open fun beforeClose(): Promise<Unit>

    /**
     * If you have not passed isForm=false in the constructor, this callback
     * will be called on submit
     */
    protected open fun onSubmit(data: FormDataExtended<T>): Promise<Unit>

    /**
     * Closes the application
     */
    open fun close(): Promise<Unit>

    /**
     * Force the application to re-render
     */
    protected open fun reRender(): Promise<Unit>

    /**
     * Open the application
     */
    open fun launch(): Promise<Unit>

    /**
     * Register a hook function that is cleaned up when the application is closed.
     * @param key first parameter of Hooks.on
     * @param callback second parameter passed to Hooks.on
     */
    protected fun <H> registerHook(key: String, callback: Function<H>)

    /**
     * Add an event listener. Must be called inside an overriden onInit() method
     *
     * @param selector a CSS selector that returns one or more results
     * @param evenType string of what event you want to listen for, defaults to 'click'
     * @param callback event listener callback
     */
    protected fun <O> on(selector: String, evenType: String = definedExternally, callback: (Event) -> O)

    /**
     * When a header menu button is clicked, this callback is executed with
     * the selected action
     */
    protected open fun onMenu(action: String)
}