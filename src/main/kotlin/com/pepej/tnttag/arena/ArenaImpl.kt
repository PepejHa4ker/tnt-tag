package com.pepej.tnttag.arena

import com.pepej.papi.scheduler.Schedulers
import com.pepej.papi.terminable.composite.CompositeTerminable
import com.pepej.tnttag.TNTTag.Companion.instance
import com.pepej.tnttag.api.Arena
import com.pepej.tnttag.models.TNTState
import com.pepej.tnttag.models.User
import com.pepej.tnttag.service.UserService
import org.bukkit.World
import java.util.concurrent.ConcurrentHashMap

class ArenaImpl(override val context: Arena.Context) : Arena {

    override fun start() {
        Schedulers.builder()
            .sync()
            .every(1)
            .run(this)
            .bindWith(this)

    }

    override fun stop() {
        this.compositeTerminable.closeAndReportException()
    }

    override fun join(user: User) {

        if (!context.users.add(user)) {
            return
        }
        user.games += 1
        UserService.sendMessage(user, "Зашёл в игру")
    }


    override fun leave(user: User) {
        context.users.remove(user)
    }

    override fun startListening() {
        TODO("Not yet implemented")
    }

    override fun changeTNTState(firstUser: User, secondUser: User): TNTState {
        TODO("Not yet implemented")
    }

    /**
     * Starts arena update task
     */
    override fun run() {
        if (context.users.isEmpty()) {
            stop()
        }
    }

    override fun <T : AutoCloseable> bind(terminable: T): T {
        return this.compositeTerminable.bind(terminable)
    }


    class ContextImpl(override val world: World,
                      override var state: Arena.State = Arena.State.DISABLED,
                      override val config: ArenaConfig ,
                      override val users: MutableSet<User> = ConcurrentHashMap.newKeySet()
    ) : Arena.Context
}