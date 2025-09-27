package io.github.blad3mak3r.snowflake.exposed.core

import io.github.blad3mak3r.snowflake.core.SnowflakeFactory
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.Table

@Suppress("unused")
fun Table.snowflake(name: String, factory: SnowflakeFactory): Column<Long> {
    return long(name).clientDefault { runBlocking { factory.nextId() } }
}

@Suppress("unused")
fun ResultRow.snowflake(column: Column<Long>): Long = this[column]
