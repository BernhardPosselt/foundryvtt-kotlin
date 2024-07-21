package com.foundryvtt.core

import js.objects.Record

external interface PF2EActorConfig {
    val documentClasses: Record<String, JsClass<Actor<*>>>
}

external interface PF2EConfig {
    val Actor: PF2EActorConfig
}

external interface Config {
    val PF2E: PF2EConfig
}

external val CONFIG: Config