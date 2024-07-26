package at.posselt.kingmaker.utils

import js.objects.Record
import js.objects.recordOf
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

fun String.nullIfBlank() = ifBlank { null }