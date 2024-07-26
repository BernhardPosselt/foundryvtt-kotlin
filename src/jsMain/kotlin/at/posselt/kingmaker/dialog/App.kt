package at.posselt.kingmaker.dialog

import at.posselt.kingmaker.utils.buildPromise
import com.foundryvtt.core.Hooks
import com.foundryvtt.core.HooksEventListener
import com.foundryvtt.core.applications.api.ApplicationRenderOptions
import js.array.push
import kotlinx.coroutines.await
import kotlinx.html.org.w3c.dom.events.Event
import kotlin.js.Promise

data class AppHook<T>(
    val key: String,
    val callback: Function<T>,
)

data class AppEventListener(
    val selector: String,
    val eventType: String,
    val callback: (Event) -> Promise<Unit>,
)

open class App<C>(config: HandlebarsFormApplicationOptions) : HandlebarsFormApplication<C>(config) {
    init {
        console.log("kt", config)
    }

    private val appHooks = arrayOf<AppHook<*>>()
    private val appEventListeners = arrayOf<AppEventListener>()
    protected val appHook = object : HooksEventListener {
        override fun <T> on(key: String, callback: Function<T>) {
            appHooks.push(AppHook(key = key, callback = callback))
            Hooks.on(key, callback)
        }
    }

    override fun _preClose(options: ApplicationRenderOptions): Promise<Unit> = buildPromise {
        super._preClose(options).await()
        appHooks.forEach {
            Hooks.off(it.key, it.callback)
        }
    }

    protected open fun on(selector: String, eventType: String = "click", callback: (Event) -> Promise<Unit>) {
        appEventListeners.push(
            AppEventListener(
                selector = selector,
                eventType = eventType,
                callback = callback
            )
        )
    }
}