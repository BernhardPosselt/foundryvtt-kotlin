package com.foundryvtt.core


abstract external class DataModel<D> {
    fun toObject(source: Boolean = definedExternally): D
    fun toJSON(): D
    fun reset()
}