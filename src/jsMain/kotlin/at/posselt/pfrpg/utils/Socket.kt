package at.posselt.pfrpg.utils

import at.posselt.pfrpg.Config
import com.foundryvtt.core.AnyObject
import io.socket.Socket


fun Socket.emitPfrpg2eKingdomCampingWeather(data: AnyObject) =
    emit("module.${Config.moduleId}", data)

fun Socket.onPfrpg2eKingdomCampingWeather(callback: (Any) -> Unit) =
    on("module.${Config.moduleId}", callback)