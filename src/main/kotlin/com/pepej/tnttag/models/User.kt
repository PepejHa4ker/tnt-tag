package com.pepej.tnttag.models

import com.pepej.papi.utils.Players
import com.pepej.tnttag.api.Arena
import org.bukkit.entity.Player
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import java.util.*


object Users : IntIdTable("tt_users") {

    var uuid = varchar("uuid", 40)
    var username = varchar("username", 20)
    var wins = integer("wins").default(0)
    var games = integer("games").default(0)
    var kills = integer("kills").default(0)
    var tntCollected = integer("tnt_collected").default(0)
}

class UserEntity(id: EntityID<Int>) : IntEntity(id) {

    companion object :  IntEntityClass<UserEntity>(Users)
    var uuid by Users.uuid
    var username by Users.username
    val wins by Users.wins
    val games by Users.games
    val kills by Users.kills
    val tntCollected by Users.tntCollected


    fun toUser(): User {
        return User(
            uuid = UUID.fromString(uuid),
            username = username,
            wins = wins,
            games = games,
            kills = kills,
            tntCollected = tntCollected
        )
    }


}

data class User(val uuid: UUID, val username: String, var wins: Int = 0, var games: Int = 0, var kills: Int = 0, var tntCollected: Int = 0, var tagged: Boolean = false, var currentArena: Arena? = null) {


    fun asPlayer(): Player? = Players.getNullable(uuid)
}



