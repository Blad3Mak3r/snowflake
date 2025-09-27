plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.maven.publish)

    // Apply the java-library plugin for API and implementation separation.
    `java-library`
    signing
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    compileOnly(project(":core"))
    compileOnly(project(":exposed"))

    compileOnly(libs.exposedCore)
    compileOnly(libs.exposedDao)
}

testing {
    suites {
        // Configure the built-in test suite
        val test by getting(JvmTestSuite::class) {
            // Use Kotlin Test test framework
            useKotlinTest("2.2.0")
        }
    }
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

mavenPublishing {
    coordinates("io.github.blad3mak3r.snowflake", "snowflake-exposed-dao", "$version")

    pom {
        name.set("snowflake-exposed-dao")
        description.set("Advanced coroutines-based Snowflakes generator")
        url.set("https://github.com/Blad3Mak3r/snowflake")
        issueManagement {
            system.set("GitHub")
            url.set("https://github.com/Blad3Mak3r/snowflake/issues")
        }
        licenses {
            license {
                name.set("Apache License 2.0")
                url.set("https://github.com/Blad3Mak3r/snowflake/LICENSE.txt")
                distribution.set("repo")
            }
        }
        scm {
            url.set("https://github.com/Blad3Mak3r/snowflake")
            connection.set("https://github.com/Blad3Mak3r/snowflake.git")
            developerConnection.set("scm:git:ssh://git@github.com:Blad3Mak3r/snowflake.git")
        }
        developers {
            developer {
                name.set("Juan Luis Caro")
                url.set("https://github.com/Blad3Mak3r")
            }
        }
    }

    publishToMavenCentral(automaticRelease = true)

    signAllPublications()
}
