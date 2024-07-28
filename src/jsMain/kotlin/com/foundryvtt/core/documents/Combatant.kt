package com.foundryvtt.core.documents

import com.foundryvtt.core.Actor
import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.Roll
import com.foundryvtt.core.abstract.DatabaseDeleteOperation
import com.foundryvtt.core.abstract.DatabaseUpdateOperation
import js.objects.jso
import kotlin.js.Promise

// required to make instance of work, but since the classes are not registered here
// at page load, we can't use @file:JsQualifier
@JsName("CONFIG.Combatant.documentClass")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
external class Combatant : ClientDocument {
    companion object : DocumentStatic<Combatant>;

    override fun delete(operation: DatabaseDeleteOperation): Promise<Combatant>
    override fun update(data: AnyObject, operation: DatabaseUpdateOperation): Promise<Combatant>

    var _id: String
    var type: String
    var actorId: String
    var tokenId: String
    var sceneId: String
    var name: String
    var img: String
    var initiative: Int
    var hidden: Boolean
    var defeated: Boolean

    var resource: AnyObject?
    val combat: Combat?
    val isNPC: Boolean
    val actor: Actor?
    val token: Token?
    val players: Array<User>
    val isDefeated: Boolean
    fun getInitiativeRoll(formula: String): Roll
    fun rollInitiative(formula: String): Promise<Combatant>
    fun updateResource(): AnyObject?
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE", "UNCHECKED_CAST")
fun Combatant.update(data: Combatant, operation: DatabaseUpdateOperation = jso()): Promise<Combatant> =
    update(data as AnyObject, operation)