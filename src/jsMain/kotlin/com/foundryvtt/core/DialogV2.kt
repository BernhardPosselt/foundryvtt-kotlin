//@file:JsQualifier("foundry.applications.api")

package com.foundryvtt.core

import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.pointerevents.PointerEvent
import kotlin.js.Promise

@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
@JsName("foundry.applications.api.DialogV2")
open external class DialogV2(options: DialogV2Options) : ApplicationV2 {
    companion object X {
        protected fun _onClickButton(event: PointerEvent, target: HTMLButtonElement)
        fun confirm(options: ConfirmOptions): Promise<Any>
        fun prompt(options: PromptOptions): Promise<Any>
        fun wait(options: WaitOptions): Promise<Any>
    }

    protected fun _renderButtons(): String
    protected fun _onSubmit(target: HTMLButtonElement, event: Event): Promise<DialogV2>
    protected fun _onKeyDown(event: KeyboardEvent)
}