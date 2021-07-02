package com.pepej.tnttag.api.events

import com.pepej.tnttag.api.Arena
import com.pepej.tnttag.models.TNTState
import com.pepej.tnttag.models.User
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

sealed class TTEvent : Event() {

    override fun getHandlers(): HandlerList {
        return HANDLERS
    }

    companion object {
        private val HANDLERS = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return HANDLERS
        }
    }
}

sealed interface TTArenaEvent {

    val arena: Arena
}

sealed interface TTUserEvent : Cancellable {

    val user: User

}

private class CancellableImpl: Cancellable{

    private var cancelled: Boolean = false

    override fun isCancelled(): Boolean = cancelled
    override fun setCancelled(cancel: Boolean) {
        this.cancelled = cancel
    }
}

class TTArenaStateChangeEvent(override val arena: Arena, oldState: Arena.State, newState: Arena.State): TTArenaEvent, TTEvent()
class TTUserJoinArenaEvent(override val user: User, override val arena: Arena): TTArenaEvent, TTUserEvent, Cancellable by CancellableImpl(), TTEvent()
class TTUserLeaveArenaEvent(override val user: User, override val arena: Arena): TTArenaEvent, TTUserEvent, Cancellable by CancellableImpl(), TTEvent()
class TTUserTNTStateChangeEvent(override val user: User, override val arena: Arena, state: TNTState): TTArenaEvent, TTUserEvent, Cancellable by CancellableImpl(), TTEvent()







