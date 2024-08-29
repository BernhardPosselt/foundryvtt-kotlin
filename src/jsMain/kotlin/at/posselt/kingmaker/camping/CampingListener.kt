package at.posselt.kingmaker.camping

import at.posselt.kingmaker.data.checks.DegreeOfSuccess
import at.posselt.kingmaker.takeIfInstance
import com.foundryvtt.core.Actor
import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.Game
import com.foundryvtt.core.utils.getProperty
import com.foundryvtt.pf2e.actor.PF2ENpc


enum class CampingCommand {
    CLEAR_ACTIVITIES,
    SKIP_ACTIVITIES,
    SYNC_ACTIVITIES,
    DO_NOTHING
}

fun checkPreActorUpdate(actor: Actor, update: AnyObject): CampingCommand =
    actor.takeIfInstance<PF2ENpc>()
        ?.getCamping()
        ?.let { camping ->
            val activities = getProperty(update, "flags.pf2e-kingmaker-tools.camping-sheet.campingActivities")
                ?.unsafeCast<Array<CampingActivity>>()
            if (activities == null) return CampingCommand.DO_NOTHING
            val activitiesByName = camping.campingActivities.associateBy { it.activity }
            val activityStateChanged = activities.mapNotNull { new ->
                val previous = activitiesByName[new.activity]
                val hasDifferentResult = new.result != previous?.result
                val hasDifferentActor = new.actorUuid != previous?.actorUuid
                if (hasDifferentActor || hasDifferentResult) {
                    previous to new
                } else {
                    null
                }
            }
            val needsSync = activityStateChanged.isNotEmpty() || camping.campingActivities.size != activities.size
            val prepareCampsite = activityStateChanged.find { it.second.isPrepareCamp() }?.second

            return if (needsSync && prepareCampsite != null && prepareCampsite.result == null) {
                CampingCommand.CLEAR_ACTIVITIES
            } else if (needsSync && prepareCampsite != null && prepareCampsite.parseResult() == DegreeOfSuccess.CRITICAL_FAILURE) {
                CampingCommand.SKIP_ACTIVITIES
            } else if (needsSync) {
                CampingCommand.SYNC_ACTIVITIES
            } else {
                CampingCommand.DO_NOTHING
            }
        } ?: CampingCommand.DO_NOTHING
