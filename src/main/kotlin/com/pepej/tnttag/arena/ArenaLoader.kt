package com.pepej.tnttag.arena

interface ArenaLoader {

    fun unloadArena(arenaId: String)

    fun loadAndSaveArenaFromConfig(config: ArenaConfig)

    fun loadAndRegisterAllArenas();

}