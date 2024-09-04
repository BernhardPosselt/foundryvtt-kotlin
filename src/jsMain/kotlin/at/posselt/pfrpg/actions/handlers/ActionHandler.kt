package at.posselt.pfrpg.actions.handlers

import at.posselt.pfrpg.actions.ActionMessage
import at.posselt.pfrpg.actions.ActionDispatcher

enum class ExecutionMode {
    ALL,
    GM_ONLY,
    OTHERS,
}

abstract class ActionHandler(
    private val action: String,
    val mode: ExecutionMode = ExecutionMode.GM_ONLY,
) {
    fun canExecute(action: ActionMessage): Boolean = action.action == this.action
    abstract suspend fun execute(action: ActionMessage, dispatcher: ActionDispatcher)
}