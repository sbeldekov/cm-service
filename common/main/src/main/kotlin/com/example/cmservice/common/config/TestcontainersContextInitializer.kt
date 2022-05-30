package com.example.cmservice.common.config

import mu.KotlinLogging
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.Ordered
import org.springframework.core.env.MapPropertySource
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.Wait
import java.io.File

private val logger = KotlinLogging.logger {}

class TestcontainersContextInitializer : ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {
    override fun getOrder() = Ordered.LOWEST_PRECEDENCE

    override fun initialize(applicationContext: ConfigurableApplicationContext) =
        applicationContext
            .getResource("classpath:$DOCKER_COMPOSE_FILE_NAME")
            .also { if (it.exists().not()) error("$DOCKER_COMPOSE_FILE_NAME not found") }
            .file
            .let { dockerComposeFile ->
                val envId = randomAlphabetic(12, 12).lowercase()
                val dockerComposeContainer = TestDockerComposeContainer(dockerComposeFile).apply {
                    this.withExposedService(POSTGRES_SERVICE, POSTGRES_PORT)
                        .withLogConsumer(
                            POSTGRES_SERVICE,
                            Slf4jLogConsumer(LoggerFactory.getLogger(POSTGRES_SERVICE))
                                .withPrefix(POSTGRES_SERVICE)
                        )
                        .waitingFor(
                            POSTGRES_SERVICE,
                            Wait.forLogMessage(
                                ".*database system is ready to accept connections[\\s]*",
                                2
                            )
                        )
                        .withEnv("POSTGRES_DB", envId)
                        .withEnv("POSTGRES_USER", envId)
                        .withEnv("POSTGRES_PASSWORD", envId)
                        .start()
                }

                val pgHost = dockerComposeContainer.getServiceHost(POSTGRES_SERVICE, POSTGRES_PORT)
                val pgPort = dockerComposeContainer.getServicePort(POSTGRES_SERVICE, POSTGRES_PORT)
                val pgUrl = "jdbc:postgresql://$pgHost:$pgPort/$envId"
                logger.debug { ">>>>> Started $POSTGRES_SERVICE: $pgUrl" }

                applicationContext.environment.propertySources.addFirst(
                    MapPropertySource(
                        javaClass.simpleName,
                        mapOf(
                            "spring.datasource.username" to envId,
                            "spring.datasource.password" to envId,
                            "spring.datasource.url" to pgUrl
                        )
                    )
                )
                logger.debug { "${javaClass.simpleName} initialized" }
            }

    private companion object {
        private const val POSTGRES_SERVICE = "postgres_1"
        private const val POSTGRES_PORT = 5432
        private const val DOCKER_COMPOSE_FILE_NAME = "docker-compose.yaml"

        private class TestDockerComposeContainer(dockerComposeFile: File) :
            DockerComposeContainer<TestDockerComposeContainer>(dockerComposeFile)
    }
}
