package at.posselt.pfrpg.actor

import at.posselt.pfrpg.data.actor.Attribute
import at.posselt.pfrpg.data.actor.Lore
import at.posselt.pfrpg.data.actor.Perception
import at.posselt.pfrpg.data.actor.Skill
import at.posselt.pfrpg.toCamelCase
import at.posselt.pfrpg.utils.asSequence
import at.posselt.pfrpg.utils.fromUuidTypeSafe
import com.foundryvtt.core.Actor
import com.foundryvtt.pf2e.actor.PF2EAttribute
import com.foundryvtt.pf2e.actor.PF2ECharacter
import com.foundryvtt.pf2e.actor.PF2ECreature

fun PF2ECreature.resolveAttribute(attribute: Attribute): PF2EAttribute? = when (attribute) {
    is Perception -> perception
    is Skill -> skills[attribute.toCamelCase()]!!
    is Lore -> skills[attribute.value] ?: skills[attribute.lorePostfixValue]
}

fun PF2ECreature.getLoreAttributes(): List<Lore> {
    val nonLoreSkills = Skill.entries.map { it.value }.toSet()
    return skills.asSequence()
        .filter { !nonLoreSkills.contains(it.component1()) }
        .map { Lore(it.component1()) }
        .toList()
}

fun PF2ECharacter.runsExplorationActivity(name: String) =
    system.exploration.mapNotNull { id -> items.get(id)?.name }.any { name == it }

suspend fun openActor(uuid: String) {
    fromUuidTypeSafe<Actor>(uuid)
        ?.sheet
        ?.render(true)
}