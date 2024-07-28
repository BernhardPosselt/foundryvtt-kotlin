@file:JsQualifier("foundry.utils")

package com.foundryvtt.core.utils

import com.foundryvtt.core.AnyObject

external fun expandObject(value: AnyObject): AnyObject
external fun <T> deepClone(value: T): T
external fun mergeObject(original: AnyObject, other: AnyObject = definedExternally): AnyObject


