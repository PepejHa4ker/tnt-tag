package com.pepej.tnttag.service

import com.pepej.papi.adventure.text.Component
import com.pepej.papi.adventure.title.Title
import com.pepej.papi.events.Events
import com.pepej.papi.terminable.TerminableConsumer
import com.pepej.papi.terminable.module.TerminableModule
import com.pepej.papi.text.Text.colorize
import com.pepej.tnttag.TNTTag.Companion.instance
import com.pepej.tnttag.models.User
import com.pepej.tnttag.models.UserEntity
import com.pepej.tnttag.models.Users
import com.pepej.tnttag.service.DatabaseFactory.query
import com.pepej.tnttag.utils.user
import kotlinx.coroutines.*
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.jetbrains.exposed.sql.update
import java.time.Duration

private val cachedUsers = mutableSetOf<User>()

object UserService : TerminableModule, MutableSet<User> by cachedUsers {

    private fun startUpdateTask(user: User) {
        instance.coroutineScope.launch {
            while (find { it.uuid == user.uuid } != null) {
                updateUser(user)
                delay(Duration.ofSeconds(30).toMillis())
            }
            cancel()
        }
    }

    fun getUser(player: Player): User {
        val user = find { it.uuid.toString() == player.uniqueId.toString() }
        return if (user != null) {
            user
        } else {
            loadUser(player)
            getUser(player)
        }
    }


    private fun loadUser(player: Player) {
        CoroutineScope(Dispatchers.IO).launch {
            var user = query {
                UserEntity.find {
                    Users.uuid eq player.uniqueId.toString()
                }.firstOrNull()?.toUser()
            }
            if (user == null) {
                user = User(player.uniqueId, player.name)
                query {
                    UserEntity.new {
                        uuid = user.uuid.toString()
                        username = user.username
                    }
                }
            }
            add(user)
        }
    }

    suspend fun updateUser(user: User) {
        query {
            Users.update(
                where = {
                    Users.uuid eq user.uuid.toString()
                },
                body = {
                    it[wins] = user.wins
                    it[games] = user.games
                    it[kills] = user.kills
                    it[tntCollected] = user.tntCollected
                }
            )
        }
    }

    override fun setup(consumer: TerminableConsumer) {
        Events.subscribe(PlayerJoinEvent::class.java)
            .handler {
                instance.coroutineScope.launch {
                    val user = it.player.user()
                    startUpdateTask(user)
                    add(user)
                }
            }
            .bindWith(consumer)
        Events.subscribe(PlayerQuitEvent::class.java)
            .handler {
                instance.coroutineScope.launch {
                    val user = it.player.user()
                    updateUser(user)
                    remove(user)
                }
            }
            .bindWith(consumer)
    }

    private fun prefixedMessage(message: String): String {
        return "&3[&eT&aT&3] &a $message"
    }

    private fun prefixedComponent(component: Component): Component {
        return Component.text(colorize("&3[&eT&aT&3] &a")).append(component)
    }

    fun sendMessage(user: User?, message: String) {
        user?.asPlayer()?.sendMessage(colorize(prefixedMessage(message)))
    }

    fun sendComponent(user: User?, component: Component) {
        val player = user?.asPlayer() ?: return
        instance.audiences.player(player).sendMessage(prefixedComponent(component))
    }

    fun sendTitle(
        user: User?,
        mainMessage: String,
        bottomMessage: String,
        fadeIn: Long = 300,
        stay: Long = 1000,
        fadeOut: Long = 500,
    ) {
        val player = user?.asPlayer() ?: return
        val title: Title = Title.title(
            Component.text(colorize(mainMessage)),
            Component.text(colorize(bottomMessage)),
            Title.Times.of(Duration.ofMillis(fadeIn), Duration.ofMillis(stay), Duration.ofMillis(fadeOut))
        )
        instance.audiences.player(player).showTitle(title)
    }

    fun sendTitle(
        user: User?,
        mainComponent: Component,
        bottomComponent: Component,
        fadeIn: Long = 300,
        stay: Long = 1000,
        fadeOut: Long = 500,
    ) {
        val player = user?.asPlayer() ?: return
        val title: Title = Title.title(
            mainComponent,
            bottomComponent,
            Title.Times.of(Duration.ofMillis(fadeIn), Duration.ofMillis(stay), Duration.ofMillis(fadeOut))
        )
        instance.audiences.player(player).showTitle(title)
    }
}