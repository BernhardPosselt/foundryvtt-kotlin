package at.posselt.kingmaker.macros.heropoints

import com.foundryvtt.pf2e.actor.PF2ECharacter
import js.objects.recordOf
import kotlinx.coroutines.asDeferred
import kotlinx.coroutines.awaitAll
import kotlin.math.max

enum class AwardMode {
    SUBTRACT,
    ADD,
    SET
}

data class PointsForPlayers(
    val actor: PF2ECharacter,
    val points: Int,
    val mode: AwardMode = AwardMode.ADD
)

suspend fun updateHeroPoints(points: List<PointsForPlayers>) {
    points.map {
        val actor = it.actor
        val actualPoints = when (it.mode) {
            AwardMode.SUBTRACT -> max(0, actor.system.resources.heroPoints.value - it.points)
            AwardMode.ADD -> actor.system.resources.heroPoints.value + it.points
            AwardMode.SET -> it.points
        }
        it.actor.update(
            recordOf("system.resources.heroPoints.value" to actualPoints)
        ).asDeferred()
    }.awaitAll()
}