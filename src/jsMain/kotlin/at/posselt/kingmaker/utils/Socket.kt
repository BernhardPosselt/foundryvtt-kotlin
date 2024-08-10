package at.posselt.kingmaker.utils

import at.posselt.kingmaker.Config
import com.foundryvtt.core.AnyObject
import io.socket.Socket

fun Socket.emitKingmakerTools(data: AnyObject) =
    emit("module.${Config.moduleId}", data)

fun Socket.onKingmakerTools(callback: (Any) -> Unit) =
    on("module.${Config.moduleId}", callback)