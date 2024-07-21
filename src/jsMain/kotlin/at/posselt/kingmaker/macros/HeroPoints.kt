package at.posselt.kingmaker.macros

import at.posselt.kingmaker.dialog.prompt
import at.posselt.kingmaker.postChatMessage
import at.posselt.kingmaker.postChatTemplate
import com.foundryvtt.core.*
import com.foundryvtt.pf2e.actor.PF2ECharacter
import js.array.toTypedArray
import js.objects.Record
import js.objects.recordOf
import kotlinx.coroutines.asDeferred
import kotlinx.coroutines.awaitAll
import kotlinx.js.JsPlainObject
import kotlin.math.max
import kotlin.math.min

private enum class AwardMode {
    SUBTRACT,
    ADD,
    SET
}

private data class PointsForPlayer(
    val player: PF2ECharacter,
    val points: Int,
    val mode: AwardMode = AwardMode.ADD
)

suspend fun resetHeroPoints(actors: Array<PF2ECharacter>) {
    val points = actors.map { PointsForPlayer(player = it, 1, AwardMode.SET) }.toTypedArray()
    updateHeroPoints(points)
    postChatMessage("Reset hero point values to 1")
}

private suspend fun updateHeroPoints(points: Array<PointsForPlayer>) {
    points.map {
        val actor = it.player
        val actualPoints = when (it.mode) {
            AwardMode.SUBTRACT -> max(0, actor.system.resources.heroPoints.value - it.points)
            AwardMode.ADD -> actor.system.resources.heroPoints.value + it.points
            AwardMode.SET -> it.points
        }
        it.player.update(
            recordOf("system.resources.heroPoints.value" to min(3, actualPoints))
        ).asDeferred()
    }.awaitAll()
}

@JsPlainObject
external interface HeroPointsContext {
    val players: Array<PF2ECharacter>
}

@JsPlainObject
private external interface PlayerPointsContext {
    val points: Int
    val name: String
}

@JsPlainObject
private external interface AwardPointsChatContext {
    val points: Array<PlayerPointsContext>
}


suspend fun awardHeroPoints(players: Array<PF2ECharacter>) {
    prompt<Record<String, Int>, Unit>(
        templatePath = "components/km-dialog-form/award-hero-points.hbs",
        templateContext = HeroPointsContext(players = players),
        title = "Award Hero Points",
    ) { data ->
        val points = players.asSequence()
            .map {
                val points = (data["award-all"] ?: 0) + (data[it.uuid] ?: 0)
                PointsForPlayer(player = it, points = points)
            }
            .filter { it.points > 0 }
            .toTypedArray()
        if (points.isNotEmpty()) {
            updateHeroPoints(points)
            postChatTemplate(
                "chatmessages/award-hero-points.hbs", AwardPointsChatContext(
                    points.map { PlayerPointsContext(it.points, it.player.name!!) }.toTypedArray()
                )
            )
        }
    }
}