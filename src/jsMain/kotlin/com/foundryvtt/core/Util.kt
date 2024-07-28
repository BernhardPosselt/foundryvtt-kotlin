package com.foundryvtt.core

import js.objects.Record

typealias AnyNonnullObject<T> = Record<String, T>
typealias AnyObject = Record<String, Any?>
typealias AudioContext = Any // not yet available in Kotlin yet, dom API

