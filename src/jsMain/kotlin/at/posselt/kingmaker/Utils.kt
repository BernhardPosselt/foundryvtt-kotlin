package at.posselt.kingmaker

import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.Document
import js.objects.PropertyKey
import js.objects.Record
import js.objects.jso
import js.objects.recordOf
import js.reflect.Proxy
import js.reflect.ProxyHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asDeferred
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.promise
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.js.Promise

/**
 * Use this whenever you'd use an async () => {} lambda in JS, e.g.
 *
 * func("do something") {
 *     buildPromise {
 *         window.fetch("https://google.com").await()
 *     }
 * }
 */
fun <T> buildPromise(
    block: suspend CoroutineScope.() -> T,
): Promise<T> =
    CoroutineScope(EmptyCoroutineContext).promise(block = block)

/**
 * Make awaitAll also work on a list of Promises instead in addition to Deferred
 */
suspend fun <T> List<Promise<T>>.awaitAll(): List<T> =
    map { it.asDeferred() }.awaitAll()

suspend fun <T> Array<Promise<T>>.awaitAll(): Array<T> =
    map { it.asDeferred() }.awaitAll().toTypedArray()

fun <F : Any, S> MutableList<Pair<F, S>>.toRecord(): Record<F, S> =
    recordOf(*toTypedArray())

fun <F : Any, S> List<Pair<F, S>>.toRecord(): Record<F, S> =
    recordOf(*toTypedArray())

fun <F : Any, S> Map<F, S>.toRecord(): Record<F, S> =
    recordOf(*map { it.key to it.value }.toTypedArray())

fun <F : Any, S> Array<Pair<F, S>>.toRecord(): Record<F, S> =
    recordOf(*this)


fun String.unslugify(): String =
    split("-")
        .joinToString(" ")
        .replaceFirstChar(Char::uppercase)

private class SetterProxy

class Handler<T : Any>(
    private val currentPath: String = "",
    private val updates: HashMap<String, Any?>,
) {
    fun set(target: T, p: PropertyKey, value: Any, receiver: Any) {
        updates[currentPath] = value
    }

    fun get(target: T, p: PropertyKey, receiver: Any): Any =
        Handler<T>(if (currentPath.isEmpty()) "$p" else "$currentPath.$p", updates)
            .asProxy<T>(jso())

    fun <T : Any> asProxy(target: T): Proxy<T> {
        @Suppress("UNCHECKED_CAST")
        val binding = recordOf("set" to ::set, "get" to ::get) as ProxyHandler<T>
        return Proxy(target, binding)
    }
}

@Suppress("UNCHECKED_CAST")
fun <D : Document> D.buildUpdate(block: D.() -> Unit): AnyObject {
    val result = HashMap<String, Any?>()
    val proxy = Handler<Any>(updates = result)
        .asProxy<Any>(this) as D
    proxy.block()
    return result.toRecord()
}
