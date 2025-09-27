package io.github.blad3mak3r.snowflake.exposed.dao

import io.github.blad3mak3r.snowflake.core.SnowflakeFactory
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable

open class SnowflakeIdTable(
    name: String,
    private val factory: SnowflakeFactory
) : IdTable<Long>(name) {

    final override val id: Column<EntityID<Long>> = long("id")
        .clientDefault { runBlocking { factory.nextId() } }
        .uniqueIndex()
        .entityId()

    override val primaryKey = PrimaryKey(id)
}
