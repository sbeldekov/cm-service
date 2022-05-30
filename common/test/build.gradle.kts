dependencies {
    compileOnly(kotlin("stdlib-jdk8"))
    compileOnly("org.springframework.boot", "spring-boot-starter-test")
    api("org.awaitility", "awaitility-kotlin")
    api("org.mockito.kotlin", "mockito-kotlin")
    api("org.mockito", "mockito-inline")
}
