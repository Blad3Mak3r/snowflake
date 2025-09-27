package io.github.blad3mak3r.snowflake.exposed.core

import io.github.blad3mak3r.snowflake.core.SnowflakeGenerator
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.Table

@Suppress("unused")
fun Table.snowflake(name: String, generator: SnowflakeGenerator): Column<Long> {
    return long(name).clientDefault { runBlocking { generator.nextId() } }
}

@Suppress("unused")
fun ResultRow.snowflake(column: Column<Long>): Long = this[column]
