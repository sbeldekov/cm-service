package com.example.cmservice.common.config

import mu.KotlinLogging
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.Ordered
import org.springframework.core.env.MapPropertySource
import kotlin.random.Random

private val logger = KotlinLogging.logger {}

class RandomPortContextInitializer : ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {
    override fun getOrder() = Ordered.LOWEST_PRECEDENCE

    override fun initialize(applicationContext: ConfigurableApplicationContext) =
        applicationContext.environment.propertySources.addFirst(
            MapPropertySource(
                javaClass.simpleName,
                Random.nextInt(MIN_PORT, MAX_PORT + 1).let { port ->
                    mapOf(
                        "server.port" to port,
                        "local.server.url" to "localhost:$port"
                    )
                }
            )
        ).also { logger.debug { "${javaClass.simpleName} initialized" } }

    private companion object {
        private const val MIN_PORT = 49152
        private const val MAX_PORT = 65535
    }
}
