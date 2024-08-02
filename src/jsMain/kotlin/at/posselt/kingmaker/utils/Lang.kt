package at.posselt.kingmaker.utils

import js.array.JsTuple2
import js.array.toTypedArray
import js.array.tupleOf
import js.objects.Object
import js.objects.ReadonlyRecord
import js.objects.Record
import js.objects.recordOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asDeferred
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.promise
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
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

@Suppress(
    "NOTHING_TO_INLINE",
    "CANNOT_CHECK_FOR_EXTERNAL_INTERFACE",
    "CANNOT_CHECK_FOR_ERASED",
    "ERROR_IN_CONTRACT_DESCRIPTION"
)
@OptIn(ExperimentalContracts::class)
inline fun isJsObject(x: Any?): Boolean {
    contract {
        returns(true) implies (x is Record<String, Any?>)
    }
    return jsTypeOf(x) == "object" && x !is Array<*> && x != null
}

fun <T> Record<String, T>.asSequence(): Sequence<JsTuple2<String, T>> =
    Object.entries(this).asSequence()

fun <T> Sequence<Pair<String, T>>.toRecord(): ReadonlyRecord<String, T> =
    Object.fromEntries(map { tupleOf(it.first, it.second) }.toTypedArray())

fun <T> Sequence<JsTuple2<String, T>>.toRecord(): ReadonlyRecord<String, T> =
    Object.fromEntries(toTypedArray())

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
fun <T> Sequence<JsTuple2<String, T>>.toMutableRecord(): Record<String, T> =
    Object.fromEntries(toTypedArray()) as Record<String, T>

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
fun <T> Sequence<Pair<String, T>>.toMutableRecord(): Record<String, T> =
    Object.fromEntries(map { tupleOf(it.first, it.second) }.toTypedArray()) as Record<String, T>


