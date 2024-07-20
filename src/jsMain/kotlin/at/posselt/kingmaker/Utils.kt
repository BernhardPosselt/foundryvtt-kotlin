package at.posselt.kingmaker

import kotlinx.coroutines.CoroutineScope
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



