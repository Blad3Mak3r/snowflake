# Kotlin Snowflake Generator

A lightweight Kotlin library for generating unique, distributed Twitter-style Snowflake IDs with coroutine-safe concurrency.

## ✨ Features

- 🚀 **Coroutine-safe**: uses `Mutex` to prevent duplicate IDs under heavy concurrency.  
- 🏗 **Configurable node**: supports both `datacenterId` and `workerId` fields (5 bits each).  
- ⏱ **41-bit timestamp**: millisecond precision since a custom epoch.  
- 🔄 **12-bit sequence**: up to 4096 IDs per millisecond, per node.  
- 🗄 **Exposed integration**: separate module with JetBrains Exposed extensions (custom column types & helpers).  
- ✅ **Battle-tested**: includes concurrency and overflow tests.  
- 📦 **Multi-module**:  
  - `core`: the generator itself.  
  - `exposed`: database helpers for Exposed.  

Ideal for distributed systems, microservices, or databases requiring sortable, unique identifiers without relying on UUIDs.

---

## 📦 Installation

<details>
<summary>Gradle (Kotlin DSL)</summary>

```kotlin
dependencies {
    implementation("com.tuorg:snowflake-core:1.0.0")
    implementation("com.tuorg:snowflake-exposed:1.0.0") // optional
}
```
</details>

<details>
<summary>Maven</summary>

```xml
<dependency>
  <groupId>com.tuorg</groupId>
  <artifactId>snowflake-core</artifactId>
  <version>1.0.0</version>
</dependency>
<dependency>
  <groupId>com.tuorg</groupId>
  <artifactId>snowflake-exposed</artifactId>
  <version>1.0.0</version>
</dependency>
```
</details>

---

## 🚀 Usage

### Core

```kotlin
import com.tuorg.snowflake.SnowflakeGenerator

suspend fun main() {
    val generator = SnowflakeGenerator(datacenterId = 1, workerId = 2)
    val id = generator.nextId()

    println("Snowflake ID: $id")
    println("Timestamp: ${generator.extractTimestamp(id)}")
    println("Datacenter: ${generator.extractDatacenterId(id)}")
    println("Worker: ${generator.extractWorkerId(id)}")
    println("Sequence: ${generator.extractSequence(id)}")
}
```

### Exposed integration

```kotlin
import com.tuorg.snowflake.exposed.snowflake
import org.jetbrains.exposed.sql.Table

object Users : Table("users") {
    val id = snowflake("id")
    val name = varchar("name", 50)
}
```

---

## 🧪 Testing

Run the test suite:

```bash
./gradlew test
```

Tests include:
- Concurrency safety
- Monotonic ordering
- Sequence overflow handling
- Extraction of timestamp, datacenter, worker, and sequence

---

## 📜 License

MIT License © 2025
