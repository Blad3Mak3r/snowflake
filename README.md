## Instalación

### Artífices y ejemplos

- **Exposed DAO integration (opcional):**  
  `io.github.blad3mak3r.snowflake:snowflake-exposed-dao`

#### Gradle

    implementation("io.github.blad3mak3r.snowflake:snowflake-exposed-dao:1.0.0") // opcional

#### Maven

    <dependency>
      <groupId>io.github.blad3mak3r.snowflake</groupId>
      <artifactId>snowflake-exposed-dao</artifactId>
      <version>1.0.0</version>
    </dependency>

## Uso

### Exposed integration

#### Exposed DAO integration

```kotlin
import com.tuorg.snowflake.exposed.snowflake
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object Users : LongIdTable("users") {
    override val id = snowflake("id").entityId()
    val name = varchar("name", 50)
}

class User(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<User>(Users)
    var name by Users.name
}
```

Así se muestra cómo definir una tabla y una entidad usando el campo snowflake como ID con Exposed DAO.