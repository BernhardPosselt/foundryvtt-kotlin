package at.posselt.kingmaker.macros

import at.posselt.kingmaker.actor.*
import at.posselt.kingmaker.data.actor.Attribute
import at.posselt.kingmaker.data.actor.Perception
import at.posselt.kingmaker.data.actor.Skill
import at.posselt.kingmaker.utils.awaitAll
import com.foundryvtt.core.Game
import com.foundryvtt.pf2e.PF2EActionMacroUseOptions
import com.foundryvtt.pf2e.actor.PF2ECharacter
import com.foundryvtt.pf2e.pf2e

private suspend fun rollExplorationSkillCheck(
    game: Game,
    actors: Array<PF2ECharacter>,
    explorationEffectName: String,
    attribute: Attribute,
    dc: Int?,
) {
    if (explorationEffectName == "Search" && attribute == Perception) {
        game.pf2e.actions.get("seek")?.use(PF2EActionMacroUseOptions(actors = actors))
    } else if (explorationEffectName == "Avoid Notice" && attribute == Skill.STEALTH) {
        game.pf2e.actions.get("avoid-notice")?.use(PF2EActionMacroUseOptions(actors = actors))
    } else {
        actors.rollChecks(attribute, dc).awaitAll()
    }
}

/**
 * Based on an effect name, roll a check for all actors
 * @param game
 * @param explorationEffectName "Search", "Avoid Notice" use the built-in macros, otherwise one of the appropriately capitalized names of an exploration action
 * @param attributeName a lower case skill, lore or perception
 * @param dc if provided, a dc
 */
suspend fun rollExplorationSkillCheckMacro(
    game: Game,
    explorationEffectName: String,
    attributeName: String,
    dc: Int? = null,
) {
    val actors = game.partyMembers()
        .filter { it.runsExplorationActivity(explorationEffectName) }
        .toTypedArray()
    rollExplorationSkillCheck(
        game = game,
        actors = actors,
        explorationEffectName = explorationEffectName,
        attribute = Attribute.fromString(attributeName),
        dc = dc,
    )
}