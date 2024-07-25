package at.posselt.kingmaker.actor

import at.posselt.kingmaker.data.actor.Attribute
import at.posselt.kingmaker.data.actor.Lore
import at.posselt.kingmaker.data.actor.Perception
import at.posselt.kingmaker.data.actor.Skill
import com.foundryvtt.pf2e.actor.PF2EAttribute
import com.foundryvtt.pf2e.actor.PF2ECharacter


fun PF2ECharacter.resolveSkill(skill: String): PF2EAttribute? = skills[skill]

fun PF2ECharacter.resolveAttribute(skill: Attribute): PF2EAttribute? = when (skill) {
    is Skill, is Lore -> resolveSkill(skill.value)
    is Perception -> perception
}

fun PF2ECharacter.runsExplorationActivity(name: String) =
    system.exploration?.mapNotNull { id -> items.get(id)?.name }?.any { name == it } ?: false