pluginManagement {
    val kotlinVersion: String by settings
    val springBootVersion: String by settings
    val springDependencyManagementPluginVersion: String by settings
    val openapiGeneratorPluginVersion: String by settings
    val kotlinterPluginVersion: String by settings
    val testLoggerPluginVersion: String by settings
    val jacocologPluginVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.spring") version kotlinVersion
        kotlin("plugin.jpa") version kotlinVersion
        kotlin("kapt") version kotlinVersion

        id("org.springframework.boot") version springBootVersion
        id("io.spring.dependency-management") version springDependencyManagementPluginVersion
        id("org.openapi.generator") version openapiGeneratorPluginVersion
        id("org.jmailen.kotlinter") version kotlinterPluginVersion
        id("com.adarshr.test-logger") version testLoggerPluginVersion
        id("org.barfuin.gradle.jacocolog") version jacocologPluginVersion
    }
}

@Suppress("UnstableApiUsage")
// https://docs.gradle.org/current/userguide/declaring_repositories.html#sub:centralized-repository-declaration
dependencyResolutionManagement {
    repositories {
        maven { url = uri("https://repo.spring.io/milestone") }
        mavenCentral()
    }
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
}

rootProject.name = "cm-service"

include(
    "api:client", "api:server", "common:main", "common:test", "backend"
)
