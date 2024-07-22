# Kotlin JS Primer

## Async

Instead of marking a function async, using await and returning promises, Kotlin has **suspend**ing function that are awaited automatically:

JS:

```js
async function asyncFunction() {
    return Promise.resolve(3)
}

async function f() {
    const result = await asyncFunction()
    console.log(result)
    return result
}
```

Kotlin:

```kt
suspend fun asyncFunction() = 3

suspend fun f(): Int {
    val result = asyncFunction()
    console.log(result)
    return result
}
```

You can also use Promises like in JavaScript, but Promises are not suspending functions, so you need to call the **.await()** method on it which itself is a suspending function. 
**Promise.all()** functionality is provided out of the box with a **.awaitAll()** extension function on a list.

JS:

```js
function promiseFunction(number) {
    return Promise.resolve(number)
}

async function f() {
    console.log(await promiseFunction(3))
    const result = await Promise.all([1, 2, 3]
        .map(num => promiseFunction(num)))
    console.log(result)
    return result
}
```

Kotlin:

```kt
fun promiseFunction(num: Int) = Promise.resolve(num)

suspend fun f(): Array<Int> {
    console.log(promiseFunction(3).await())
    val result = arrayOf(1, 2, 3)
        .map { promiseFunction(it) }
        .awaitAll()
    console.log(result)
    return result
}
```

Should you find yourself having to await a list of suspending functions rather than a list of Promises, you need to provide a coroutine scope:

```kt
suspend fun work(num: Int) = num

suspend fun x() = coroutineScope {
    val result = async { work(1) }
    val result2 = async { work(2) }
    arrayOf(result.await(), result2.await())
}

fun main() {
    buildPromise {
        console.log(x())
    }
}
```

Suspend functions can only be called from another suspend function or from within a coroutine context. To start an async
block in a normal function you can use the **buildPromise** utility:

```js
await promiseFunction(3);
```

```kt
fun main() {
    buildPromise {
        promiseFunction(3).await()
    }
}
```

## Objects

Passing Kotlin objects to JavaScript functions does not work because object properties are minified. There are 2 ways around that:

* Record: Similar to TypeScript's Record
* @JsPlainObject: Similar to TypeScript's interface

```kt
@JsPlainObject
external interface Plain {
    val optional: Int?
    val required: Int
}

fun acceptPlain(param: Plain) {
    
}

fun acceptRecord(param: Record<String, Int>) {
    
}

fun main() {
    // the following are equivalent but the interface is typed
    acceptPlain(Plain(required=3))
    acceptRecord(recordOf(
        "required" to 3
    ))
}
```

## Arrays/Lists

You can not pass Lists to JavaScript functions or return them. Instead, use arrays when needing to interop with JS

## Writing Type Definitions

Type definitions are written in a similar way to TypeScript, but use the **external** modifier instead of **declare**:

```kt
external fun add(a: Int, b: Int)
external fun asyncAdd(a: Int, b: Int): Promise<Int>
external fun getArray(): Array<Int>
external class A : SuperClass, Interface

external val value: Int

@JsPlainObject
external interface Obj {
    val optional: Int?
    val required: Int
}

external fun funcTakingObject(obj: Obj)

// making instanceof checks work needs the correct name on global scope
@JsName("PF2ECharacter")
external class A

// or a path to where the class instance can be accessed
@JsName("CONFIG.PF2E.Actor.documentClasses.character")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
external class B
```

## Inheriting Static Methods

JS allows you to inherit static methods and resolve the actual instance. In Kotlin, this is not supported, but you can share common logic by pulling it out into a separate open class and letting your companion objects extend from it. This means you need to maintain a separate chain of inheritance outside the companion object:

JS:
```js
class Test {
    static create() {
        return new this()
    }
}

class Test2 extends Test {}

console.log(Test2.create())  // prints Test2 {}
```

Kotlin:

```kt
open external class Test {
    @OptIn(ExperimentalStdlibApi::class)
    @JsExternalInheritorsOnly
    open class Factory<T> {
        fun create(): T
    }

    companion object : Factory<Test>
}

external class Test2 : Test {
    @OptIn(ExperimentalStdlibApi::class)
    @JsExternalInheritorsOnly
    open class Factory2<T> : Factory<T> {
        fun update(): T
    }

    companion object : Factory2<Test2>
}
// Test only has access to create
Test.create()
// But Test 2 has access to both
Test2.create()
Test2.update()
```