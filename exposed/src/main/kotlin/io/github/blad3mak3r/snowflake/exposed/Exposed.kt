package io.github.blad3mak3r.snowflake.exposed

import io.github.blad3mak3r.snowflake.core.SnowflakeGenerator
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

@Suppress("unused")
fun Table.snowflake(name: String, generator: SnowflakeGenerator): Column<Long> {
    return long(name).clientDefault { runBlocking { generator.nextId() } }
}

@Suppress("unused")
fun ResultRow.snowflake(column: Column<Long>): Long = this[column]