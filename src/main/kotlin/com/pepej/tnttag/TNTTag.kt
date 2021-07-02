package com.pepej.tnttag

import com.pepej.papi.adventure.platform.bukkit.BukkitAudiences
import com.pepej.papi.plugin.PapiJavaPlugin
import com.pepej.papi.ap.Plugin
import com.pepej.papi.ap.PluginDependency
import com.pepej.papi.dependency.Dependencies
import com.pepej.papi.dependency.Dependency
import com.pepej.papi.utils.Log
import com.pepej.tnttag.arena.ArenaLoader
import com.pepej.tnttag.arena.ArenaLoaderImpl
import com.pepej.tnttag.commands.TTCommands
import com.pepej.tnttag.service.DatabaseFactory
import com.pepej.tnttag.service.UserService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@Plugin(name = "tnt-tag",
    version = "1.0.0",
    description = "TNT tag minecraft plugin",
    depends = [PluginDependency("papi")])
@Dependencies(
//    Dependency("org.jetbrains.kotlin:kotlin-stdlib:1.5.0"), // static initialization :((((((((
//    Dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0"),
//    Dependency("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.5.0"),
//    Dependency("org.jetbrains.exposed:exposed-core:0.32.1"),
//    Dependency("org.jetbrains.exposed:exposed-dao:0.32.1"),
    Dependency("org.jetbrains.exposed:exposed-jdbc:0.32.1"),
    Dependency("mysql:mysql-connector-java:8.0.25"),
    Dependency("com.zaxxer:HikariCP:3.4.5"),
)
class TNTTag : PapiJavaPlugin() {

    val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Unconfined)
    lateinit var audiences: BukkitAudiences

    companion object {
        lateinit var instance: TNTTag
            private set
    }


    override fun onPluginLoad() {
        instance = this

    }

    override fun onPluginEnable() {
        DatabaseFactory.initialize(loadConfigNode("mysql.json"))
        val arenaLoader = ArenaLoaderImpl(getBundledFile("arenas.json"))
        arenaLoader.loadAndRegisterAllArenas()
        audiences = bind(BukkitAudiences.create(this))
        bindModule(UserService)
        bindModule(TTCommands)
        Log.info("Enabled")

    }
}