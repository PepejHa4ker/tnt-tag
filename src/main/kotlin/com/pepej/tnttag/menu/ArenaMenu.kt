package com.pepej.tnttag.menu

import com.pepej.papi.menu.Menu
import com.pepej.papi.menu.scheme.MenuScheme
import com.pepej.tnttag.service.ArenaService
import com.pepej.tnttag.utils.handle
import com.pepej.tnttag.utils.item
import com.pepej.tnttag.utils.unaryPlus
import org.bukkit.Material
import org.bukkit.entity.Player

class ArenaMenu(player: Player) : Menu(player, 3, "Выбор арены") {

    companion object {
        private val ARENA_SCHEME: MenuScheme = MenuScheme.create()
            .mask("001111100")
            .mask("001111100")
            .mask("001111100")
    }

    override fun redraw() {
        val pop = ARENA_SCHEME.newPopulator(this)

        ArenaService.getArenas().forEach { arena ->
            pop.accept(

                item(Material.COMPASS) {
                    displayName = +"&a${arena.context.config.arenaName}"
                }
                    .handle(arena::join)
            )
        }
    }
}