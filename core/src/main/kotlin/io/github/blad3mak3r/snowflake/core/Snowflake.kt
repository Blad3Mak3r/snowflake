package io.github.blad3mak3r.snowflake.core

data class Snowflake(
    val timestamp: Long,
    val datacenterId: Long,
    val workerId: Long,
    val sequence: Long
)

