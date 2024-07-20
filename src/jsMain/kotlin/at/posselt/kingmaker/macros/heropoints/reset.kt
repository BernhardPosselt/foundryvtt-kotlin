package at.posselt.kingmaker.macros.heropoints

import at.posselt.kingmaker.postChatMessage
import com.foundryvtt.pf2e.actor.PF2ECharacter

suspend fun resetHeroPoints(actors: List<PF2ECharacter>) {
    val points = actors.map { PointsForPlayers(actor = it, 1, AwardMode.SET) }
    updateHeroPoints(points)
    postChatMessage("Reset hero point values to 1")
}