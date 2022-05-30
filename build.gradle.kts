import com.adarshr.gradle.testlogger.TestLoggerExtension
import com.adarshr.gradle.testlogger.theme.ThemeType
import com.example.gradle.plugin.jacocoextra.JacocoExtraPluginExtension
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jmailen.gradle.kotlinter.KotlinterExtension

plugins {
    kotlin("jvm") apply false
    id("io.spring.dependency-management") apply false
    id("org.jmailen.kotlinter") apply false
    id("com.adarshr.test-logger") apply false
    id("org.barfuin.gradle.jacocolog") apply false
}

val projectGroup: String by project
val projectVersion: String by project

val javaVersion: String by project
val springBootVersion: String by project
val springCloudVersion: String by project
val springdocOpenapiVersion: String by project
val kotlinLoggingVersion: String by project
val kotlinMockitoVersion: String by project
val testcontainersVersion: String by project

subprojects {
    if (skip) {
        afterEvaluate {
            deactivate()
        }
        return@subprojects
    }
    beforeEvaluate {
        group = projectGroup
        version = projectVersion

        apply(plugin = "org.jetbrains.kotlin.jvm")
        apply(plugin = "io.spring.dependency-management")
        apply(plugin = "org.jmailen.kotlinter")
        apply(plugin = "com.adarshr.test-logger")
        apply(plugin = "jacoco")
        apply(plugin = "org.barfuin.gradle.jacocolog")

        apply(plugin = "com.example.gradle.plugin.fullartifactname")
        apply(plugin = "com.example.gradle.plugin.jacocoextra")

        configure<DependencyManagementExtension> {
            dependencies {
                dependency("org.springdoc:springdoc-openapi-common:$springdocOpenapiVersion")
                dependency("org.springdoc:springdoc-openapi-data-rest:$springdocOpenapiVersion")
                dependency("org.springdoc:springdoc-openapi-hateoas:$springdocOpenapiVersion")
                dependency("org.springdoc:springdoc-openapi-kotlin:$springdocOpenapiVersion")
                dependency("org.springdoc:springdoc-openapi-security:$springdocOpenapiVersion")
                dependency("org.springdoc:springdoc-openapi-ui:$springdocOpenapiVersion")
                dependency("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion")
                dependency("org.mockito.kotlin:mockito-kotlin:$kotlinMockitoVersion")
            }
            imports {
                mavenBom("org.springframework.boot:spring-boot-dependencies:$springBootVersion")
                mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
                mavenBom("org.testcontainers:testcontainers-bom:$testcontainersVersion")
                mavenBom("org.junit:junit-bom:${managedVersions["org.junit.jupiter:junit-jupiter"]}")
            }
        }

        configure<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(javaVersion))
            }
        }

        configure<KotlinterExtension> {
            ignoreFailures = false
            reporters = arrayOf("checkstyle", "plain")
            experimentalRules = true
        }

        configure<TestLoggerExtension> {
            logLevel = LogLevel.LIFECYCLE
            theme = ThemeType.MOCHA_PARALLEL
            showFullStackTraces = false
            showSkipped = false
            showSimpleNames = true
        }

        configure<JacocoExtraPluginExtension> {
            excludes.add("**/generated/**/*")
            // https://github.com/microutils/kotlin-logging/wiki/coverage-reporting
            excludes.add("**/*\$logger\$*.class")
        }

        tasks.withType<KotlinCompile>().configureEach {
            kotlinOptions {
                freeCompilerArgs = listOf("-Xjsr305=strict")
            }
        }

        tasks.withType<Test>().configureEach {
            useJUnitPlatform()

            // each Test task can use specified number of forked jvm workers in parallel
            maxParallelForks = 2

            reports.html.required.set(false)
            reports.junitXml.required.set(false)

            configure<JacocoTaskExtension> {
                output = JacocoTaskExtension.Output.FILE
            }

            // hide spring boot shutdown info messages in tests
            systemProperty("logging.level.root", "warn")

            finalizedBy(tasks.named("jacocoTestReport"))
        }

        tasks.register("plugins") {
            group = "help"
            description = "Lists plugins applied"
            doLast {
                plugins.sortedBy { it.javaClass.canonicalName }.forEach { println(it) }
            }
        }
    }
}

val Project.skip get() = subprojects.isNotEmpty()

fun Project.deactivate() = tasks.configureEach { enabled = false }
