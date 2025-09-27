package io.github.blad3mak3.snowflake.tests

import io.github.blad3mak3r.snowflake.core.SnowflakeFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SnowflakeTest {
    private val factory = SnowflakeFactory(
        datacenterId = 1,
        workerId = 2
    )

    @Test
    fun `generate single id`() = runBlocking {
        val id = factory.nextId()
        assertTrue(id > 0, "ID debe ser positivo")
    }

    @Test
    fun `ids should be unique under concurrency`() = runBlocking {
        val n = 50_000
        val concurrency = 200
        val results = mutableListOf<Long>()
        val queue = ConcurrentLinkedQueue<Long>()

        coroutineScope {
            repeat(concurrency) {
                launch(Dispatchers.Default) {
                    repeat(n / concurrency) {
                        queue.add(factory.nextId())
                    }
                }
            }
        }

        results.addAll(queue)

        assertEquals(n, results.size, "Debe generarse la cantidad correcta de IDs")
        assertEquals(n, results.toSet().size, "No debe haber IDs duplicados")
    }

    @Test
    fun `ids should be ordered`() = runBlocking {
        val ids = buildList {
            repeat(1_000) { add(factory.nextId()) }
        }
        assertTrue(ids == ids.sorted(), "Los IDs deben ser monotonicos")
    }

    @Test
    fun `extracted parts should match`() = runBlocking {
        val id = factory.nextId()

        val decoded = factory.decode(id)

        assertTrue(decoded.timestamp > 0, "timestamp debe ser positivo")
        assertEquals(1, decoded.datacenterId, "datacenterId debe ser 1")
        assertEquals(2, decoded.workerId, "workerId debe ser 2")
        assertTrue(decoded.sequence >= 0, "sequence debe ser >= 0")
    }

    @Test
    fun `sequence should overflow correctly`() = runBlocking {
        val ids = mutableListOf<Long>()
        repeat(5000) {
            ids.add(factory.nextId())
        }
        assertEquals(ids.size, ids.toSet().size, "Incluso tras overflow no debe haber duplicados")
    }
}
