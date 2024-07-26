package at.posselt.kingmaker.dialog

import com.foundryvtt.core.FormDataExtended
import com.foundryvtt.core.HooksEventListener
import com.foundryvtt.core.abstract.DataModel
import com.foundryvtt.core.applications.api.ApplicationHeaderControlsEntry
import com.foundryvtt.core.applications.api.ApplicationV2
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
    val menuButtons: Array<ApplicationHeaderControlsEntry>?
    val classes: Array<String>?
    val actions: Array<String>?
    val width: Int?
    val height: Int?
    val top: Int?
    val left: Int?
    val icon: String?
}

/**
 * Sane wrapper around ApplicationV2. Note that if you want to pass this class
 * to any Foundry API that requires an application, you can use App().app
 */
open external class App<T : Any, D : Any>(arguments: AppArguments) {
    val app: JsClass<ApplicationV2>
    protected val instance: ApplicationV2?

    /**
     * Wrapper for the Hooks.on method.
     *
     * Hooks registered using the on method are automatically removed via Hooks.off when
     * the class gets destroyed
     */
    protected val appHooks: HooksEventListener

    /**
     * Callback that happens in the application constructor right before
     * hook callbacks are bound
     *
     * Use this hook to define:
     * * listen to hooks using registerHooks()
     * * listen to events using on()
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
    fun close(): Promise<Unit>

    /**
     * Force the application to re-render
     */
    protected fun reRender(): Promise<Unit>

    /**
     * Open the application
     */
    fun launch(): Promise<Unit>

    /**
     * Returns data that is available inside templates
     */
    protected open fun getTemplateContext(): Promise<D>

    /**
     * Add an event listener. Must be called inside an overridden onInit() method
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
    protected open fun onAction(action: String, event: Event): Promise<Unit>
}