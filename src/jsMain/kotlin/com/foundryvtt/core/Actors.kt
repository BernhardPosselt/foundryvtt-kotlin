package com.foundryvtt.core

import js.objects.Record

external class Actors : WorldCollection<Actor> {
    val tokens: Record<String, Actor>
}