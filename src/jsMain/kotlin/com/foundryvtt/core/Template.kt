package com.foundryvtt.core

import kotlin.js.Promise

external fun renderTemplate(path: String, data: Any): Promise<String>