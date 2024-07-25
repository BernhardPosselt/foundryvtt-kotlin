package com.foundryvtt.core

import js.objects.Record
import kotlinx.html.org.w3c.dom.events.Event
import org.w3c.dom.DOMTokenList
import org.w3c.dom.HTMLElement
import org.w3c.dom.pointerevents.PointerEvent
import kotlin.js.Promise


@JsName("foundry.applications.api.ApplicationV2")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
open external class ApplicationV2(
    options: ApplicationConfiguration = definedExternally
) {
    @OptIn(ExperimentalStdlibApi::class)
    @JsExternalInheritorsOnly
    open class ApplicationV2Static {
        val RENDER_STATES: RenderStates
        val BASE_APPLICATION: JsClass<ApplicationV2>
        open val DEFAULT_OPTIONS: ApplicationConfiguration
        val emittedEvents: Array<String>
    }

    companion object : ApplicationV2Static

    val options: ApplicationConfiguration
    val id: String
    val title: String
    val element: HTMLElement
    val minimized: Boolean
    val position: ApplicationPosition
    val rendered: Boolean
    val hasFrame: Boolean
    val tabGroups: Record<String, String>
    val classList: DOMTokenList
    val state: Int
    // omitted: window

    fun render(options: ApplicationRenderOptions = definedExternally): Promise<ApplicationV2>
    protected fun _getHeaderControls(): Array<ApplicationHeaderControlsEntry>
    fun close(options: ApplicationClosingOptions = definedExternally): Promise<ApplicationV2>
    fun setPosition(position: ApplicationPosition? = definedExternally): ApplicationPosition
    fun toggleControls(expanded: Boolean)
    fun minimize(): Promise<Unit>
    fun maximize(): Promise<Unit>
    fun bringToFront()
    fun changeTab(tab: String, group: String, options: ApplicationTabOptions)
    protected fun _canRender(options: ApplicationRenderOptions): dynamic
    protected fun _onFirstRender(context: ApplicationRenderContext, options: ApplicationRenderOptions): Promise<Unit>
    protected fun _preRender(context: ApplicationRenderContext, options: ApplicationRenderOptions): Promise<Unit>
    protected fun _onRender(context: ApplicationRenderContext, options: ApplicationRenderOptions)
    protected fun _preClose(options: ApplicationRenderOptions): Promise<Unit>
    protected fun _prePosition(position: ApplicationPosition)
    protected fun _onPosition(position: ApplicationPosition)
    protected fun _attachFrameListeners()
    protected fun _onClickAction(event: PointerEvent, target: HTMLElement)
    protected fun _onSubmitForm(formConfig: ApplicationFormConfiguration, event: Event): Promise<Unit>
    protected fun _onChangeForm(formConfig: ApplicationFormConfiguration, event: Event): Promise<Unit>
}