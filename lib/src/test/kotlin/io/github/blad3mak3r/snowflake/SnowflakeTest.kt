package io.github.blad3mak3r.snowflake

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SnowflakeTest {
    private val generator = SnowflakeGenerator(
        datacenterId = 1,
        workerId = 2
    )

    @Test
    fun `generate single id`() = runBlocking {
        val id = generator.nextId()
        assertTrue(id > 0, "ID debe ser positivo")
    }

    @Test
    fun `ids should be unique under concurrency`() = runBlocking {
        val n = 50_000
        val concurrency = 200
        val results = mutableListOf<Long>()
        val queue = java.util.concurrent.ConcurrentLinkedQueue<Long>()

        coroutineScope {
            repeat(concurrency) {
                launch(Dispatchers.Default) {
                    repeat(n / concurrency) {
                        queue.add(generator.nextId())
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
            repeat(1_000) { add(generator.nextId()) }
        }
        assertTrue(ids == ids.sorted(), "Los IDs deben ser monotonicos")
    }

    @Test
    fun `extracted parts should match`() = runBlocking {
        val id = generator.nextId()

        val timestamp = generator.extractTimestamp(id)
        val datacenter = generator.extractDatacenterId(id)
        val worker = generator.extractWorkerId(id)
        val sequence = generator.extractSequence(id)

        assertTrue(timestamp > 0, "timestamp debe ser positivo")
        assertEquals(1, datacenter, "datacenterId debe ser 1")
        assertEquals(2, worker, "workerId debe ser 2")
        assertTrue(sequence >= 0, "sequence debe ser >= 0")
    }

    @Test
    fun `sequence should overflow correctly`() = runBlocking {
        val ids = mutableListOf<Long>()
        repeat(5000) {
            ids.add(generator.nextId())
        }
        assertEquals(ids.size, ids.toSet().size, "Incluso tras overflow no debe haber duplicados")
    }
}
