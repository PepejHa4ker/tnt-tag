package com.pepej.tnttag.api

import com.pepej.papi.terminable.Terminable
import com.pepej.papi.terminable.TerminableConsumer
import com.pepej.papi.terminable.composite.CompositeTerminable
import com.pepej.tnttag.arena.ArenaConfig
import com.pepej.tnttag.models.TNTState
import com.pepej.tnttag.models.User
import org.bukkit.World

interface Arena : TerminableConsumer, Terminable, Runnable {

    val context: Context

    val compositeTerminable: CompositeTerminable
        get() = CompositeTerminable.create()


    fun isActualToJoin(): Boolean {
        if (context.state == State.DISABLED) {
            return false
        } else if (context.users.size >= context.config.maxPlayers) {
            return false
        }
        return true
    }

    fun start()

    fun stop()

    fun join(user: User)

    fun leave(user: User)

    fun startListening()

    fun changeTNTState(firstUser: User, secondUser: User): TNTState

    override fun close() {
        stop()
    }


    /**
     * Starts arena update task
     */
    override fun run()


    enum class State {
        ENABLED,
        DISABLED,
        IN_GAME
    }

    interface Context {

        val world: World
        var state: State
        val config: ArenaConfig
        val users: MutableSet<User>
    }
}
