package com.foundryvtt.core

import js.objects.Record

typealias Object<T> = Record<String, T>
typealias AnyObject = Record<String, Any?>