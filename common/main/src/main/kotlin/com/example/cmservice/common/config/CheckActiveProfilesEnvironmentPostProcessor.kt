package com.example.cmservice.common.config

import org.springframework.boot.SpringApplication
import org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor
import org.springframework.boot.env.EnvironmentPostProcessor
import org.springframework.core.Ordered
import org.springframework.core.env.ConfigurableEnvironment

class CheckActiveProfilesEnvironmentPostProcessor : EnvironmentPostProcessor, Ordered {
    // ConfigDataEnvironmentPostProcessor sets active profiles
    override fun getOrder() = ConfigDataEnvironmentPostProcessor.ORDER + 1

    override fun postProcessEnvironment(environment: ConfigurableEnvironment, application: SpringApplication) {
        environment.activeProfiles.ifEmpty { error("No spring active profile set") }
    }
}
