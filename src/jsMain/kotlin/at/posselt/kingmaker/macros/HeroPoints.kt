package at.posselt.kingmaker.macros

import at.posselt.kingmaker.postChatMessage
import at.posselt.kingmaker.tpl
import com.foundryvtt.core.DialogV2
import com.foundryvtt.core.DialogV2Button
import com.foundryvtt.core.PromptOptions
import com.foundryvtt.pf2e.actor.PF2ECharacter
import js.objects.recordOf
import kotlinx.coroutines.asDeferred
import kotlinx.coroutines.awaitAll
import kotlinx.js.JsPlainObject
import kotlin.math.max

private enum class AwardMode {
    SUBTRACT,
    ADD,
    SET
}

private data class PointsForPlayers(
    val actor: PF2ECharacter,
    val points: Int,
    val mode: AwardMode = AwardMode.ADD
)

suspend fun resetHeroPoints(actors: List<PF2ECharacter>) {
    val points = actors.map { PointsForPlayers(actor = it, 1, AwardMode.SET) }
    updateHeroPoints(points)
    postChatMessage("Reset hero point values to 1")
}

private suspend fun updateHeroPoints(points: List<PointsForPlayers>) {
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

@JsPlainObject
external interface HeroPointsContext {
    val text: String
}

suspend fun awardHeroPoints(players: List<PF2ECharacter>) {
    val content = tpl("award-hero-points.hbs", HeroPointsContext(text = "hi"))
    val button = DialogV2Button(
        action = "ok",
        label = "Ok"
    ) { ev, button, dialog ->
        console.log(ev)
    }
    DialogV2.prompt(
        PromptOptions(content = content)
    )
}