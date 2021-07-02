package com.pepej.tnttag.service

import com.pepej.papi.config.ConfigurationNode
import com.pepej.tnttag.models.Users
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    lateinit var database: Database

    fun initialize(node: ConfigurationNode) {
        database = Database.connect(hikari(node))
        transaction {
            SchemaUtils.create(Users)
        }
    }

    private fun hikari(node: ConfigurationNode): HikariDataSource {
        val config = HikariConfig().apply {
            driverClassName = "com.mysql.cj.jdbc.Driver"
            val database = node.node("database").string
            val address = node.node("address").string
            val port = node.node("port").int
            val unName = node.node("username").string
            val pwd = node.node("password").string
            jdbcUrl = "jdbc:mysql://$address:$port/$database"
            username = "$unName"
            password = "$pwd"
            addDataSourceProperty("cachePrepStmts", "true")
            addDataSourceProperty("prepStmtCacheSize", "250")
            addDataSourceProperty("prepStmtCacheSqlLimit", "2048")

            maximumPoolSize = 10


        }
        return HikariDataSource(config)
    }

    suspend fun <T> query(block: suspend () -> T): T = newSuspendedTransaction(Dispatchers.IO, database) {
        block()
    }
}