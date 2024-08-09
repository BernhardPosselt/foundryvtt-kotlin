package at.posselt.kingmaker.utils

import at.posselt.kingmaker.app.launch
import com.foundryvtt.core.documents.JournalEntry
import com.foundryvtt.core.documents.JournalEntryPage

suspend fun openJournal(uuid: String) {
    (fromUuidTypeSafe<JournalEntryPage>(uuid)
        ?: fromUuidTypeSafe<JournalEntry>(uuid))
        ?.sheet
        ?.launch()
}