plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.maven.publish)

    `java-library`
    signing
}

repositories {
    mavenCentral()
}

dependencies {
    api(libs.bundles.coroutines)
}

val gitTagProvider = providers.exec {
    commandLine("git", "describe", "--tags", "--abbrev=0")
    workingDir = rootProject.projectDir
    isIgnoreExitValue = true
}.standardOutput.asText.map { it.trim() }.orNull

val gitHashProvider = providers.exec {
    commandLine("git", "rev-parse", "--short", "HEAD")
    workingDir = rootProject.projectDir
    isIgnoreExitValue = true
}.standardOutput.asText.map { it.trim() }.orNull

version = gitTagProvider ?: gitHashProvider ?: "dev"

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useKotlinTest("2.2.0")
        }
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

mavenPublishing {
    coordinates("io.github.blad3mak3r.snowflake", "snowflake-core", "$version")

    pom {
        name.set("snowflake-core")
        description.set("Advanced coroutines-based Snowflakes generator")
        url.set("https://github.com/Blad3Mak3r/snowflkae")
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