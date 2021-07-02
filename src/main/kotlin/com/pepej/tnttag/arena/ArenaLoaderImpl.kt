package com.pepej.tnttag.arena

import com.pepej.papi.Papi
import com.pepej.papi.config.ConfigFactory
import com.pepej.papi.scoreboard.Scoreboard
import com.pepej.papi.scoreboard.ScoreboardProvider
import com.pepej.papi.services.Services
import com.pepej.papi.utils.Log
import com.pepej.tnttag.api.Arena
import com.pepej.tnttag.service.ArenaService
import java.io.File

class ArenaLoaderImpl(private val file: File) : ArenaLoader {

    private val scoreboardProvider = Services.getNullable(ScoreboardProvider::class.java)


    override fun loadAndSaveArenaFromConfig(config: ArenaConfig) {
        val node = ConfigFactory.gson().load(file)
        val arenaConfigs = node.getList(ArenaConfig::class.java) ?: return
        arenaConfigs.removeIf { it.arenaId == config.arenaId }
        arenaConfigs.add(config)
        node.setList(ArenaConfig::class.java, arenaConfigs)
        ConfigFactory.gson().save(file, node)
        registerArenaFromConfig(config)

    }

    override fun loadAndRegisterAllArenas() {
        val arenaConfigs = ConfigFactory.gson().load(file)
            .getList(ArenaConfig::class.java) ?: throw RuntimeException("Arena file not found!")
        for (arenaConfig in arenaConfigs) {
            registerArenaFromConfig(arenaConfig)
        }
    }

    private fun registerArenaFromConfig(config: ArenaConfig) {
        Log.info("loading arena ${config.arenaId}")
//        val scoreboard: Scoreboard = scoreboardProvider?.scoreboard ?: return
        val world = Papi.worldNullable(config.arenaWorld) ?: return
        val newArena: Arena = ArenaImpl(ArenaImpl.ContextImpl(world, config = config))
        ArenaService.register(newArena)
    }

    override fun unloadArena(arenaId: String) {
        TODO("Not yet implemented")
    }
}