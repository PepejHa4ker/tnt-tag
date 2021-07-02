package com.pepej.tnttag.service

import com.pepej.papi.utils.Log
import com.pepej.tnttag.api.Arena

object ArenaService {

    private val arenas by lazy { hashSetOf<Arena>() }

    fun register(arena: Arena) {
        if (arenas.any { it.context.config.arenaId == arena.context.config.arenaId }) {
            Log.info("Arena with given id(${arena.context.config.arenaId} already registered")
            arena.stop()
            arenas.remove(arena)
        }
        Log.info("registering")
        arenas.add(arena)
        arena.start()
    }

    fun unregister(id: String?) {
        arenas.remove(getArena(id))
    }

    fun getArena(id: String?): Arena? {
        return arenas.find { it.context.config.arenaId == id }
    }

    fun getMostRelevantArena(): Arena? {
        return arenas.filter(Arena::isActualToJoin).maxByOrNull { it.context.users.size }

    }

    fun getArenas(): Set<Arena> {
        return arenas
    }

}