import org.jmailen.gradle.kotlinter.tasks.ConfigurableKtLintTask
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    kotlin("jvm")
    id("io.spring.dependency-management") apply false
    id("org.openapi.generator") apply false
}

val srcMainDir = "$projectDir/src/main"

subprojects {
    afterEvaluate {
        apply(plugin = "org.openapi.generator")

        dependencies {
            compileOnly("org.springframework.boot", "spring-boot-starter-validation")
            compileOnly("org.springframework.boot", "spring-boot-starter-web")
            compileOnly("org.springdoc", "springdoc-openapi-common")
        }

        sourceSets {
            main.configure {
                java {
                    srcDirs("$buildDir/generated/src/main/java")
                }
            }
        }

        tasks.withType<GenerateTask>().configureEach {
            group = "api generate"

            generatorName.set("spring")

            inputSpec.set("$srcMainDir/openapi/api.yaml")
            ignoreFileOverride.set("$srcMainDir/generator/.openapi-generator-ignore")
            outputDir.set("$buildDir/generated")

            generateApiTests.set(false)
            generateApiDocumentation.set(false)
            generateModelTests.set(false)
            generateModelDocumentation.set(false)
            skipOperationExample.set(true)

            configOptions.put("apiFirst", "false")
            configOptions.put("dateLibrary", "java8")
            configOptions.put("hideGenerationTimestamp", "true")
            configOptions.put("interfaceOnly", "true")
            configOptions.put("openApiNullable", "false")
            configOptions.put("skipDefaultInterface", "true")
            configOptions.put("title", rootProject.name)
            configOptions.put("unhandledException", "false")
            configOptions.put("useOptional", "true")
        }

        tasks.named("openApiGenerate").configure { enabled = false }
        tasks.withType<ConfigurableKtLintTask>().configureEach { enabled = false }
        tasks.compileKotlin.configure { dependsOn(tasks.withType<GenerateTask>()) }
    }
}
