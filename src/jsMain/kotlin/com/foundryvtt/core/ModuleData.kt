package com.foundryvtt.core

import kotlin.js.Promise

external class ModuleData {
    val active: Boolean
    fun updateSource(data: Any): Promise<Unit>
}