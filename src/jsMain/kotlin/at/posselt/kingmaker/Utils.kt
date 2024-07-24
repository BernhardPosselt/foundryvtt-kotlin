package at.posselt.kingmaker

import com.foundryvtt.core.Document
import js.objects.PropertyKey
import js.objects.Record
import js.objects.jso
import js.objects.recordOf
import js.reflect.Proxy
import js.reflect.ProxyHandler
import js.symbol.Symbol
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

val isProxy = Symbol("isProxy")


private class Handler(
    private val currentPath: String = "",
    private val updates: HashMap<String, Any?>,
) {
    private fun buildPath(p: String) = if (currentPath.isEmpty()) p else "$currentPath.$p"

    fun set(target: dynamic, p: PropertyKey, value: dynamic, receiver: Any) {
        @Suppress("IMPLICIT_BOXING_IN_IDENTITY_EQUALS")
        if (value[isProxy] === true)
            throw IllegalArgumentException(
                "You are assigning an attribute to a proxy. " +
                        "Did you mean to assign a value instead?"
            )
        updates[buildPath("$p")] = value
    }

    fun get(target: Any, p: PropertyKey, receiver: Any): Any {
        if (p === isProxy) return true
        return Handler(buildPath("$p"), updates)
            .asProxy(jso())
    }

    fun asProxy(target: Any): Proxy<Any> {
        @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE", "UNCHECKED_CAST")
        val binding = recordOf("set" to ::set, "get" to ::get) as ProxyHandler<Any>
        return Proxy(target, binding)
    }
}

/**
 * Allows you to assign partial updates in a typesafe manner, e.g.:
 * pf2eActor.typeSafeUpdate {
 *     name = "test",
 *     system.details.level.value = 3
 * }.await()
 *
 * will produce {'name': 'test', 'system.details.level.value': 3}
 *
 * Note that you *must not* assign a property to itself, e.g.
 * * pf2eActor.typeSafeUpdate {
 *  *     name = "test",
 *  *     system.details.level.value = system.details.level.value
 *  * }.await()
 */
@Suppress("UNCHECKED_CAST")
fun <D : Document> D.typeSafeUpdate(block: D.() -> Unit): Promise<D> {
    val result = HashMap<String, Any?>()
    val proxy = Handler(updates = result)
        .asProxy(this) as D
    proxy.block()
    return update(result.toRecord()) as Promise<D>
}
