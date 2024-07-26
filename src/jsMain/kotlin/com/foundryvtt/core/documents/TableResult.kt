package com.foundryvtt.core.documents

import com.foundryvtt.core.abstract.Document

external class TableResult : Document {
    val icon: String
    fun getChatText(): String

    var type: String
    var text: String
    var img: String
    var documentCollection: String
    var documentId: String
    var weight: Int
    var range: Array<Int>
    var drawn: Boolean
}