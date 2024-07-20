package com.foundryvtt.core

open external class Actor<D> : Document<D> {
    val name: String?
    val type: String
    val hasPlayerOwner: Boolean
}

