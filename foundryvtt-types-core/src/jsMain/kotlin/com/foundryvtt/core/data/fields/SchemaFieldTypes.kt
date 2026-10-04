package com.foundryvtt.core.data.fields

import js.objects.Record

typealias DataSchema<T> = Record<String, DataField<T>>