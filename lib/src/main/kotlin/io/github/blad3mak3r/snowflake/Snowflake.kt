package io.github.blad3mak3r.snowflake

import kotlinx.coroutines.delay
import kotlinx.coroutines.yield
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Instant

class SnowflakeGenerator(
    private val datacenterId: Long,
    private val workerId: Long,
    val epochMillis: Long = DEFAULT_EPOCH_MILLIS,
    private val onClockBackwards: ClockBackwardsStrategy = ClockBackwardsStrategy.WAIT
) {
    init {
        require(datacenterId in 0..MAX_DATACENTER_ID) { "datacenterId must be between 0 and $MAX_DATACENTER_ID" }
        require(workerId in 0..MAX_WORKER_ID) { "workerId must be between 0 and $MAX_WORKER_ID" }
    }

    private val mutex = Mutex()
    private var lastTimestamp: Long = -1L
    private var sequence: Long = 0L

    companion object {
        const val SEQUENCE_BITS = 12
        const val WORKER_ID_BITS = 5
        const val DATACENTER_ID_BITS = 5
        const val TIMESTAMP_BITS = 41

        const val SEQUENCE_MASK = (1L shl SEQUENCE_BITS) - 1L

        const val MAX_WORKER_ID = (1L shl WORKER_ID_BITS) - 1L // 31
        const val MAX_DATACENTER_ID = (1L shl DATACENTER_ID_BITS) - 1L // 31

        const val WORKER_ID_SHIFT = SEQUENCE_BITS
        const val DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS
        const val TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS

        val DEFAULT_EPOCH_MILLIS: Long = Instant.parse("2025-01-01T00:00:00Z").toEpochMilli()
    }

    suspend fun nextId(): Long = mutex.withLock {
        var ts = System.currentTimeMillis()

        if (ts < lastTimestamp) {
            when (onClockBackwards) {
                ClockBackwardsStrategy.WAIT -> ts = waitUntil(lastTimestamp)
                ClockBackwardsStrategy.THROW -> throw IllegalStateException("Clock moved backwards")
                ClockBackwardsStrategy.IGNORE -> {}
            }
        }

        if (ts == lastTimestamp) {
            sequence = (sequence + 1L) and SEQUENCE_MASK
            if (sequence == 0L) {
                ts = waitUntil(lastTimestamp + 1)
            }
        } else {
            sequence = 0L
        }

        lastTimestamp = ts

        ((ts - epochMillis) shl TIMESTAMP_SHIFT) or
                (datacenterId shl DATACENTER_ID_SHIFT) or
                (workerId shl WORKER_ID_SHIFT) or
                sequence
    }

    private suspend fun waitUntil(target: Long): Long {
        var now = System.currentTimeMillis()
        while (now < target) {
            yield()
            delay(1)
            now = System.currentTimeMillis()
        }
        return now
    }

    fun extractTimestamp(id: Long): Long =
        ((id ushr TIMESTAMP_SHIFT) and ((1L shl TIMESTAMP_BITS) - 1)) + epochMillis

    fun extractDatacenterId(id: Long): Long =
        (id ushr DATACENTER_ID_SHIFT) and ((1L shl DATACENTER_ID_BITS) - 1)

    fun extractWorkerId(id: Long): Long =
        (id ushr WORKER_ID_SHIFT) and ((1L shl WORKER_ID_BITS) - 1)

    fun extractSequence(id: Long): Long =
        id and SEQUENCE_MASK
}


enum class ClockBackwardsStrategy { WAIT, THROW, IGNORE }

