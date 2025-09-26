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
  - `exposed-dao`: database helpers for Exposed DAO.

Ideal for distributed systems, microservices, or databases requiring sortable, unique identifiers without relying on UUIDs.

---

## 📦 Installation

This library is available via [Maven Central](https://central.sonatype.com/namespace/io.github.blad3mak3r.snowflake).

### Artifacts

| Module      | Artifact                                               |
| ----------- | ------------------------------------------------------ |
| Core        | `io.github.blad3mak3r.snowflake:snowflake-core`        |
| Exposed     | `io.github.blad3mak3r.snowflake:snowflake-exposed`     |
| Exposed DAO | `io.github.blad3mak3r.snowflake:snowflake-exposed-dao` |

<details>
<summary>Gradle (Version Catalog)</summary>

## libs.versions.toml
```toml
[versions]
snowdlake = "X.Y.Z"

[libraries]
snowflakeCore = { module = "io.github.blad3mak3r.snowflake:snowflake-core", version.ref = "snowflake" }
snowflakeExposed = { module = "io.github.blad3mak3r.snowflake:snowflake-exposed", version.ref = "snowflake" }
snowflakeExposedDao = { module = "io.github.blad3mak3r.snowflake:snowflake-exposed-dao", version.ref = "snowflake" }

[bundles]
snowflake = ["snowflakeCore", "snowflakeExposed", "snowflakeExposedDao"]
```

## build.gradle.kts
```kotlin
dependencies {
    implementation(libs.bundles.snowflake)
}
```

</details>

<details>
<summary>Gradle (Kotlin DSL)</summary>

## build.gradle.kts
```kotlin
dependencies {
    implementation("io.github.blad3mak3r.snowflake:snowflake-core:X.Y.Z")
    implementation("io.github.blad3mak3r.snowflake:snowflake-exposed:X.Y.Z") // optional
    implementation("io.github.blad3mak3r.snowflake:snowflake-exposed-dao:X.Y.Z") // optional
}
```
</details>

<details>
<summary>Maven</summary>

## pom.xml
```xml
<dependency>
  <groupId>io.github.blad3mak3r.snowflake</groupId>
  <artifactId>snowflake-core</artifactId>
  <version>X.Y.Z</version>
</dependency>
<dependency>
  <groupId>io.github.blad3mak3r.snowflake</groupId>
  <artifactId>snowflake-exposed</artifactId>
  <version>X.Y.Z</version>
</dependency>
<dependency>
  <groupId>io.github.blad3mak3r.snowflake</groupId>
  <artifactId>snowflake-exposed-dao</artifactId>
  <version>X.Y.Z</version>
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
