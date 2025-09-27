package io.github.blad3mak3r.snowflake.tests

import io.github.blad3mak3r.snowflake.core.SnowflakeFactory
import io.github.blad3mak3r.snowflake.exposed.core.snowflake
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.SchemaUtils
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExposedTest {
    companion object {
        private val factory = SnowflakeFactory(
            datacenterId = 1,
            workerId = 2
        )

        private val h2db = R2dbcDatabase.connect("r2dbc:h2:mem:///test")

        object TestTable : Table("test") {
            val id = snowflake("id", factory = factory)
            val name = varchar("name", 50)
        }
    }

    @Test
    fun `Test ID generation and ordering`() = runBlocking {
        suspendTransaction(db = h2db) {
            SchemaUtils.create(TestTable)
            TestTable.insert {
                it[name] = "First"
            }

            TestTable.insert {
                it[name] = "Second"
            }

            val results = TestTable.selectAll().orderBy(TestTable.id, SortOrder.ASC).toList()

            assertEquals(2, results.size, "Should have inserted 2 rows")

            val first = results[0].let { it[TestTable.id] to it[TestTable.name] }
            val second = results[1].let { it[TestTable.id] to it[TestTable.name] }

            assertEquals(first.second, "First", "First should be First")
            assertEquals(second.second, "Second", "Second should be Second")
            assertTrue(first.first < second.first, "First should be less than second")
        }
    }
}
