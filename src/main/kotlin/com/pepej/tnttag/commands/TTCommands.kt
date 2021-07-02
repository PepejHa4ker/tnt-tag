package com.pepej.tnttag.commands

import com.pepej.papi.command.Commands
import com.pepej.papi.terminable.TerminableConsumer
import com.pepej.papi.terminable.module.TerminableModule
import com.pepej.tnttag.api.Arena
import com.pepej.tnttag.menu.ArenaMenu
import com.pepej.tnttag.service.ArenaService
import com.pepej.tnttag.service.UserService
import java.util.*

object TTCommands : TerminableModule {

    override fun setup(consumer: TerminableConsumer) {
        Commands.parserRegistry().register(Arena::class.java) {
            Optional.ofNullable(ArenaService.getArena(it))
        }
        Commands.create()
            .assertPlayer()
            .handler {
                ArenaMenu(it.sender()).open()
            }
            .registerAndBind(consumer, "arenas")

        Commands.create()
            .assertPlayer()
            .handler {
                val user = UserService.getUser(it.sender())
                user.currentArena?.leave(user)

            }
            .registerAndBind(consumer, "лив", "leave", "выход")
        Commands.create()
            .assertPlayer()
            .assertUsage("<arena>")
            .tabHandler { arenas() }
            .handler {
                val arena = it.arg(0).parseOrFail(Arena::class.java)
                val user = UserService.getUser(it.sender()) ?: return@handler
                arena.join(user)

            }
            .registerAndBind(consumer, "join")

    }


    private fun arenas(): List<String?> {
        return ArenaService.getArenas()
            .map { it.context.config.arenaId }

    }
}