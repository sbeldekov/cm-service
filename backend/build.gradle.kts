plugins {
    kotlin("kapt")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    id("org.springframework.boot")
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
    testCompileOnly {
        extendsFrom(configurations.testAnnotationProcessor.get())
    }
}

dependencies {
    implementation(project(":api:server"))
    implementation(project(":common:main"))
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-batch")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-hateoas")
    // implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("org.springframework.data:spring-data-envers")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.liquibase:liquibase-core")
    implementation("org.springdoc:springdoc-openapi-data-rest")
    implementation("org.springdoc:springdoc-openapi-hateoas")
    implementation("org.springdoc:springdoc-openapi-kotlin")
    // implementation("org.springdoc:springdoc-openapi-security")
    implementation("org.springdoc:springdoc-openapi-ui")
    implementation("io.github.microutils:kotlin-logging-jvm")

    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("org.testcontainers:postgresql")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testAnnotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    kapt("org.springframework.boot:spring-boot-configuration-processor")
    kaptTest("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation(project(":api:client"))
    testImplementation(project(":common:test"))
    testImplementation(kotlin("test"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    // testImplementation("org.springframework.batch:spring-batch-test")
    // testImplementation("org.springframework.security:spring-security-test")
}

// do not generate plain jar, boot jar is enough
tasks.jar.configure { enabled = false }
