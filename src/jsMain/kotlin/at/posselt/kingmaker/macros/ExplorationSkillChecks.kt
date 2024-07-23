package at.posselt.kingmaker.macros

import at.posselt.kingmaker.actor.*
import at.posselt.kingmaker.awaitAll
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
        game.pf2e.actions["seek"]?.use(PF2EActionMacroUseOptions(actors = actors))
    } else if (explorationEffectName == "Avoid Notice" && attribute == Skill.STEALTH) {
        game.pf2e.actions["avoid-notice"]?.use(PF2EActionMacroUseOptions(actors = actors))
    } else {
        rollChecks(actors, attribute, dc).awaitAll()
    }
}

suspend fun rollExplorationSkillCheckMacro(
    game: Game,
    explorationEffectName: String,
    attributeName: String,
    dc: Int?,
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