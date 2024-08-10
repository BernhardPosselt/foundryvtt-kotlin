package com.foundryvtt.core.collections

import com.foundryvtt.core.Actor
import js.objects.Record

external class Actors : WorldCollection<Actor> {
    companion object : WorldCollectionStatic

    val tokens: Record<String, Actor>
}