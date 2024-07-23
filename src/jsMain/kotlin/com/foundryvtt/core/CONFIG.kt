package com.foundryvtt.core

import js.objects.Record

external interface PF2EActorConfig {
    val documentClasses: Record<String, JsClass<Actor>>
}

external interface Config

external val CONFIG: Config