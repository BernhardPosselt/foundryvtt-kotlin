@file:JsQualifier("foundry.utils")

package com.foundryvtt.core.utils

import com.foundryvtt.core.AnyObject
import js.objects.ReadonlyRecord

external fun expandObject(value: AnyObject): AnyObject
external fun <T> deepClone(value: T): T
external fun mergeObject(original: AnyObject, other: AnyObject = definedExternally): AnyObject
external fun flattenObject(original: Any): ReadonlyRecord<String, Any>

