package at.posselt.kingmaker.app

import at.posselt.kingmaker.utils.buildPromise
import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.Hooks
import com.foundryvtt.core.HooksEventListener
import com.foundryvtt.core.applications.api.ApplicationRenderOptions
import js.array.push
import kotlinx.coroutines.await
import kotlinx.html.org.w3c.dom.events.Event
import org.w3c.dom.asList
import kotlin.js.Promise

data class AppHook<T>(
    val key: String,
    val callback: Function<T>,
)

data class AppEventListener(
    val selector: String,
    val eventType: String,
    val callback: (Event) -> Unit,
)

/**
 * Small utility class that takes care of unregistering Hooks and
 * provides a neat way of defining event listeners
 */
open class App<C>(config: HandlebarsFormApplicationOptions) : HandlebarsFormApplication<C>(config) {
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

    override fun _preRender(context: AnyObject, options: ApplicationRenderOptions): Promise<Unit> {
        appEventListeners.forEach {
            element.querySelectorAll(it.selector).asList()
                .forEach { el -> el.addEventListener(it.eventType, it.callback) }
        }
        return super._preRender(context, options)
    }

    protected open fun on(selector: String, eventType: String = "click", callback: (Event) -> Unit) {
        appEventListeners.push(
            AppEventListener(
                selector = selector,
                eventType = eventType,
                callback = callback
            )
        )
    }
}